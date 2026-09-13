package com.yaozhidao.mapper;

import com.yaozhidao.entity.Article;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ArticleMapper {

    /** 分类分页列表，按发布时间降序 */
    List<Article> list(@Param("category") String category,
                       @Param("offset") int offset,
                       @Param("size") int size);

    long countList(@Param("category") String category);

    /** 热门列表（按阅读量降序），推荐接口用 */
    List<Article> hotList(@Param("size") int size);

    /** 按分类取热门文章（推荐算法用） */
    List<Article> hotByCategory(@Param("category") String category, @Param("size") int size);

    /** 同分类相关文章（排除自身，按阅读量取3篇） */
    List<Article> related(@Param("category") String category,
                          @Param("excludeId") Long excludeId,
                          @Param("size") int size);

    Article findById(@Param("id") Long id);

    int incrementReadCount(@Param("id") Long id);

    int incrementLikeCount(@Param("id") Long id, @Param("delta") int delta);

    int incrementCollectCount(@Param("id") Long id, @Param("delta") int delta);

    int incrementShareCount(@Param("id") Long id);
}
