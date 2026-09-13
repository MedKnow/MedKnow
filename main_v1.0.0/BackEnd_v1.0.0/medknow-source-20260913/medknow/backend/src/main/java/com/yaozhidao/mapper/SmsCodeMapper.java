package com.yaozhidao.mapper;

import com.yaozhidao.entity.SmsCode;
import org.apache.ibatis.annotations.Param;

public interface SmsCodeMapper {

    int insert(SmsCode smsCode);

    /** 该手机号最近一条验证码记录（按发送时间倒序） */
    SmsCode findLatestByPhone(@Param("phone") String phone);

    /** 错误次数 +1 */
    int incrementAttempt(@Param("id") Long id);
}
