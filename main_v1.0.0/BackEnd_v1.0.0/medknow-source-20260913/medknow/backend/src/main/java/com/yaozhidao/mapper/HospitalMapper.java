package com.yaozhidao.mapper;

import com.yaozhidao.entity.Hospital;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HospitalMapper {

    /** 全部医院（推荐算法在内存计算距离与得分，种子量级足够） */
    List<Hospital> findAll();

    Hospital findById(@Param("id") Long id);

    /** 名称/地址模糊搜索 */
    List<Hospital> search(@Param("keyword") String keyword,
                          @Param("offset") int offset,
                          @Param("size") int size);

    long countSearch(@Param("keyword") String keyword);
}
