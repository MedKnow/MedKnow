package com.yaozhidao.service;

import com.yaozhidao.common.BizException;
import com.yaozhidao.common.ResultCode;
import com.yaozhidao.common.ValidationUtil;
import com.yaozhidao.dto.request.PlanDrugRequest;
import com.yaozhidao.dto.request.PlanUpsertRequest;
import com.yaozhidao.dto.response.ConflictItem;
import com.yaozhidao.dto.response.PlanCreateResponse;
import com.yaozhidao.dto.response.PlanDetailResponse;
import com.yaozhidao.dto.response.PlanListResponse;
import com.yaozhidao.dto.response.PlanStateResponse;
import com.yaozhidao.entity.Drug;
import com.yaozhidao.entity.MedicationPlan;
import com.yaozhidao.entity.PlanDrug;
import com.yaozhidao.entity.Reminder;
import com.yaozhidao.entity.User;
import com.yaozhidao.mapper.DrugMapper;
import com.yaozhidao.mapper.MedicationPlanMapper;
import com.yaozhidao.mapper.PlanDrugMapper;
import com.yaozhidao.mapper.ReminderMapper;
import com.yaozhidao.mapper.UserMapper;
import com.yaozhidao.security.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 用药计划服务（核心模块）
 * 状态机：DRAFT → ACTIVE → PAUSED ↔ ACTIVE；过期 → COMPLETED/EXPIRED
 * 冲突检测只提示不拦截（PRD 行为：创建永远成功，风险打在 drugs 标记里）
 */
@Service
public class MedicationPlanService {

    private static final Logger log = LoggerFactory.getLogger(MedicationPlanService.class);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    /** 剂量解析：数字 + 单位 */
    private static final Pattern DOSAGE_PATTERN = Pattern.compile("^([0-9]*\\.?[0-9]+)\\s*([a-zA-Z一-龥]+)$");
    /** 重量单位 ↔ 毫克换算 */
    private static final long MG_PER_G = 1000;
    private static final Set<String> WEIGHT_UNITS = Set.of("mg", "g");

    private final MedicationPlanMapper planMapper;
    private final PlanDrugMapper planDrugMapper;
    private final ReminderMapper reminderMapper;
    private final DrugMapper drugMapper;
    private final UserMapper userMapper;

    /**
     * 冲突 JSON 序列化用 Jackson2 ObjectMapper（静态实例，不依赖 Spring bean）。
     * 注意：Spring Boot 4 自动配置的是 Jackson3（tools.jackson），不提供 Jackson2 的 ObjectMapper bean
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public MedicationPlanService(MedicationPlanMapper planMapper,
                                 PlanDrugMapper planDrugMapper,
                                 ReminderMapper reminderMapper,
                                 DrugMapper drugMapper,
                                 UserMapper userMapper) {
        this.planMapper = planMapper;
        this.planDrugMapper = planDrugMapper;
        this.reminderMapper = reminderMapper;
        this.drugMapper = drugMapper;
        this.userMapper = userMapper;
    }

    /** 创建用药计划（草稿状态） */
    @Transactional
    public PlanCreateResponse create(PlanUpsertRequest req) {
        Long userId = UserContext.requireUserId();
        User user = userMapper.findById(userId);
        // PENDING（信息不完善）用户不允许创建计划（文档 40301）
        if (user == null || !"NORMAL".equals(user.getStatus())) {
            throw new BizException(ResultCode.PROFILE_INCOMPLETE);
        }

        // 基础校验
        validateRequest(req);
        LocalDate startDate = LocalDate.parse(req.getStartDate());
        LocalDate endDate = LocalDate.parse(req.getEndDate());
        if (startDate.isBefore(LocalDate.now())) {
            throw new BizException(ResultCode.PARAM_DATE, "开始日期不能早于今天");
        }

        // 逐药校验（verified / dosageRisk）+ 冲突检测
        List<PlanDrug> planDrugs = buildPlanDrugs(req);
        List<ConflictItem> conflicts = detectConflicts(req.getDrugs());

        // 插入计划（草稿）
        MedicationPlan plan = new MedicationPlan();
        plan.setUserId(userId);
        plan.setStatus("DRAFT");
        plan.setStartDate(startDate);
        plan.setEndDate(endDate);
        plan.setNotes(req.getNotes());
        plan.setDiagnosis(req.getDiagnosis());
        plan.setReminderMethods(String.join(",", req.getReminderMethods()));
        plan.setConflicts(toJson(conflicts));
        planMapper.insert(plan);

        for (int i = 0; i < planDrugs.size(); i++) {
            planDrugs.get(i).setPlanId(plan.getId());
            planDrugs.get(i).setSortNo(i);
        }
        planDrugMapper.insertBatch(planDrugs);

        // 组装响应
        PlanCreateResponse resp = new PlanCreateResponse();
        resp.setPlanId(plan.getId());
        resp.setStatus(plan.getStatus());
        resp.setStartDate(plan.getStartDate());
        resp.setEndDate(plan.getEndDate());
        resp.setNotes(plan.getNotes());
        resp.setDiagnosis(plan.getDiagnosis());
        resp.setReminderMethods(req.getReminderMethods());
        resp.setDrugs(planDrugs.stream().map(d -> new PlanCreateResponse.DrugResult(
                d.getDrugId(), d.getDrugName(), d.getVerified(), d.getDosageRisk()))
                .collect(Collectors.toList()));
        resp.setConflicts(conflicts);
        return resp;
    }

