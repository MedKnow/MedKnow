package com.yaozhidao.service;

import com.yaozhidao.common.BizException;
import com.yaozhidao.common.ResultCode;
import com.yaozhidao.common.ValidationUtil;
import com.yaozhidao.dto.response.ArticleDetailResponse;
import com.yaozhidao.dto.response.ArticleListResponse;
import com.yaozhidao.dto.response.CollectResponse;
import com.yaozhidao.dto.response.LikeResponse;
import com.yaozhidao.dto.response.RecommendResponse;
import com.yaozhidao.entity.Article;
import com.yaozhidao.entity.ArticleBehavior;
import com.yaozhidao.entity.ArticleInteraction;
import com.yaozhidao.mapper.ArticleBehaviorMapper;
import com.yaozhidao.mapper.ArticleInteractionMapper;
import com.yaozhidao.mapper.ArticleMapper;
import com.yaozhidao.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 科普文章服务：分类列表 / 个性化推荐 / 详情 / 点赞 / 收藏 / 分享
 * 文章相关接口除互动外均支持未登录访问（可选登录）
 */
@Service
public class ArticleService {

    private static final Set<String> CATEGORIES = Set.of("中药", "西药", "养生", "疾病", "饮食");
    private static final Set<String> SHARE_TARGETS = Set.of("WECHAT", "QQ", "COPY_LINK");
    private static final String TYPE_LIKE = "LIKE";
    private static final String TYPE_COLLECT = "COLLECT";

    private final ArticleMapper articleMapper;
    private final ArticleInteractionMapper interactionMapper;
    private final ArticleBehaviorMapper behaviorMapper;

    public ArticleService(ArticleMapper articleMapper,
                          ArticleInteractionMapper interactionMapper,
                          ArticleBehaviorMapper behaviorMapper) {
        this.articleMapper = articleMapper;
        this.interactionMapper = interactionMapper;
        this.behaviorMapper = behaviorMapper;
    }

    /** 分类列表（分页，未登录可用） */
    public ArticleListResponse list(String category, Integer page, Integer size) {
        if (category != null && !category.isBlank() && !CATEGORIES.contains(category)) {
            throw new BizException(ResultCode.PARAM_COMMON, "无效的文章分类");
        }
        int[] p = ValidationUtil.normalizePage(page, size);
        String cat = (category == null || category.isBlank()) ? null : category;

        List<Article> list = articleMapper.list(cat, (p[0] - 1) * p[1], p[1]);
        long total = articleMapper.countList(cat);

        ArticleListResponse resp = new ArticleListResponse();
        resp.setArticles(toCards(list));
        resp.setTotal(total);
        resp.setPage(p[0]);
        resp.setSize(p[1]);
        resp.setTotalPages((int) Math.ceil((double) total / p[1]));
        return resp;
    }

    /** 个性化推荐：登录用户按行为偏好，未登录返回热门 */
    public RecommendResponse recommend() {
        Long userId = UserContext.getUserId();
        List<Article> articles;

        if (userId != null) {
            // 近 30 天行为聚合出偏好文章（按 VIEW+1/LIKE+3/COLLECT+3/SHARE+5 加权）
            List<Long> preferred = behaviorMapper.findPreferredArticleIds(
                    userId, LocalDateTime.now().minusDays(30), 50);
            if (!preferred.isEmpty()) {
                // 取偏好文章中最热门的 10 篇
                List<Article> preferredArticles = preferred.stream()
                        .map(articleMapper::findById)
                        .filter(a -> a != null && Boolean.TRUE.equals(a.getStatus()))
                        .sorted((a1, a2) -> Integer.compare(a2.getReadCount(), a1.getReadCount()))
                        .limit(10)
                        .collect(Collectors.toList());
                articles = preferredArticles;
            } else {
                articles = articleMapper.hotList(10);
            }
        } else {
            articles = articleMapper.hotList(10);
        }

        RecommendResponse resp = new RecommendResponse();
        resp.setArticles(toCards(articles));
        resp.setTotal(articles.size());
        return resp;
    }

    /** 文章详情：阅读量 +1、记录 VIEW 行为、相关文章推荐 */
    public ArticleDetailResponse getDetail(Long articleId) {
        Article article = articleMapper.findById(articleId);
        if (article == null) {
            throw new BizException(ResultCode.ARTICLE_NOT_FOUND);
        }

        articleMapper.incrementReadCount(articleId);
        Long userId = UserContext.getUserId();
        if (userId != null) {
            recordBehavior(userId, articleId, "VIEW", null);
        }

        ArticleDetailResponse resp = new ArticleDetailResponse();
        resp.setArticleId(article.getId());
        resp.setTitle(article.getTitle());
        resp.setContent(article.getContent());
        resp.setCoverImage(article.getCoverImage());
        resp.setCategory(article.getCategory());
        resp.setAuthor(article.getAuthor());
        resp.setAuthorTitle(article.getAuthorTitle());
        resp.setPublishTime(article.getPublishTime());
        resp.setReadCount(article.getReadCount() + 1);
        resp.setLikeCount(article.getLikeCount());
        resp.setCollectCount(article.getCollectCount());
        resp.setShareCount(article.getShareCount());
        resp.setIsLiked(isInteracted(userId, articleId, TYPE_LIKE));
        resp.setIsCollected(isInteracted(userId, articleId, TYPE_COLLECT));
        resp.setTags(article.getTags() == null ? List.of()
                : Arrays.stream(article.getTags().split(",")).map(String::trim).collect(Collectors.toList()));
        resp.setRelatedArticles(articleMapper.related(article.getCategory(), articleId, 3).stream()
                .map(a -> {
                    ArticleDetailResponse.RelatedItem item = new ArticleDetailResponse.RelatedItem();
                    item.setArticleId(a.getId());
                    item.setTitle(a.getTitle());
                    item.setCoverImage(a.getCoverImage());
                    return item;
                }).collect(Collectors.toList()));
        return resp;
    }

