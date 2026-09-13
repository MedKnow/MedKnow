package com.medKnow.medknow.model.userModel.updateUserMessage

import com.medKnow.medknow.model.userModel.Gender

// 更新个人信息（返回响应）
data class UpdateUserMessageResponse (

    // 用户名。用户昵称，2-20 个字符
    val userName: String?,

    // 年龄。0-120
    val age: Int?,

    // 性别。MALE / FEMALE / OTHER
    val gender: Gender?,

    // 职业
    val occupation: String?,

    // 过敏史。多个过敏原用逗号分隔
    val allergies: String?,

    //慢性疾病。慢性病是，多个用逗号分隔
    val chronicDiseases: String?

)