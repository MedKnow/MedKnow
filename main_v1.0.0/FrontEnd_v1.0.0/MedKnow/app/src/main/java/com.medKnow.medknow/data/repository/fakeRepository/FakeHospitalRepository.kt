package com.medKnow.medknow.data.repository.fakeRepository

import com.medKnow.medknow.data.repository.HospitalRepository
import com.medKnow.medknow.model.visitNavigation.Department
import com.medKnow.medknow.model.visitNavigation.HospitalInformationResponse
import com.medKnow.medknow.model.visitNavigation.recommendedHospitalList.RecommendedHospitalListQuery
import com.medKnow.medknow.model.visitNavigation.recommendedHospitalList.RecommendedHospitalListResponse
import com.medKnow.medknow.model.visitNavigation.searchHospital.SearchHospitalQuery
import com.medKnow.medknow.model.visitNavigation.searchHospital.SearchHospitalResponse
import com.medKnow.medknow.model.visitNavigation.searchHospital.HospitalSearch
import com.medKnow.medknow.model.visitNavigation.recommendedHospitalList.Hospital

import kotlinx.coroutines.delay

// 全局错误标志
object FakeErrorHospitalFlags {

    // 是否有权限
    var isUnauthorized: Boolean = false
    // 网络是否正常
    var isNetworkError: Boolean = false
    // 通用服务器错误
    var simulateServerError: Boolean = false

}

// 假_就诊导航模块
class FakeHospitalRepository : HospitalRepository {

    // 全局正则表达式
    // 医院名称正则表达式
    val keywordRegex = Regex("^[\\u4e00-\\u9fa5a-zA-Z0-9·\\-()]+\$")

    // 虚假的医院数据库
    val hospitals = mutableListOf (
        HospitalInformationResponse(
            hospitalId = 1021,
            name = "湘雅第二医院",
            address = "长沙市岳麓区书院南路",
            phone = "18134093379",
            rating = 4.5,
            introduction = "中南大学湘雅二医院",
            departments = listOf(
                Department(
                    departmentName = "肿瘤科",
                    description = "在治疗恶性肿瘤方面遥遥领先"
                ),
                Department(
                    departmentName = "脑科",
                    description = "医疗器械先进，医资力量雄厚"
                )
            )
        ),
        HospitalInformationResponse(
            hospitalId = 1022,
            name = "长沙市人民医院",
            address = "长沙市雨花区湘江中路",
            phone = "19327903824",
            rating = 4.0,
            introduction = "百年历史，深得长沙市人民信赖",
            departments = listOf(
                Department(
                    departmentName = "精神科",
                    description = "建科历史悠久，诸多老资历医生，临床经验丰富"
                ),
                Department(
                    departmentName = "眼科",
                    description = "医资力量雄厚，科室有多名在校医学教授"
                )
            )
        )
    )

    // 推荐医院列表
    override suspend fun recommendedHospitalList(query: RecommendedHospitalListQuery): Result<RecommendedHospitalListResponse> {
        delay(100)

        val legalHospital = hospitals.find { it.rating == 4.0 }

        return when {

            // 500 服务器内部错误
            FakeErrorHospitalFlags.simulateServerError -> Result.failure(Exception("系统繁忙，请稍后重试"))

            // 400 参数校验失败
            // 40005 经纬度缺失
            query.longitude == null || query.latitude == null -> Result.failure(Exception("经纬度缺失或格式错误"))

            // 40006 距离参数无效
            query.distance ?: 10 < 1 || query.distance ?: 10 > 50 -> Result.failure(Exception("距离参数无效"))

            // 200 推荐成功
            else -> {
                if (legalHospital != null) {

                    // 有数据
                    Result.success (
                        RecommendedHospitalListResponse(
                            total = 1,
                            page = query.page ?: 1,
                            size = query.size ?: 10,
                            list = listOf (
                                Hospital(
                                    hospitalId = legalHospital.hospitalId,
                                    name = legalHospital.name,
                                    address = legalHospital.address,
                                    distance = 3.3,
                                    rating = legalHospital.rating,
                                    mainDepartments = legalHospital.departments.map { it.departmentName },
                                    estimatedTime = 10,
                                    phone = legalHospital.phone
                                )
                            )
                        )
                    )

                } else {

                    // 无数据
                    Result.success (
                        RecommendedHospitalListResponse(
                            total = 0,
                            page = 1,
                            size = 10,
                            list = emptyList()
                        )
                    )

                }
            }
        }
    }

    // 医院详情
    override suspend fun hospitalInformation(hospitalId: Int): Result<HospitalInformationResponse> {
        delay(300)

        val legalHospital = hospitals.find { it.hospitalId == hospitalId }

        return when {

            // 500 服务器内部错误
            FakeErrorHospitalFlags.simulateServerError -> Result.failure(Exception("系统繁忙，请稍后重试"))

            // 404 医院不存在
            legalHospital == null -> Result.failure(Exception("医院不存在"))

            // 200 获取成功
            else -> Result.success (
                HospitalInformationResponse(
                    hospitalId = legalHospital.hospitalId,
                    name = legalHospital.name,
                    address = legalHospital.address,
                    phone = legalHospital.phone,
                    rating = legalHospital.rating,
                    introduction = legalHospital.introduction,
                    departments = legalHospital.departments
                )
            )

        }
    }

    // 搜索医院
    override suspend fun searchHospital(query: SearchHospitalQuery): Result<SearchHospitalResponse> {
        delay(400)

        val legalHospitals = hospitals.filter { it.name == query.keyword }

        return when {

            // 500 服务器内部错误
            FakeErrorHospitalFlags.simulateServerError -> Result.failure(Exception("系统繁忙，请稍后再试"))

            // 400 参数校验失败
            // 40001 关键词为空
            query.keyword == null -> Result.failure(Exception("搜索关键词不能为空"))

            // 40002 关键词不合规
            !query.keyword.matches(keywordRegex) -> Result.failure(Exception("搜索内容包含不规范字符"))

            // 200 搜索成功
            else -> {
                if (legalHospitals.isNotEmpty()) {

                    // 有结果
                    Result.success (
                        SearchHospitalResponse(
                            total = legalHospitals.size,
                            page = query.page ?: 1,
                            size = query.size ?: 10,
                            list = legalHospitals.map { legalHospital ->
                                HospitalSearch(
                                    hospitalId = legalHospital.hospitalId,
                                    name = legalHospital.name,
                                    address = legalHospital.address,
                                    phone = legalHospital.phone,
                                    rating = legalHospital.rating,
                                    mainDepartments = legalHospital.departments.map { it.departmentName}
                                )
                            }
                        )
                    )

                } else {

                    // 无结果
                    Result.success (
                        SearchHospitalResponse(
                            total = 0,
                            page = 1,
                            size = 10,
                            list = emptyList()
                        )
                    )

                }
            }
        }
    }

}