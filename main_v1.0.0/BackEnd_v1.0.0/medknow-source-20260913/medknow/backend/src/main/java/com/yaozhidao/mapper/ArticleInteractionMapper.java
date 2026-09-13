package com.yaozhidao.mapper;

import com.yaozhidao.entity.ArticleInteraction;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ArticleInteractionMapper {

    ArticleInteraction find(@Param("userId") Long userId,
                            @Param("articleId") Long articleId,
                            @Param("type") String type);

    int insert(ArticleInteraction interaction);

    int delete(@Param("id") Long id);

    /** 用户对一批文章的 LIKE/COLLECT 状态（列表页一次查出，避免 N+1） */
    List<ArticleInteraction> findByUserAndArticles(@Param("userId") Long userId,
                                                   @Param("articleIds") List<Long> articleIds);
}
