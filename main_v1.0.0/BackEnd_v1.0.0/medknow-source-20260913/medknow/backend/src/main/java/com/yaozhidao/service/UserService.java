package com.yaozhidao.service;

import com.yaozhidao.common.BizException;
import com.yaozhidao.common.ResultCode;
import com.yaozhidao.dto.request.ProfileUpdateRequest;
import com.yaozhidao.dto.response.AvatarResponse;
import com.yaozhidao.dto.response.SettingsResponse;
import com.yaozhidao.entity.User;
import com.yaozhidao.mapper.UserMapper;
import com.yaozhidao.security.UserContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/** 用户服务：个人信息 / 偏好设置 / 头像上传 */
@Service
public class UserService {

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png");
    private static final long MAX_SIZE = 2 * 1024 * 1024; // 2MB
    private static final Set<String> GENDERS = Set.of("MALE", "FEMALE", "OTHER");
    private static final Set<String> THEMES = Set.of("PURPLE", "GREEN", "BLUE", "ORANGE", "GRAY");
    /** 必填资料四件套，齐全才升级为 NORMAL（PRD：昵称/年龄/性别/职业） */
    private static final List<String> REQUIRED_FIELDS = Arrays.asList("userName", "age", "gender", "occupation");

    private final UserMapper userMapper;

    @Value("${app.upload-dir}")
    private String uploadDir;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /** 获取个人信息（手机号脱敏） */
    public User getProfile() {
        Long userId = UserContext.requireUserId();
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BizException(ResultCode.NOT_LOGIN);
        }
        // 脱敏：138****8000
        String phone = user.getPhone();
        user.setPhone(phone.substring(0, 3) + "****" + phone.substring(7));
        return user;
    }

    /** 更新个人信息；必填四件套齐全则账户升级为 NORMAL，否则保持 PENDING */
    public void updateProfile(ProfileUpdateRequest req) {
        Long userId = UserContext.requireUserId();
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BizException(ResultCode.NOT_LOGIN);
        }

        if (req.getUserName() != null) {
            String name = req.getUserName().trim();
            if (name.length() < 2 || name.length() > 20) {
                throw new BizException(ResultCode.PARAM_COMMON, "昵称长度需在2~20个字符之间");
            }
            user.setUserName(name);
        }
        if (req.getAge() != null) {
            if (req.getAge() < 0 || req.getAge() > 120) {
                throw new BizException(ResultCode.PARAM_COMMON, "年龄需在0~120之间");
            }
            user.setAge(req.getAge());
        }
        if (req.getGender() != null) {
            if (!GENDERS.contains(req.getGender())) {
                throw new BizException(ResultCode.PARAM_COMMON, "性别取值不合法");
            }
            user.setGender(req.getGender());
        }
        if (req.getOccupation() != null) {
            user.setOccupation(req.getOccupation().trim());
        }
        if (req.getAllergies() != null) {
            user.setAllergies(req.getAllergies().trim());
        }
        if (req.getChronicDiseases() != null) {
            user.setChronicDiseases(req.getChronicDiseases().trim());
        }

        // 账户状态：必填四件套齐全 → NORMAL，否则 PENDING
        boolean complete = REQUIRED_FIELDS.stream().allMatch(f -> {
            switch (f) {
                case "userName": return StringUtils.hasText(user.getUserName());
                case "age": return user.getAge() != null;
                case "gender": return StringUtils.hasText(user.getGender());
                default: return StringUtils.hasText(user.getOccupation());
            }
        });
        user.setStatus(complete ? "NORMAL" : "PENDING");

        userMapper.updateProfile(user);
    }

    /** 获取偏好设置 */
    public SettingsResponse getSettings() {
        Long userId = UserContext.requireUserId();
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BizException(ResultCode.NOT_LOGIN);
        }
        SettingsResponse resp = new SettingsResponse();
        resp.setThemeColor(user.getThemeColor());
        resp.setAvatar(defaultAvatar(user.getAvatar()));
        resp.setNotificationEnabled(user.getNotificationEnabled());
        resp.setSmsReminderEnabled(user.getSmsReminderEnabled());
        return resp;
    }

    /** 上传头像：jpg/jpeg/png ≤2MB，存本地 upload 目录，UUID 文件名防重名/路径穿越 */
    public AvatarResponse uploadAvatar(MultipartFile file, String baseUrl) {
        Long userId = UserContext.requireUserId();

        if (file == null || file.isEmpty()) {
            throw new BizException(ResultCode.PARAM_COMMON, "请选择要上传的图片");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BizException(ResultCode.FILE_TOO_LARGE);
        }

        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            ext = original.substring(dot + 1).toLowerCase(Locale.ROOT);
        }
        if (!ALLOWED_EXT.contains(ext)) {
            throw new BizException(ResultCode.FILE_FORMAT);
        }

        try {
            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            String fileName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
            Path target = dir.resolve(fileName).normalize();
            // 双保险：确保目标仍在 upload 目录内（防路径穿越）
            if (!target.startsWith(dir)) {
                throw new BizException(ResultCode.FILE_FORMAT);
            }
            file.transferTo(target.toFile());

            String url = baseUrl + "/api/v1/files/avatars/" + fileName;
            userMapper.updateAvatar(userId, url);
            return new AvatarResponse(url);
        } catch (IOException e) {
            throw new BizException(ResultCode.SERVER_ERROR);
        }
    }

    /** 头像为空时返回默认地址 */
    private String defaultAvatar(String avatar) {
        return StringUtils.hasText(avatar) ? avatar : "https://cdn.yaozhidao.com/avatars/default.png";
    }
}
