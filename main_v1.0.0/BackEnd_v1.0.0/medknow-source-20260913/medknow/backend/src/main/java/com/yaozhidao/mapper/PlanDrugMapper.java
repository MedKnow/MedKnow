package com.yaozhidao.mapper;

import com.yaozhidao.entity.PlanDrug;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PlanDrugMapper {

    int insertBatch(@Param("list") List<PlanDrug> drugs);

    List<PlanDrug> findByPlanId(@Param("planId") Long planId);

    int deleteByPlanId(@Param("planId") Long planId);
}
