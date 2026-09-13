package com.yaozhidao.mapper;

import org.apache.ibatis.annotations.Param;

public interface UserDrugBoxMapper {

    int insert(@Param("userId") Long userId, @Param("drugId") Long drugId);

    int count(@Param("userId") Long userId, @Param("drugId") Long drugId);
}
