package com.yaozhidao.controller;

import com.yaozhidao.common.Result;
import com.yaozhidao.dto.request.ProfileUpdateRequest;
import com.yaozhidao.dto.response.AvatarResponse;
import com.yaozhidao.dto.response.SettingsResponse;
import com.yaozhidao.entity.User;
import com.yaozhidao.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** 用户模块：个人信息 / 偏好设置 / 头像上传（全部需要登录） */
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** GET /api/v1/user/profile 获取个人信息（手机号脱敏） */
    @GetMapping("/profile")
    public Result<User> getProfile() {
        return Result.success("获取成功", userService.getProfile());
    }

    /** PUT /api/v1/user/profile 更新个人信息 */
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody ProfileUpdateRequest req) {
        userService.updateProfile(req);
        return Result.success("更新成功", null);
    }

    /** GET /api/v1/user/settings 获取偏好设置 */
    @GetMapping("/settings")
    public Result<SettingsResponse> getSettings() {
        return Result.success("获取成功", userService.getSettings());
    }

    /** POST /api/v1/user/avatar 上传头像（jpg/jpeg/png ≤2MB） */
    @PostMapping("/avatar")
    public Result<AvatarResponse> uploadAvatar(@RequestPart("file") MultipartFile file,
                                               HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName()
                + (request.getServerPort() == 80 ? "" : ":" + request.getServerPort());
        return Result.success("上传成功", userService.uploadAvatar(file, baseUrl));
    }
}
