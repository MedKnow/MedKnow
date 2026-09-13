package com.medKnow.medknow.data.remote.dto

import com.medKnow.medknow.model.drugSearch.medicationMessage.GetMedicationMessageResponse
import com.medKnow.medknow.model.drugSearch.medicationSearch.MedicationSearchResponse
import com.medKnow.medknow.model.drugSearch.medicationSearch.Results

// 药品搜索 data
data class MedicationSearchData (

    // 总记录数
    val total: Int,

    // 当前页码
    val page: Int,

    // 每页条数
    val size: Int,

    // 搜索结果列表
    val results: List<MedicationSearchItemDto>,

) {
    fun toDomain(): MedicationSearchResponse {
        return MedicationSearchResponse(
            total = total,
            page = page,
            size = size,
            results = results.map { it.toDomain() },
        )
    }
}

// 药品搜索 dto
data class MedicationSearchItemDto (

    // 药品ID
    val drugId: Int,

    // 药品名称。如“阿莫西林胶囊”
    val drugName: String,

    // 通用名。如“阿莫西林”
    val genericName: String,

    // 药品类别。如“抗生素”
    val category: String? = null,

    // 功效摘要。简要描述
    val summary: String? = null

) {
    fun toDomain(): Results {
        return Results(
            drugId = drugId,
            drugName = drugName,
            genericName = genericName,
            category = category,
            summary = summary,
        )
    }
}

// 药品详情 data
data class MedicationMessageData (

    // 药品ID
    val drugId: Int,

    // 药品名称
    val drugName: String,

    // 通用名
    val genericName: String,

    // 药品类别。如“抗生素”
    val category: String? = null,

    // 适应症
    val indications: String? = null,

    // 用法用量
    val dosage: String? = null,

    // 不良反应
    val sideEffects: String? = null,

    // 禁忌
    val contraindications: String? = null,

    // 注意事项
    val precautions: String? = null,

    // 存储条件
    val storage: String? = null,

    // 收藏
    val isCollected: Boolean

) {
    fun toDomain(): GetMedicationMessageResponse {
        return GetMedicationMessageResponse(
            drugId = drugId,
            drugName = drugName,
            genericName = genericName,
            category = category,
            indications = indications,
            dosage = dosage,
            sideEffects = sideEffects,
            contraindications = contraindications,
            precautions = precautions,
            storage = storage,
            isCollected = isCollected,
        )
    }
}