    /** 计划列表（分页 + 状态筛选） */
    public PlanListResponse list(String status, Integer page, Integer size) {
        Long userId = UserContext.requireUserId();
        if (status != null && !status.isBlank()) {
            Set<String> valid = Set.of("DRAFT", "ACTIVE", "PAUSED", "COMPLETED", "EXPIRED");
            if (!valid.contains(status)) {
                throw new BizException(ResultCode.PARAM_STATUS);
            }
        }
        // 查询前先做计划状态翻转（懒更新）
        planMapper.expireOverduePlans();

        int[] p = ValidationUtil.normalizePage(page, size);
        List<MedicationPlan> plans = planMapper.findByUserId(userId, status, (p[0] - 1) * p[1], p[1]);
        long total = planMapper.countByUserId(userId, status);

        PlanListResponse resp = new PlanListResponse();
        resp.setTotal(total);
        resp.setPageNum(p[0]);
        resp.setPageSize(p[1]);
        resp.setPlans(plans.stream().map(plan -> {
            PlanListResponse.ListItem item = new PlanListResponse.ListItem();
            item.setPlanId(plan.getId());
            item.setStatus(plan.getStatus());
            item.setStartDate(plan.getStartDate());
            item.setEndDate(plan.getEndDate());
            item.setNotes(plan.getNotes());
            item.setReminderMethods(splitMethods(plan.getReminderMethods()));

            List<PlanDrug> drugs = planDrugMapper.findByPlanId(plan.getId());
            item.setDrugCount(drugs.size());
            if (!drugs.isEmpty()) {
                String first = drugs.get(0).getDrugName();
                item.setMainDrugName(drugs.size() > 1 ? first + "等" + drugs.size() + "种" : first);
            }
            item.setAdherenceRate(adherenceRate(plan.getId()));
            return item;
        }).collect(Collectors.toList()));
        return resp;
    }

