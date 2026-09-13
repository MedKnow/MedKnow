package com.yaozhidao.service;

import com.yaozhidao.common.BizException;
import com.yaozhidao.common.ResultCode;
import com.yaozhidao.dto.request.FeedbackRequest;
import com.yaozhidao.entity.Feedback;
import com.yaozhidao.mapper.FeedbackMapper;
import com.yaozhidao.security.UserContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/** 通用服务：用户反馈 */
@Service
public class CommonService {

    /** 截图数量上限 */
    private static final int MAX_IMAGES = 3;
    /** 反馈内容长度上限 */
    private static final int MAX_CONTENT = 500;

    private final FeedbackMapper feedbackMapper;

    public CommonService(FeedbackMapper feedbackMapper) {
        this.feedbackMapper = feedbackMapper;
    }

    /** 提交反馈（登录可选：带 token 记录用户，匿名可提交） */
    public void submitFeedback(FeedbackRequest req) {
        String content = req.getContent() == null ? "" : req.getContent().trim();
        if (content.isEmpty()) {
            throw new BizException(ResultCode.FEEDBACK_INVALID, "反馈内容不能为空");
        }
        if (content.length() > MAX_CONTENT) {
            throw new BizException(ResultCode.FEEDBACK_INVALID, "反馈内容不能超过500字");
        }
        List<String> images = req.getImages();
        if (images != null && images.size() > MAX_IMAGES) {
            throw new BizException(ResultCode.IMAGE_COUNT_LIMIT);
        }

        Feedback feedback = new Feedback();
        feedback.setUserId(UserContext.getUserId()); // 未登录为 null（匿名）
        feedback.setContent(content);
        feedback.setContact(req.getContact());
        if (images != null && !images.isEmpty()) {
            feedback.setImages(images.stream().collect(Collectors.joining(",")));
        }
        feedbackMapper.insert(feedback);
    }
}
