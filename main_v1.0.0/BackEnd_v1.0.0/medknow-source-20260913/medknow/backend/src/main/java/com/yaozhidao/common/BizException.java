package com.yaozhidao.common;

/**
 * 业务异常：携带错误码 + HTTP 状态码 + 提示信息，由 GlobalExceptionHandler 统一处理
 */
public class BizException extends RuntimeException {

    private final int code;
    private final int httpStatus;

    public BizException(ResultCode rc) {
        super(rc.getMessage());
        this.code = rc.getCode();
        this.httpStatus = rc.getHttpStatus();
    }

    public BizException(ResultCode rc, String message) {
        super(message);
        this.code = rc.getCode();
        this.httpStatus = rc.getHttpStatus();
    }

    public int getCode() {
        return code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