    /** 计划详情（含今日提醒） */
    public PlanDetailResponse detail(Long planId) {
        Long userId = UserContext.requireUserId();
        MedicationPlan plan = getOwnedPlan(planId, userId);

        reminderMapper.triggerDueReminders();
        reminderMapper.expireOverdueReminders();

        PlanDetailResponse resp = new PlanDetailResponse();
        resp.setPlanId(plan.getId());
        resp.setStatus(plan.getStatus());
        resp.setStartDate(plan.getStartDate());
        resp.setEndDate(plan.getEndDate());
        resp.setNotes(plan.getNotes());
        resp.setDiagnosis(plan.getDiagnosis());
        resp.setAdherenceRate(adherenceRate(planId));
        resp.setReminderMethods(splitMethods(plan.getReminderMethods()));
        resp.setDrugs(planDrugMapper.findByPlanId(planId).stream().map(d -> {
            PlanDetailResponse.DrugItem item = new PlanDetailResponse.DrugItem();
            item.setDrugId(d.getDrugId());
            item.setDrugName(d.getDrugName());
            item.setDosage(d.getDosage());
            item.setFrequency(d.getFrequency());
            item.setTakeTime(d.getTakeTime());
            item.setTakeMethod(d.getTakeMethod());
            item.setDietaryRestrictions(d.getDietaryRestrictions());
            item.setVerified(d.getVerified());
            item.setDosageRisk(d.getDosageRisk());
            return item;
        }).collect(Collectors.toList()));
        resp.setConflicts(fromJson(plan.getConflicts()));
        resp.setTodayReminders(reminderMapper.findByPlanAndDate(planId, LocalDate.now()).stream().map(r -> {
            PlanDetailResponse.TodayReminderItem item = new PlanDetailResponse.TodayReminderItem();
            item.setReminderId(r.getId());
            item.setDrugName(r.getDrugName());
            item.setScheduledTime(LocalDateTime.of(r.getScheduledDate(), r.getScheduledTime()));
            item.setStatus(r.getStatus());
            return item;
        }).collect(Collectors.toList()));
        return resp;
    }

    /** 更新计划（仅 DRAFT/PAUSED 可更新） */
    @Transactional
    public void update(Long planId, PlanUpsertRequest req) {
        Long userId = UserContext.requireUserId();
        MedicationPlan plan = getOwnedPlan(planId, userId);
        if (!"DRAFT".equals(plan.getStatus()) && !"PAUSED".equals(plan.getStatus())) {
            throw new BizException(ResultCode.STATE_CONFLICT, "仅草稿或暂停状态的计划可更新");
        }

        validateRequest(req);
        List<PlanDrug> planDrugs = buildPlanDrugs(req);
        List<ConflictItem> conflicts = detectConflicts(req.getDrugs());

        plan.setStartDate(LocalDate.parse(req.getStartDate()));
        plan.setEndDate(LocalDate.parse(req.getEndDate()));
        plan.setNotes(req.getNotes());
        plan.setDiagnosis(req.getDiagnosis());
        plan.setReminderMethods(String.join(",", req.getReminderMethods()));
        plan.setConflicts(toJson(conflicts));
        planMapper.updatePlan(plan);

        // 删旧重插
        planDrugMapper.deleteByPlanId(planId);
        for (int i = 0; i < planDrugs.size(); i++) {
            planDrugs.get(i).setPlanId(planId);
            planDrugs.get(i).setSortNo(i);
        }
        planDrugMapper.insertBatch(planDrugs);
    }

