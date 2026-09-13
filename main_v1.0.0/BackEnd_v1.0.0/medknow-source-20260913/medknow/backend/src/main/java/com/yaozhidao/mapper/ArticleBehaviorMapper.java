package com.yaozhidao.mapper;

import com.yaozhidao.entity.ArticleBehavior;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ArticleBehaviorMapper {

    int insert(ArticleBehavior behavior);

    /** 用户近30天行为，按行为权重聚合出偏好文章 id 列表（VIEW+1/LIKE+3/COLLECT+3/SHARE+5） */
    List<Long> findPreferredArticleIds(@Param("userId") Long userId,
                                       @Param("since") LocalDateTime since,
                                       @Param("limit") int limit);
}
