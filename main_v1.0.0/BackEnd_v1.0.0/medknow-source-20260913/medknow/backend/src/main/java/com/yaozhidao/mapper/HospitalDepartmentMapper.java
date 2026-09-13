package com.yaozhidao.mapper;

import com.yaozhidao.entity.HospitalDepartment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HospitalDepartmentMapper {

    List<HospitalDepartment> findByHospitalId(@Param("hospitalId") Long hospitalId);
}