    /** 激活计划：批量生成提醒（幂等） */
    @Transactional
    public PlanStateResponse activate(Long planId) {
        Long userId = UserContext.requireUserId();
        MedicationPlan plan = getOwnedPlan(planId, userId);

        if (!"DRAFT".equals(plan.getStatus()) && !"EXPIRED".equals(plan.getStatus())
                && !"PAUSED".equals(plan.getStatus())) {
            throw new BizException(ResultCode.CANNOT_ACTIVATE);
        }
        // 提醒方式兜底校验
        if (plan.getReminderMethods() == null || plan.getReminderMethods().isBlank()) {
            throw new BizException(ResultCode.PARAM_COMMON, "请至少选择一种提醒方式");
        }

        // 批量生成提醒：开始日→结束日 × 每药 × 每时间点（唯一键 uk_drug_time 兜底幂等，重复激活不产生双份）
        List<PlanDrug> drugs = planDrugMapper.findByPlanId(planId);
        List<Reminder> reminders = new ArrayList<>();
        long days = ChronoUnit.DAYS.between(plan.getStartDate(), plan.getEndDate()) + 1;
        for (int i = 0; i < days; i++) {
            LocalDate date = plan.getStartDate().plusDays(i);
            for (PlanDrug drug : drugs) {
                for (String t : drug.getTakeTime().split(",")) {
                    LocalTime time = LocalTime.parse(t.trim(), TIME_FMT);
                    Reminder r = new Reminder();
                    r.setPlanId(planId);
                    r.setUserId(userId);
                    r.setPlanDrugId(drug.getId());
                    r.setDrugName(drug.getDrugName());
                    r.setScheduledDate(date);
                    r.setScheduledTime(time);
                    r.setScheduledAt(LocalDateTime.of(date, time));
                    reminders.add(r);
                }
            }
        }
        if (!reminders.isEmpty()) {
            reminderMapper.insertBatch(reminders);
        }
        log.info("计划 {} 激活，生成 {} 条提醒", planId, reminders.size());

        LocalDateTime now = LocalDateTime.now();
        planMapper.updateStatus(planId, "ACTIVE", now, null);

        PlanStateResponse resp = new PlanStateResponse();
        resp.setPlanId(planId);
        resp.setStatus("ACTIVE");
        resp.setActivatedAt(now);
        return resp;
    }

    /** 暂停（仅 ACTIVE） */
    @Transactional
    public PlanStateResponse pause(Long planId) {
        Long userId = UserContext.requireUserId();
        MedicationPlan plan = getOwnedPlan(planId, userId);
        if (!"ACTIVE".equals(plan.getStatus())) {
            throw new BizException(ResultCode.CANNOT_PAUSE);
        }
        LocalDateTime now = LocalDateTime.now();
        planMapper.updateStatus(planId, "PAUSED", null, now);

        PlanStateResponse resp = new PlanStateResponse();
        resp.setPlanId(planId);
        resp.setStatus("PAUSED");
        resp.setPausedAt(now);
        return resp;
    }

    /** 恢复（仅 PAUSED） */
    @Transactional
    public PlanStateResponse resume(Long planId) {
        Long userId = UserContext.requireUserId();
        MedicationPlan plan = getOwnedPlan(planId, userId);
        if (!"PAUSED".equals(plan.getStatus())) {
            throw new BizException(ResultCode.CANNOT_RESUME);
        }
        LocalDateTime now = LocalDateTime.now();
        planMapper.updateStatus(planId, "ACTIVE", now, null);

        PlanStateResponse resp = new PlanStateResponse();
        resp.setPlanId(planId);
        resp.setStatus("ACTIVE");
        resp.setResumedAt(now);
        return resp;
    }

    // ---------- 私有方法 ----------

    /** 取归属当前用户的计划，不存在 40401 / 非本人 40302 */
    private MedicationPlan getOwnedPlan(Long planId, Long userId) {
        MedicationPlan plan = planMapper.findById(planId);
        if (plan == null) {
            throw new BizException(ResultCode.PLAN_NOT_FOUND);
        }
        if (!plan.getUserId().equals(userId)) {
            throw new BizException(ResultCode.FORBIDDEN_PLAN);
        }
        return plan;
    }