    /** 收藏/取消收藏（切换式） */
    @Transactional
    public CollectResponse toggleCollect(Long articleId) {
        Long userId = UserContext.requireUserId();
        if (articleMapper.findById(articleId) == null) {
            throw new BizException(ResultCode.ARTICLE_NOT_FOUND);
        }
        boolean nowCollected = toggle(userId, articleId, TYPE_COLLECT);
        articleMapper.incrementCollectCount(articleId, nowCollected ? 1 : -1);
        recordBehavior(userId, articleId, "COLLECT", null);
        return new CollectResponse(nowCollected);
    }

    /** 点赞/取消点赞（切换式），返回最新状态与总数 */
    @Transactional
    public LikeResponse toggleLike(Long articleId) {
        Long userId = UserContext.requireUserId();
        if (articleMapper.findById(articleId) == null) {
            throw new BizException(ResultCode.ARTICLE_NOT_FOUND);
        }
        boolean nowLiked = toggle(userId, articleId, TYPE_LIKE);
        articleMapper.incrementLikeCount(articleId, nowLiked ? 1 : -1);
        recordBehavior(userId, articleId, "LIKE", null);
        Article latest = articleMapper.findById(articleId);
        return new LikeResponse(nowLiked, latest.getLikeCount());
    }

    /** 分享：记录行为 + shareCount+1 */
    @Transactional
    public void share(Long articleId, String target) {
        if (!SHARE_TARGETS.contains(target)) {
            throw new BizException(ResultCode.PARAM_COMMON, "不支持的分享平台");
        }
        if (articleMapper.findById(articleId) == null) {
            throw new BizException(ResultCode.ARTICLE_NOT_FOUND);
        }
        articleMapper.incrementShareCount(articleId);
        recordBehavior(UserContext.getUserId(), articleId, "SHARE", target);
    }

    // ---------- 私有工具 ----------

    /** 切换式互动：存在则删（取消），不存在则插（点赞/收藏） */
    private boolean toggle(Long userId, Long articleId, String type) {
        ArticleInteraction exist = interactionMapper.find(userId, articleId, type);
        if (exist != null) {
            interactionMapper.delete(exist.getId());
            return false;
        }
        ArticleInteraction interaction = new ArticleInteraction();
        interaction.setUserId(userId);
        interaction.setArticleId(articleId);
        interaction.setType(type);
        interactionMapper.insert(interaction);
        return true;
    }

    /** 是否已互动（未登录恒 false） */
    private boolean isInteracted(Long userId, Long articleId, String type) {
        return userId != null && interactionMapper.find(userId, articleId, type) != null;
    }

    /** 记录浏览行为（未登录可空） */
    private void recordBehavior(Long userId, Long articleId, String type, String target) {
        ArticleBehavior behavior = new ArticleBehavior();
        behavior.setUserId(userId);
        behavior.setArticleId(articleId);
        behavior.setBehaviorType(type);
        behavior.setTarget(target);
        behaviorMapper.insert(behavior);
    }

    /** 实体列表 → 卡片 DTO（补 isLiked/isCollected） */
    private List<ArticleListResponse.Card> toCards(List<Article> articles) {
        Long userId = UserContext.getUserId();
        Set<Long> liked = new HashSet<>();
        Set<Long> collected = new HashSet<>();
        if (userId != null && !articles.isEmpty()) {
            List<Long> ids = articles.stream().map(Article::getId).collect(Collectors.toList());
            for (ArticleInteraction it : interactionMapper.findByUserAndArticles(userId, ids)) {
                if (TYPE_LIKE.equals(it.getType())) {
                    liked.add(it.getArticleId());
                } else {
                    collected.add(it.getArticleId());
                }
            }
        }
        return articles.stream().map(a -> {
            ArticleListResponse.Card card = new ArticleListResponse.Card();
            card.setArticleId(a.getId());
            card.setTitle(a.getTitle());
            card.setSummary(a.getSummary());
            card.setCoverImage(a.getCoverImage());
            card.setCategory(a.getCategory());
            card.setAuthor(a.getAuthor());
            card.setPublishTime(a.getPublishTime());
            card.setReadCount(a.getReadCount());
            card.setLikeCount(a.getLikeCount());
            card.setIsLiked(liked.contains(a.getId()));
            card.setIsCollected(collected.contains(a.getId()));
            return card;
        }).collect(Collectors.toList());
    }
}
