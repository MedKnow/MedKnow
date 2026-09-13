package com.yaozhidao.mapper;

import com.yaozhidao.entity.Drug;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DrugMapper {

    /** 模糊搜索（参数化 LIKE，防注入） */
    List<Drug> search(@Param("keyword") String keyword,
                      @Param("offset") int offset,
                      @Param("size") int size);

    long countSearch(@Param("keyword") String keyword);

    Drug findById(@Param("id") Long id);

    /** 按名称匹配药品库（先精确后模糊） */
    List<Drug> findByName(@Param("name") String name);
}
