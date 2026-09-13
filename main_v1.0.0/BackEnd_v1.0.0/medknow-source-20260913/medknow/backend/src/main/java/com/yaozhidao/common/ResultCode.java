package com.yaozhidao.common;

/**
 * 错误码码表（全部集中管理，新增码不许散落各处）
 * code: 业务错误码（成功为 0）；httpStatus: 对应的 HTTP 状态码
 */
public enum ResultCode {

    // ---------- 验证码/登录 ----------
    CODE_ERROR(1001, 400, "验证码错误"),
    CODE_EXPIRED(1002, 400, "验证码已过期，请重新获取"),
    PHONE_INVALID(1003, 400, "手机号格式不正确"),
    SEND_TOO_FREQUENT(1004, 400, "发送过于频繁，请稍后再试"),

    // ---------- 通用参数 ----------
    PARAM_ERROR(4000, 400, "参数校验失败"),
    PARAM_COMMON(40001, 400, "参数错误"),
    PARAM_DATE(40002, 400, "日期或输入不规范"),
    PARAM_VALUE(40003, 400, "参数值无效"),
    PARAM_STATUS(40004, 400, "无效的状态筛选值"),
    LOCATION_INVALID(40005, 400, "定位信息不完整，请授权位置权限"),
    DISTANCE_INVALID(40006, 400, "距离参数无效"),
    FILE_TOO_LARGE(40007, 400, "图片大小不能超过2MB"),
    FILE_FORMAT(40008, 400, "仅支持 JPG/PNG 格式"),
    FEEDBACK_INVALID(40009, 400, "反馈内容不能为空"),
    IMAGE_COUNT_LIMIT(40010, 400, "最多上传3张截图"),

    // ---------- 认证 ----------
    TOKEN_EXPIRED(4010, 401, "Token 已过期，请重新登录"),
    NOT_LOGIN(4011, 401, "未登录或 Token 为空"),

    // ---------- 权限 ----------
    FORBIDDEN_LOGIN(40300, 403, "请先登录"),
    PROFILE_INCOMPLETE(40301, 403, "请先完成注册或完善信息"),
    FORBIDDEN_PLAN(40302, 403, "无权访问该计划"),

    // ---------- 资源不存在 ----------
    PLAN_NOT_FOUND(40401, 404, "用药计划不存在"),
    REMINDER_NOT_FOUND(40402, 404, "提醒记录不存在"),
    DRUG_NOT_FOUND(40403, 404, "药品不存在"),
    HOSPITAL_NOT_FOUND(40404, 404, "医院不存在"),
    ARTICLE_NOT_FOUND(3001, 404, "文章不存在"),

    // ---------- 状态冲突 ----------
    STATE_CONFLICT(40901, 409, "当前状态不允许该操作"),
    CANNOT_ACTIVATE(40902, 409, "当前状态不可激活"),
    CANNOT_PAUSE(40903, 409, "仅生效中的计划可以暂停"),
    CANNOT_RESUME(40904, 409, "仅暂停中的计划可以恢复"),
    DRUG_ALREADY_IN_BOX(40905, 409, "该药品已在药箱中"),

    // ---------- 服务器 ----------
    SERVER_ERROR(50000, 500, "系统繁忙，请稍后再试");

    private final int code;
    private final int httpStatus;
    private final String message;

    ResultCode(int code, int httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
