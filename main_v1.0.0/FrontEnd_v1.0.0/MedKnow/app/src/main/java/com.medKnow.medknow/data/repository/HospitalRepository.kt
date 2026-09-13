package com.medKnow.medknow.data.repository

import com.medKnow.medknow.model.visitNavigation.HospitalInformationResponse
import com.medKnow.medknow.model.visitNavigation.recommendedHospitalList.RecommendedHospitalListQuery
import com.medKnow.medknow.model.visitNavigation.recommendedHospitalList.RecommendedHospitalListResponse
import com.medKnow.medknow.model.visitNavigation.searchHospital.SearchHospitalQuery
import com.medKnow.medknow.model.visitNavigation.searchHospital.SearchHospitalResponse

// 就诊导航模块
interface HospitalRepository {

    // 接口20：推荐医院列表
    suspend fun recommendedHospitalList(query: RecommendedHospitalListQuery): Result<RecommendedHospitalListResponse>

    // 接口21：医院详情
    suspend fun hospitalInformation(hospitalId: Int): Result<HospitalInformationResponse>

    // 接口22：搜索医院
    suspend fun searchHospital(query: SearchHospitalQuery): Result<SearchHospitalResponse>

}