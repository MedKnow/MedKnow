package com.medKnow.medknow.model.userModel

// 获取个人信息
data class GetUserMessageResponse (

    // 用户ID。登录时返回的那个用户ID
    val userId: String,

    // 用户名
    val userName: String,

    // 手机号，脱敏显示（中间 4 位用 * 代替）
    val phone: String,

    // 头像URL
    val avatar: String,

    // 年龄
    val age: Int,

    // 性别。MALE 男 / FEMALE 女 / OTHER 其他
    val gender: Gender,

    // 职业
    val occupation: String,

    // 过敏史
    val allergies: String?,

    // 慢性疾病
    val chronicDiseases: String?,

    // 账户状态。NORMAL 正常 / PENDING 待完善 / RESTRICTED 受限
    val status: String,

    // 注册时间。格式yyyy-MM-dd HH:mm:ss
    val createdAt: String

)