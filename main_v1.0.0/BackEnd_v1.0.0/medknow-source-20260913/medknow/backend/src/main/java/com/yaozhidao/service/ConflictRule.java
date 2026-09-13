package com.yaozhidao.service;

import java.util.Map;
import java.util.Set;

/**
 * 用药冲突检测规则（简化方案）
 * CONTRAINDICATION 用静态映射表驱动：键=药品名关键词，值=与之配伍禁忌的药名关键词数组
 * 说明：后续数据量大后可升级为 drug_conflict 表驱动，替换此静态类即可
 */
public final class ConflictRule {

    private ConflictRule() {
    }

    /** 配伍禁忌映射（含对称展开：A↔B 双向都写了，保证从任一方向都能查到） */
    private static final Map<String, String[]> CONTRAINDICATIONS = Map.of(
            "藿香正气", new String[]{"头孢", "甲硝唑", "呋喃唑酮"},   // 双硫仑样反应（含乙醇）
            "头孢", new String[]{"藿香正气", "甲硝唑", "呋喃唑酮"},   // 双硫仑样反应
            "阿司匹林", new String[]{"布洛芬"},                     // NSAID 叠加出血风险
            "布洛芬", new String[]{"阿司匹林"},                     // NSAID 叠加出血风险
            "蒙脱石散", new String[]{"阿莫西林", "左甲状腺素"},       // 吸附影响吸收，需间隔1小时
            "感冒灵", new String[]{"布洛芬"}                         // 对乙酰氨基酚+布洛芬叠加
    );

    /**
     * 判断两药是否构成配伍禁忌；drugAName/drugBName 为计划中用户输入的完整药名。
     * 映射表的键是关键词（如"藿香正气"），需遍历用包含匹配，而非精确 get()
     */
    public static boolean isContraindicated(String drugAName, String drugBName) {
        for (Map.Entry<String, String[]> entry : CONTRAINDICATIONS.entrySet()) {
            if (drugAName.contains(entry.getKey())) {
                for (String target : entry.getValue()) {
                    if (drugBName.contains(target)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** 供测试/排查用的全部禁忌关键词 */
    public static Set<String> allKeys() {
        return CONTRAINDICATIONS.keySet();
    }
}
