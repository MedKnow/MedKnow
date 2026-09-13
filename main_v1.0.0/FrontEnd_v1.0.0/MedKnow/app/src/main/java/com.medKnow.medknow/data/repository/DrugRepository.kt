package com.medKnow.medknow.data.repository

import com.medKnow.medknow.model.drugSearch.medicationMessage.GetMedicationMessageResponse
import com.medKnow.medknow.model.drugSearch.medicationSearch.MedicationSearchQuery
import com.medKnow.medknow.model.drugSearch.medicationSearch.MedicationSearchResponse

// 药品搜索模块
interface DrugRepository {

    // 接口17：药品模糊搜索
    suspend fun medicationSearch(query: MedicationSearchQuery): Result<MedicationSearchResponse>

    // 接口18：获取药品详情
    suspend fun getMedicationMessage(drugId: Int): Result<GetMedicationMessageResponse>

    // 接口19：收藏药品到药箱
    suspend fun addMedicationToCabinet(drugId: Int): Result<Unit>

}