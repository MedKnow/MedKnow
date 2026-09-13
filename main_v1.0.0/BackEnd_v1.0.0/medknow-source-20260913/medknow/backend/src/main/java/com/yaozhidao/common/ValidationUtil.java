package com.yaozhidao.common;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * 手动校验工具（PRD 4.3.1 输入与数据校验规则）
 */
public final class ValidationUtil {

    private ValidationUtil() {
    }

    /** 中国大陆手机号：11 位，1 开头，第二位 3-9 */
    private static final Pattern PHONE = Pattern.compile("^1[3-9]\\d{9}$");

    /** 药品/搜索关键词白名单：中文、英文、数字、·、-、() 全角半角括号 */
    private static final Pattern KEYWORD = Pattern.compile("^[\\u4e00-\\u9fa5A-Za-z0-9·\\-()（）]+$");

    /** SQL 关键字黑名单（大小写不敏感） */
    private static final String[] SQL_KEYWORDS = {
            "select", "insert", "update", "delete", "drop", "union", "alter", "truncate", "exec", "declare",
            "--", "/*", "*/", "=", ";", "<", ">", "'", "\"", " and ", " or ", " where ", " from "
    };

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /** 手机号校验 */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE.matcher(phone).matches();
    }

    /**
     * 搜索关键词校验（PRD：长度 2~50 字；仅允许中文/英文/数字/常用符号；禁止 SQL 关键字及 HTML 标签）
     * 通过返回 null，不通过返回具体提示
     */
    public static String validateKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return "搜索关键词不能为空";
        }
        String kw = keyword.trim();
        if (kw.length() < 2 || kw.length() > 50) {
            return "搜索关键词长度需在2~50个字符之间";
        }
        if (!KEYWORD.matcher(kw).matches()) {
            return "搜索内容包含不规范字符";
        }
        String lower = kw.toLowerCase();
        for (String k : SQL_KEYWORDS) {
            if (lower.contains(k.trim())) {
                return "搜索内容包含不规范字符";
            }
        }
        return null;
    }

    /** 服药时间点：HH:mm 且落在 06:00~23:00 */
    public static boolean isValidTakeTime(String time) {
        if (time == null) {
            return false;
        }
        try {
            LocalTime t = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
            return !t.isBefore(LocalTime.of(6, 0)) && !t.isAfter(LocalTime.of(23, 0));
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /** 日期：yyyy-MM-dd 且可解析 */
    public static LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, DATE_FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** 分页参数归一化：page>=1，size 1~20，默认 page=1 size=10 */
    public static int[] normalizePage(Integer page, Integer size) {
        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null || size < 1) ? 10 : Math.min(size, 20);
        return new int[]{p, s};
    }
}
