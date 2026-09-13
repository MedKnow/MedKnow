package com.yaozhidao.mapper;

import com.yaozhidao.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    User findById(@Param("id") Long id);

    User findByPhone(@Param("phone") String phone);

    int insert(User user);

    /** 更新个人资料（userName/age/gender/occupation/allergies/chronicDiseases/status） */
    int updateProfile(User user);

    /** 更新偏好设置（themeColor/notificationEnabled/smsReminderEnabled） */
    int updateSettings(User user);

    /** 更新头像 */
    int updateAvatar(@Param("id") Long id, @Param("avatar") String avatar);
}
