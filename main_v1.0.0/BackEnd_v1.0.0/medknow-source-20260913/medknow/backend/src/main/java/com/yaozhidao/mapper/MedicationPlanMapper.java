package com.yaozhidao.mapper;

import com.yaozhidao.entity.MedicationPlan;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MedicationPlanMapper {

    int insert(MedicationPlan plan);

    MedicationPlan findById(@Param("id") Long id);

    /** 分页 + 可选状态筛选 */
    List<MedicationPlan> findByUserId(@Param("userId") Long userId,
                                      @Param("status") String status,
                                      @Param("offset") int offset,
                                      @Param("size") int size);

    long countByUserId(@Param("userId") Long userId, @Param("status") String status);

    /** 更新计划主体（状态外的字段） */
    int updatePlan(MedicationPlan plan);

    /** 更新状态 + 可选时间戳 */
    int updateStatus(@Param("id") Long id,
                     @Param("status") String status,
                     @Param("activatedAt") java.time.LocalDateTime activatedAt,
                     @Param("pausedAt") java.time.LocalDateTime pausedAt);

    /** 计划到期状态翻转：end_date < 今天 且 ACTIVE → 全部 TAKEN 则 COMPLETED，否则 EXPIRED */
    int expireOverduePlans();
}
