package com.medKnow.medknow.model.userModel.updateUserMessage

import com.medKnow.medknow.model.userModel.Gender

// 更新个人信息（请求体）
data class UpdateUserMessageRequest (

    // 用户名。用户昵称，2-20 个字符
    val userName: String?,

    // 年龄
    val age: Int?,

    // 性别。MALE / FEMALE / OTHER
    val gender: Gender?,

    // 职业
    val occupation: String?,

    // 过敏史。多个过敏源用逗号分隔
    val allergies: String?,

    // 慢性疾病。慢性病史，多个用逗号分隔
    val chronicDiseases: String?

)