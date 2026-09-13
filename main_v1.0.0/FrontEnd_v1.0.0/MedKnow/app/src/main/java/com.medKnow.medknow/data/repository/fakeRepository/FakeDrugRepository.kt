package com.medKnow.medknow.data.repository.fakeRepository

import com.medKnow.medknow.data.repository.DrugRepository
import com.medKnow.medknow.model.drugSearch.medicationMessage.GetMedicationMessageResponse
import com.medKnow.medknow.model.drugSearch.medicationSearch.MedicationSearchQuery
import com.medKnow.medknow.model.drugSearch.medicationSearch.MedicationSearchResponse
import com.medKnow.medknow.model.drugSearch.medicationSearch.Results
import kotlinx.coroutines.delay
import kotlin.collections.map

// 全局错误标志
object FakeErrorDrugFlags {

    // 是否有权限
    var isUnauthorized: Boolean = false
    // 网络是否正常
    var isNetworkError: Boolean = false
    // 通用服务器错误
    var simulateServerError: Boolean = false

}

// 假_药品搜索模块
class FakeDrugRepository : DrugRepository {

    // 全局正则表达式
    // 药品名称正则表达式
    val keywordRegex = Regex("^[\\u4e00-\\u9fa5a-zA-Z0-9·\\-()]+\$")

    // 虚假的药品数据库
    val medications = mutableListOf (
        GetMedicationMessageResponse(
            drugId = 10001,
            drugName = "阿莫西林胶囊",
            genericName = "阿莫西林",
            category = "抗生素",
            indications = "适用于敏感菌（不产β内酰胺酶菌株）引起的下列感染：呼吸道感染、泌尿生殖道感染、皮肤软组织感染等。",
            dosage = "成人一次0.5g，每6～8小时1次；小儿一日剂量按体重20～40mg/kg，每8小时1次。",
            sideEffects = "恶心、呕吐、腹泻及假膜性肠炎等胃肠道反应；皮疹、药物热和哮喘等过敏反应。",
            contraindications = "对青霉素类药物过敏者禁用；传染性单核细胞增多症患者禁用。",
            precautions = "肾功能不全者需根据肌酐清除率调整剂量；有哮喘、枯草热等过敏性疾病史者慎用。",
            storage = "密封，在阴凉干燥处保存（不超过20℃）。",
            isCollected = false
        )
    )
    // 药品模糊搜索
    override suspend fun medicationSearch(query: MedicationSearchQuery): Result<MedicationSearchResponse> {
        delay(300)

        val legalMedications = medications.filter { it.genericName == query.keyword }

        val queryPage = when {
            query.page == null -> 1
            else -> query.page
        }

        val querySize = when {
            query.size == null -> 20
            else -> query.size
        }

        return when {

            // 500 服务器内部错误
            FakeErrorDrugFlags.simulateServerError -> Result.failure(Exception("系统繁忙，请稍后再试"))

            // 400 参数校验失败
            // 40001 搜索关键词不能为空
            query.keyword == null -> Result.failure(Exception("搜索关键词不能为空"))

            // 40002 搜索内容包含不规范字符
            !query.keyword.matches(keywordRegex) -> Result.failure(Exception("搜索内容包含不规范字符"))

            // 200 搜索成功
            else -> {
                if (legalMedications.isNotEmpty()) {

                    // 有结果
                    Result.success (
                        MedicationSearchResponse(
                            total = legalMedications.size,
                            page = query.page ?: 1,
                            size = query.size ?: 20,
                            results = medications.map { medication ->
                                Results(
                                    drugId = medication.drugId,
                                    drugName = medication.drugName,
                                    genericName = medication.genericName,
                                    category = medication.category,
                                    summary = null
                                )
                            }
                        )
                    )

                } else {

                    // 无结果
                    Result.success (
                        MedicationSearchResponse(
                            total = 0,
                            page = 1,
                            size = 20,
                            results = emptyList()
                        )
                    )

                }
            }
        }
    }

    // 药品详情
    override suspend fun getMedicationMessage(drugId: Int): Result<GetMedicationMessageResponse> {
        delay(500)

        val legalMedication = medications.find { it.drugId == drugId }

        return when {

            // 500 服务器内部错误
            FakeErrorDrugFlags.simulateServerError -> Result.failure(Exception("系统繁忙，请稍后再试"))

            // 404 药品不存在
            legalMedication == null -> Result.failure(Exception("药品不存在"))

            // 200 获取成功
            else -> Result.success (
                GetMedicationMessageResponse(
                    drugId = legalMedication.drugId,
                    drugName = legalMedication.drugName,
                    genericName = legalMedication.genericName,
                    category = legalMedication.category,
                    indications = legalMedication.indications,
                    dosage = legalMedication.dosage,
                    sideEffects = legalMedication.sideEffects,
                    contraindications = legalMedication.contraindications,
                    precautions = legalMedication.precautions,
                    storage = legalMedication.storage,
                    isCollected = legalMedication.isCollected
                )
            )
        }
    }

    // 收藏药品到药箱
    override suspend fun addMedicationToCabinet(drugId: Int): Result<Unit> {
        delay(200)

        val legalMedication = medications.find { it.drugId == drugId }
        val legalMedicationIndex = medications.indexOfFirst { it.drugId == drugId }

        return when {

            // 500 服务器内部错误
            FakeErrorDrugFlags.simulateServerError -> Result.failure(Exception("系统繁忙，请稍后再试"))

            // 404 药品不存在
            legalMedication == null -> Result.failure(Exception("药品不存在"))

            // 409 已收藏，重复操作
            legalMedication.isCollected -> Result.failure(Exception("该药品已在药箱中"))

            // 200 收藏成功
            else -> {

                medications[legalMedicationIndex] = legalMedication.copy(isCollected = true)

                Result.success(Unit)

            }
        }
    }

}