    /** 请求基础校验（日期/时间/提醒方式） */
    private void validateRequest(PlanUpsertRequest req) {
        if (req.getDrugs() == null || req.getDrugs().isEmpty()) {
            throw new BizException(ResultCode.PARAM_COMMON, "药品清单不能为空");
        }
        LocalDate startDate = ValidationUtil.parseDate(req.getStartDate());
        LocalDate endDate = ValidationUtil.parseDate(req.getEndDate());
        if (startDate == null || endDate == null) {
            throw new BizException(ResultCode.PARAM_DATE, "日期格式应为 yyyy-MM-dd");
        }
        if (!endDate.isAfter(startDate)) {
            throw new BizException(ResultCode.PARAM_DATE, "结束日期不能早于开始日期");
        }
        if (req.getReminderMethods() == null || req.getReminderMethods().isEmpty()) {
            throw new BizException(ResultCode.PARAM_COMMON, "请至少选择一种提醒方式");
        }
        for (PlanDrugRequest d : req.getDrugs()) {
            if (d.getDrugName() == null || d.getDrugName().trim().isEmpty()) {
                throw new BizException(ResultCode.PARAM_COMMON, "药品名称不能为空");
            }
            if (d.getDrugName().length() < 2 || d.getDrugName().length() > 50) {
                throw new BizException(ResultCode.PARAM_COMMON, "药品名称长度需在2~50个字符之间");
            }
            if (d.getTakeTime() == null || d.getTakeTime().isBlank()) {
                throw new BizException(ResultCode.PARAM_COMMON, "服药时间不能为空");
            }
            for (String t : d.getTakeTime().split(",")) {
                if (!ValidationUtil.isValidTakeTime(t.trim())) {
                    throw new BizException(ResultCode.PARAM_COMMON,
                            "服药时间格式错误，需为 HH:mm 且落在 06:00~23:00 之间");
                }
            }
            Set<String> methods = Set.of("BEFORE_MEAL", "AFTER_MEAL", "EMPTY_STOMACH", "BEFORE_SLEEP");
            if (!methods.contains(d.getTakeMethod())) {
                throw new BizException(ResultCode.PARAM_COMMON, "服用方式取值不合法");
            }
        }
    }

    /** 逐药构建 PlanDrug：匹配药品库 verified + 剂量校验 dosageRisk */
    private List<PlanDrug> buildPlanDrugs(PlanUpsertRequest req) {
        List<PlanDrug> result = new ArrayList<>();
        for (PlanDrugRequest d : req.getDrugs()) {
            PlanDrug pd = new PlanDrug();
            pd.setDrugName(d.getDrugName().trim());
            pd.setDosage(d.getDosage());
            pd.setFrequency(d.getFrequency());
            pd.setTakeTime(d.getTakeTime());
            pd.setTakeMethod(d.getTakeMethod());
            pd.setDietaryRestrictions(d.getDietaryRestrictions());

            // 匹配药品库（精确优先，模糊兜底）
            Drug matched = null;
            List<Drug> candidates = drugMapper.findByName(pd.getDrugName());
            for (Drug c : candidates) {
                if (c.getDrugName().equals(pd.getDrugName()) || pd.getDrugName().equals(c.getGenericName())) {
                    matched = c;
                    break;
                }
            }
            if (matched == null && !candidates.isEmpty()) {
                matched = candidates.get(0);
            }
            if (matched != null) {
                pd.setDrugId(matched.getId());
                pd.setVerified(true);
                pd.setDosageRisk(isDosageOverLimit(d.getDosage(), matched));
            } else {
                pd.setDrugId(null);
                pd.setVerified(false);
                pd.setDosageRisk(false);
            }
            result.add(pd);
        }
        return result;
    }

