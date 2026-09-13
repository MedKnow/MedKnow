package com.medKnow.medknow.data.remote.dto

import com.medKnow.medknow.model.visitNavigation.Department
import com.medKnow.medknow.model.visitNavigation.recommendedHospitalList.Hospital
import com.medKnow.medknow.model.visitNavigation.searchHospital.HospitalSearch

// 科室
data class DepartmentDto(

    // 科室名称
    val departmentName: String,

    // 科室简介
    val description: String?

)

// 医院详情
data class HospitalInformationResponseDto(

    // 医院ID
    val hospitalId: Int,

    // 医院名称
    val name: String,

    // 地址
    val address: String,

    // 联系电话
    val phone: String?,

    // 综合评分
    val rating: Double,

    // 医院简介
    val introduction: String?,

    // 科室列表
    val departments: List<Department>

)

// 推荐医院
data class HospitalDto(

    // 医院ID
    val hospitalId: Int,

    // 医院名称。全称
    val name: String,

    // 地址
    val address: String,

    // 距离（公里）。保留一位小数，如 3.2
    val distance: Double,

    // 综合评分。保留一位小数
    val rating: Double,

    // 强项科室。科室名称列表
    val mainDepartments: List<String>,

    // 预计到达时间（分钟）。驾车预计耗时
    val estimatedTime: Int,

    // 联系电话。可能为空
    val phone: String?

)

// 推荐医院列表（返回响应）
data class RecommendedHospitalListResponseDto(

    // 总记录数
    val total: Int,

    // 当前页码
    val page: Int,

    // 每页条数
    val size: Int,

    // 推荐医院列表
    val list: List<Hospital>

)

// 医院
data class HospitalSearchDto(

    // 医院ID
    val hospitalId: Int,

    // 医院名称
    val name: String,

    // 地址
    val address: String,

    // 联系电话
    val phone: String?,

    // 综合评分
    val rating: Double,

    // 主要科室
    val mainDepartments: List<String>

)

// 搜索医院（返回响应）
data class SearchHospitalResponseDto(

    // 总记录数
    val total: Int,

    // 当前页码
    val page: Int,

    // 每页条数
    val size: Int,

    // 医院列表
    val list: List<HospitalSearch>

)