package com.yaozhidao.service;

import com.yaozhidao.common.BizException;
import com.yaozhidao.common.ResultCode;
import com.yaozhidao.common.ValidationUtil;
import com.yaozhidao.dto.request.LoginRequest;
import com.yaozhidao.dto.request.SendCodeRequest;
import com.yaozhidao.dto.response.LoginResponse;
import com.yaozhidao.dto.response.SendCodeResponse;
import com.yaozhidao.entity.SmsCode;
import com.yaozhidao.entity.User;
import com.yaozhidao.mapper.SmsCodeMapper;
import com.yaozhidao.mapper.UserMapper;
import com.yaozhidao.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * 认证服务：发送验证码 + 手机号验证码登录
 * 开发模式（app.dev-code-return=true）：验证码打印控制台并在响应中返回，联调免真实短信
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    /** 验证码有效期（秒） */
    private static final int CODE_EXPIRE_SECONDS = 300;
    /** 重发间隔（秒） */
    private static final int RESEND_INTERVAL_SECONDS = 60;
    /** 最大错误尝试次数 */
    private static final int MAX_ATTEMPTS = 5;

    private final SmsCodeMapper smsCodeMapper;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final SecureRandom random = new SecureRandom();

    @Value("${app.dev-code-return:false}")
    private boolean devCodeReturn;

    public AuthService(SmsCodeMapper smsCodeMapper, UserMapper userMapper, JwtUtil jwtUtil) {
        this.smsCodeMapper = smsCodeMapper;
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    /** 发送短信验证码 */
    public SendCodeResponse sendCode(SendCodeRequest req) {
        String phone = req.getPhone().trim();
        if (!ValidationUtil.isValidPhone(phone)) {
            throw new BizException(ResultCode.PHONE_INVALID);
        }

        // 60 秒重发限制
        SmsCode latest = smsCodeMapper.findLatestByPhone(phone);
        if (latest != null && latest.getCreatedAt().plusSeconds(RESEND_INTERVAL_SECONDS).isAfter(LocalDateTime.now())) {
            throw new BizException(ResultCode.SEND_TOO_FREQUENT);
        }

        // 生成 6 位验证码，5 分钟有效期，落库
        String code = String.format("%06d", random.nextInt(1_000_000));
        SmsCode smsCode = new SmsCode();
        smsCode.setPhone(phone);
        smsCode.setCode(code);
        smsCode.setExpireAt(LocalDateTime.now().plusSeconds(CODE_EXPIRE_SECONDS));
        smsCodeMapper.insert(smsCode);

        // 开发模式：打印控制台 + 响应返回
        log.info("【开发模式】手机号 {} 的验证码：{}", phone, code);
        String devCode = devCodeReturn ? code : null;
        return new SendCodeResponse(CODE_EXPIRE_SECONDS, devCode);
    }

    /** 手机号验证码登录（新用户自动注册） */
    public LoginResponse login(LoginRequest req) {
        String phone = req.getPhone().trim();
        String code = req.getCode().trim();
        if (!ValidationUtil.isValidPhone(phone)) {
            throw new BizException(ResultCode.PHONE_INVALID);
        }

        SmsCode latest = smsCodeMapper.findLatestByPhone(phone);
        if (latest == null || latest.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new BizException(ResultCode.CODE_EXPIRED);
        }
        if (!latest.getCode().equals(code)) {
            smsCodeMapper.incrementAttempt(latest.getId());
            if (latest.getAttemptCount() + 1 >= MAX_ATTEMPTS) {
                throw new BizException(ResultCode.CODE_ERROR, "验证码错误次数过多，请重新获取");
            }
            throw new BizException(ResultCode.CODE_ERROR);
        }

        // 查/建用户：新用户默认昵称"用户+尾号4位"，状态 PENDING（待完善信息）
        User user = userMapper.findByPhone(phone);
        boolean isNew = false;
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setUserName("用户" + phone.substring(7));
            user.setStatus("PENDING");
            user.setThemeColor("PURPLE");
            user.setNotificationEnabled(true);
            user.setSmsReminderEnabled(false);
            userMapper.insert(user);
            isNew = true;
        }
        if (isNew) {
            log.info("新用户注册：{} ({})", user.getUserName(), phone);
        }

        String token = jwtUtil.generateToken(user.getId(), phone);
        return new LoginResponse(token, user.getId(), user.getUserName(), user.getStatus());
    }
}