    /** 剂量超限校验：单位同为重量（mg/g）或同为片/粒/支时可比，否则不判风险 */
    private boolean isDosageOverLimit(String dosage, Drug drug) {
        if (drug.getMaxSingleDose() == null || drug.getMaxDoseUnit() == null) {
            return false;
        }
        Matcher m = DOSAGE_PATTERN.matcher(dosage.trim());
        if (!m.matches()) {
            return false;
        }
        try {
            BigDecimal value = new BigDecimal(m.group(1));
            String unit = m.group(2);
            BigDecimal maxValue = drug.getMaxSingleDose();
            String maxUnit = drug.getMaxDoseUnit();

            // 重量单位互转：统一换算成 mg
            if (WEIGHT_UNITS.contains(unit) && WEIGHT_UNITS.contains(maxUnit)) {
                BigDecimal mine = "g".equals(unit) ? value.multiply(BigDecimal.valueOf(MG_PER_G)) : value;
                BigDecimal theirs = "g".equals(maxUnit) ? maxValue.multiply(BigDecimal.valueOf(MG_PER_G)) : maxValue;
                return mine.compareTo(theirs) > 0;
            }
            // 同种非重量单位（片/粒/支/ml 等）直接比数值
            if (unit.equals(maxUnit)) {
                return value.compareTo(maxValue) > 0;
            }
            // 不可比（如 片 vs g）：不判定风险
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 冲突检测：
     * INTERVAL — 两药服药时间点相差 <2 小时
     * CONTRAINDICATION — 静态禁忌映射表命中
     */
    private List<ConflictItem> detectConflicts(List<PlanDrugRequest> drugs) {
        List<ConflictItem> conflicts = new ArrayList<>();
        for (int i = 0; i < drugs.size(); i++) {
            for (int j = i + 1; j < drugs.size(); j++) {
                PlanDrugRequest a = drugs.get(i);
                PlanDrugRequest b = drugs.get(j);

                // 1. 时间间隔冲突
                List<LocalTime> ta = parseTimes(a.getTakeTime());
                List<LocalTime> tb = parseTimes(b.getTakeTime());
                boolean intervalHit = false;
                for (LocalTime x : ta) {
                    for (LocalTime y : tb) {
                        long diffMin = Math.abs(ChronoUnit.MINUTES.between(x, y));
                        if (diffMin > 12 * 60) { // 跨日边界（如 22:00 vs 06:00）
                            diffMin = 24 * 60 - diffMin;
                        }
                        if (diffMin < 120) {
                            intervalHit = true;
                            break;
                        }
                    }
                    if (intervalHit) {
                        break;
                    }
                }
                if (intervalHit) {
                    conflicts.add(new ConflictItem(List.of(a.getDrugName(), b.getDrugName()), "INTERVAL",
                            "「" + a.getDrugName() + "」与「" + b.getDrugName()
                                    + "」服药时间间隔不足2小时，建议错开服用"));
                }

                // 2. 配伍禁忌
                if (ConflictRule.isContraindicated(a.getDrugName(), b.getDrugName())
                        || ConflictRule.isContraindicated(b.getDrugName(), a.getDrugName())) {
                    conflicts.add(new ConflictItem(List.of(a.getDrugName(), b.getDrugName()), "CONTRAINDICATION",
                            "「" + a.getDrugName() + "」与「" + b.getDrugName() + "」存在配伍禁忌，请咨询药师"));
                }
            }
        }
        return conflicts;
    }

    private List<LocalTime> parseTimes(String takeTime) {
        return Arrays.stream(takeTime.split(","))
                .map(t -> LocalTime.parse(t.trim(), TIME_FMT))
                .collect(Collectors.toList());
    }

    /** 计划依从率：TAKEN / 总提醒数 × 100（整数百分比） */
    private Integer adherenceRate(Long planId) {
        java.util.Map<String, Object> stat = reminderMapper.statsByPlan(planId);
        long total = ((Number) stat.getOrDefault("totalDoses", 0)).longValue();
        long confirmed = ((Number) stat.getOrDefault("confirmedDoses", 0)).longValue();
        if (total == 0) {
            return 0;
        }
        return (int) Math.round(confirmed * 100.0 / total);
    }

    private List<String> splitMethods(String methods) {
        return methods == null || methods.isBlank() ? List.of()
                : Arrays.stream(methods.split(",")).map(String::trim).collect(Collectors.toList());
    }

    private String toJson(List<ConflictItem> conflicts) {
        try {
            return OBJECT_MAPPER.writeValueAsString(conflicts);
        } catch (Exception e) {
            return "[]";
        }
    }

    @SuppressWarnings("unchecked")
    private List<ConflictItem> fromJson(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return OBJECT_MAPPER.readValue(json, OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, ConflictItem.class));
        } catch (Exception e) {
            return List.of();
        }
    }
}
