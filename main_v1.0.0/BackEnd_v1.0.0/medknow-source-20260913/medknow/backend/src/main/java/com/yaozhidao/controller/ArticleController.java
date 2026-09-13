package com.yaozhidao.controller;

import com.yaozhidao.common.Result;
import com.yaozhidao.dto.request.ShareRequest;
import com.yaozhidao.dto.response.ArticleDetailResponse;
import com.yaozhidao.dto.response.ArticleListResponse;
import com.yaozhidao.dto.response.CollectResponse;
import com.yaozhidao.dto.response.LikeResponse;
import com.yaozhidao.dto.response.RecommendResponse;
import com.yaozhidao.service.ArticleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 科普文章：列表/推荐/详情未登录可用；点赞/收藏需登录；分享未登录也可
 */
@RestController
@RequestMapping("/api/v1/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /** GET /api/v1/articles/recommend 个性化推荐（未登录返回热门） */
    @GetMapping("/recommend")
    public Result<RecommendResponse> recommend() {
        return Result.success("获取成功", articleService.recommend());
    }

    /** GET /api/v1/articles 分类列表（分页） */
    @GetMapping
    public Result<ArticleListResponse> list(@RequestParam(required = false) String category,
                                            @RequestParam(required = false) Integer page,
                                            @RequestParam(required = false) Integer size) {
        return Result.success("获取成功", articleService.list(category, page, size));
    }

    /** GET /api/v1/articles/{articleId} 文章详情（阅读量+1） */
    @GetMapping("/{articleId}")
    public Result<ArticleDetailResponse> detail(@PathVariable Long articleId) {
        return Result.success("获取成功", articleService.getDetail(articleId));
    }

    /** POST /api/v1/articles/{articleId}/collect 收藏/取消收藏（切换式） */
    @PostMapping("/{articleId}/collect")
    public Result<CollectResponse> collect(@PathVariable Long articleId) {
        return Result.success("操作成功", articleService.toggleCollect(articleId));
    }

    /** POST /api/v1/articles/{articleId}/like 点赞/取消点赞（切换式） */
    @PostMapping("/{articleId}/like")
    public Result<LikeResponse> like(@PathVariable Long articleId) {
        return Result.success("操作成功", articleService.toggleLike(articleId));
    }

    /** GET /api/v1/articles/{articleId}/share 分享（body 传 target） */
    @GetMapping("/{articleId}/share")
    public Result<Void> share(@PathVariable Long articleId, @RequestBody(required = false) ShareRequest req) {
        String target = req == null || req.getTarget() == null ? "" : req.getTarget();
        articleService.share(articleId, target);
        return Result.success("分享成功", null);
    }
}
