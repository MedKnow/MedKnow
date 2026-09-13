package com.medKnow.medknow.data.remote

import com.medKnow.medknow.model.ApiResponse
import com.medKnow.medknow.model.EmptyData
import com.medKnow.medknow.model.checkins.CheckinsRequest
import com.medKnow.medknow.model.checkins.GetTodayCheckinsListResponse
import com.medKnow.medknow.model.checkins.getComplianceStatistics.GetComplianceStatisticsQuery
import com.medKnow.medknow.model.checkins.getComplianceStatistics.GetComplianceStatisticsResponse
import com.medKnow.medknow.model.common.HealthCheckResponse
import com.medKnow.medknow.model.common.userFeedback.UserFeedbackRequest
import com.medKnow.medknow.model.drugSearch.medicationMessage.GetMedicationMessageResponse
import com.medKnow.medknow.model.drugSearch.medicationSearch.MedicationSearchQuery
import com.medKnow.medknow.model.drugSearch.medicationSearch.MedicationSearchResponse
import com.medKnow.medknow.model.healthArticle.ArticleCollectResponse
import com.medKnow.medknow.model.healthArticle.ArticleLikeResponse
import com.medKnow.medknow.model.healthArticle.ArticleShareRequest
import com.medKnow.medknow.model.healthArticle.articleInformation.ArticleInformationResponse
import com.medKnow.medknow.model.healthArticle.articleListSortByCategory.ArticleListSortByCategoryQuery
import com.medKnow.medknow.model.healthArticle.articleListSortByCategory.ArticleListSortByCategoryResponse
import com.medKnow.medknow.model.healthArticle.recommendedArticleList.RecommendedArticleListResponse
import com.medKnow.medknow.model.medicationPlans.ActiveMedicationPlanResponse
import com.medKnow.medknow.model.medicationPlans.ResumeMedicationPlanResponse
import com.medKnow.medknow.model.medicationPlans.StopMedicationPlanResponse
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.CreateMedicationPlanRequest
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.CreateMedicationPlanResponse
import com.medKnow.medknow.model.medicationPlans.getMedicationPlanList.GetMedicationPlanListQuery
import com.medKnow.medknow.model.medicationPlans.getMedicationPlanList.GetMedicationPlanListResponse
import com.medKnow.medknow.model.medicationPlans.getPlanMessage.GetPlanMessageResponse
import com.medKnow.medknow.model.medicationPlans.updateMedicationPlan.UpdateMedicationPlanRequest
import com.medKnow.medknow.model.userModel.GetUserMessageResponse
import com.medKnow.medknow.model.userModel.GetUserPreferSettingResponse
import com.medKnow.medknow.model.userModel.LoginByPhone.LoginByPhoneRequest
import com.medKnow.medknow.model.userModel.LoginByPhone.LoginByPhoneResponse
import com.medKnow.medknow.model.userModel.putAvatar.PutAvatarRequest
import com.medKnow.medknow.model.userModel.putAvatar.PutAvatarResponse
import com.medKnow.medknow.model.userModel.sendCodeByPhone.SendCodeByPhoneRequest
import com.medKnow.medknow.model.userModel.sendCodeByPhone.SendCodeByPhoneResponse
import com.medKnow.medknow.model.userModel.updateUserMessage.UpdateUserMessageRequest
import com.medKnow.medknow.model.userModel.updateUserMessage.UpdateUserMessageResponse
import com.medKnow.medknow.model.visitNavigation.HospitalInformationResponse
import com.medKnow.medknow.model.visitNavigation.recommendedHospitalList.RecommendedHospitalListQuery
import com.medKnow.medknow.model.visitNavigation.recommendedHospitalList.RecommendedHospitalListResponse
import com.medKnow.medknow.model.visitNavigation.searchHospital.SearchHospitalQuery
import com.medKnow.medknow.model.visitNavigation.searchHospital.SearchHospitalResponse

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface ApiService {

    // 用户模块（ 6 个）

    // 接口1：发送短信验证码
    // 用户输入手机号，后端调用短信网关发送 6 位验证码。同一手机号 60 秒内只能发送一次
    @POST("/api/v1/auth/send-code")
    suspend fun sendCodeByPhone(@Body request: SendCodeByPhoneRequest): Response<ApiResponse<SendCodeByPhoneResponse>>

    // 接口2：手机号验证码登录
    // 用户输入手机号和短信验证码完成登录，返回 JWT Token
    @POST("/api/v1/auth/login")
    suspend fun loginByPhone(@Body request: LoginByPhoneRequest): Response<ApiResponse<LoginByPhoneResponse>>

    // 接口3：获取个人信息
    // 获取当前登录用户的基本信息，包括昵称、头像、年龄、性别、职业等
    @GET("/api/v1/user/profile")
    suspend fun getUserMessage(): Response<ApiResponse<GetUserMessageResponse>>

    // 接口4：更新个人信息
    // 用户修改自己的昵称、年龄、性别、职业、过敏史、慢性病史等信息
    @PUT("/api/v1/user/profile")
    suspend fun updateUserMessage(@Body request: UpdateUserMessageRequest): Response<ApiResponse<UpdateUserMessageResponse>>

    // 接口5：获取用户偏好设置
    // 获取当前用户的个性化设置，包括主题色、头像、提醒开关等。App 启动时调用，用于恢复用户的自定义配置
    @GET("/api/v1/user/settings")
    suspend fun getUserPreferSetting(): Response<ApiResponse<GetUserPreferSettingResponse>>

    // 接口6：上传头像
    @POST("/api/v1/user/avatar")
    suspend fun putAvatar(@Body request: PutAvatarRequest): Response<ApiResponse<PutAvatarResponse>>

    // 用药计划（ 7 个）

    // 接口7：创建用药计划
    // 用户输入诊断或医嘱信息，生成结构化用药计划，以“草稿”状态保存。包含药品清单、提醒方式及智能解析冲突结果
    @POST("/api/v1/medication-plans")
    suspend fun createMedicationPlan(@Body request: CreateMedicationPlanRequest): Response<ApiResponse<CreateMedicationPlanResponse>>

    // 接口8：获取用药计划列表
    // 获取当前用户所有用药计划摘要列表，支持按状态筛选
    @GET("/api/v1/medication-plans")
    suspend fun getMedicationPlanList(@QueryMap query: GetMedicationPlanListQuery): Response<ApiResponse<GetMedicationPlanListResponse>>

    // 接口9：获取计划详情
    // 返回计划完整信息，包含药品清单、提醒方式、智能解析冲突及今日提醒列表
    @GET("/api/v1/medication-plans/{planId}")
    suspend fun getPlanMessage(@Path("planId") planId: Int): Response<ApiResponse<GetPlanMessageResponse>>

    // 接口10：更新用药计划
    // 全量更新用药计划，仅草稿或暂停状态可操作
    @PUT("/api/v1/medication-plans/{planId}")
    suspend fun updateMedicationPlan(
        @Path("planId") planId: Int,
        @Body request: UpdateMedicationPlanRequest
    ): Response<ApiResponse<Unit>>

    // 接口11：激活用药计划
    // 将草稿或暂停状态的计划激活，系统开始按计划触发提醒
    @POST("/api/v1/medication-plans/{planId}/activate")
    suspend fun activeMedicationPlan(@Path("planId") planId: Int): Response<ApiResponse<ActiveMedicationPlanResponse>>

    // 接口12：暂停用药计划
    // 暂停生效中的计划，暂停期间不发送提醒
    @POST("/api/v1/medication-plans/{planId}/pause")
    suspend fun stopMedicationPlan(@Path("planId") planId: Int): Response<ApiResponse<StopMedicationPlanResponse>>

    // 接口13：恢复用药计划
    // 恢复已暂停的计划，重新激活提醒
    @POST("/api/v1/medication-plans/{planId}/resume")
    suspend fun resumeMedicationPlan(@Path("planId") planId: Int): Response<ApiResponse<ResumeMedicationPlanResponse>>

    // 服药打卡（ 3 个）

    // 接口14：服药打卡
    // 用户收到用药提醒后，点击“已服药”完成打卡操作。系统记录实际服药时间，更新提醒状态为“已确认”，并重新计算当前周期的用药依从率
    @POST("/api/v1/checkins")
    suspend fun checkins(@Body request: CheckinsRequest): Response<ApiResponse<EmptyData?>>

    // 接口15：获取今日待打卡列表
    // 获取当前用户当天所有待处理的用药提醒列表，按计划服药时间升序排列
    @GET("/api/v1/checkins/today")
    suspend fun getTodayCheckinsList(): Response<ApiResponse<GetTodayCheckinsListResponse>>

    // 接口16：获取依从率统计
    // 获取当前用户在指定统计周期内的用药依从率数据，用于首页进度展示和“用药统计”页的趋势分析
    @GET("/api/v1/checkins/stats")
    suspend fun getComplianceStatistics(@QueryMap query: GetComplianceStatisticsQuery): Response<ApiResponse<GetComplianceStatisticsResponse>>

    // 药品搜索（ 3 个）

    // 接口17：药品模糊搜索
    // 用户通过药品名称进行搜索，系统从本地缓存数据库和云端药品库中匹配结果并返回列表，支持中英文、通用名或商品名的模糊匹配
    @GET("/api/v1/drugs/search")
    suspend fun medicationSearch(@QueryMap query: MedicationSearchQuery): Response<ApiResponse<MedicationSearchResponse>>

    // 接口18：药品详情
    // 展示指定药品的完整信息，帮助用户了解药品功效、用法、安全警示及存储条件，辅助安全用药
    @GET("/api/v1/drugs/{drugId}")
    suspend fun getMedicationMessage(@Path("drugId") drugId: Int): Response<ApiResponse<GetMedicationMessageResponse>>

    // 接口19：收藏药品到药箱
    // 用户可将常用或关注的药品收藏至个人“我的药箱”，便于后续快速查看。收藏操作采用幂等设计，重复调用不会产生多条记录
    @POST("/api/v1/drugs/{drugId}/collect")
    suspend fun addMedicationToCabinet(@Path("drugId") drugId: Int): Response<ApiResponse<EmptyData?>>

    // 就诊导航（ 3 个）

    // 接口20：推荐医院列表
    // 根据用户当前 GPS 定位（经纬度），结合科室匹配、医院评级、实时交通及距离等因素，综合计算并返回推荐医院列表，帮助用户在紧急或日常就诊场景下快速选择最优就医机构
    @GET("/api/v1/hospital/recommend")
    suspend fun recommendedHospitalList(@QueryMap query: RecommendedHospitalListQuery): Response<ApiResponse<RecommendedHospitalListResponse>>

    // 接口21：医院详情
    // 获取指定医院的完整信息，包括基本信息、科室列表及各科室简介，为用户提供决策参考，并可跳转导航
    @GET("/api/v1/hospitals/{hospitalId}")
    suspend fun hospitalInformation(@Path("hospitalId") hospitalId: Int): Response<ApiResponse<HospitalInformationResponse>>

    // 接口22：搜索医院
    // 用户通过关键词（医院名称或地址）搜索医院，系统从数据库中匹配并返回结果列表。与推荐接口不同，搜索不依赖定位信息，适用于用户已有明确目标或手动查找特定医院
    @GET("/api/v1/hospitals/search")
    suspend fun searchHospital(@QueryMap query: SearchHospitalQuery): Response<ApiResponse<SearchHospitalResponse>>

    // 科普文章（ 6 个）

    // 接口23：推荐文章列表
    // 根据用户画像（年龄、性别、职业、用药记录）返回个性化科普文章列表。未登录用户返回默认热门文章
    @GET("/api/v1/articles/recommend")
    suspend fun recommendedArticleList(): Response<ApiResponse<RecommendedArticleListResponse>>

    // 接口24：文章列表（按分类筛选）
    // 按分类获取科普文章列表，支持分页。未登录用户也可调用
    @GET("/api/v1/articles")
    suspend fun articleListSortByCategory(@QueryMap query: ArticleListSortByCategoryQuery): Response<ApiResponse<ArticleListSortByCategoryResponse>>

    // 接口25：文章详情
    // 获取科普文章的完整内容，包括正文、作者信息、互动数据等
    @GET("/api/v1/articles/{articleId}")
    suspend fun articleInformation(@Path("articleId") articleId: Int): Response<ApiResponse<ArticleInformationResponse>>

    // 接口26：收藏文章
    // 用户收藏或取消收藏一篇科普文章。已收藏时调用则取消收藏
    @POST("/api/v1/articles/{articleId}/collect")
    suspend fun articleCollect(@Path("articleId") articleId: Int): Response<ApiResponse<ArticleCollectResponse>>

    // 接口27：点赞文章
    // 用户点赞或取消点赞一篇科普文章。已点赞时调用则取消点赞
    @POST("/api/v1/articles/{articleId}/like")
    suspend fun articleLike(@Path("articleId") articleId: Int): Response<ApiResponse<ArticleLikeResponse>>

    // 接口28：分享文章
    // 用户分享文章到微信、QQ 或复制链接。后端记录分享行为，用于优化推荐算法
    @GET("/api/v1/articles/{articleId}/share")
    suspend fun articleShare(
        @Path("articleId") articleId: Int,
        @Body request: ArticleShareRequest
    ): Response<ApiResponse<EmptyData?>>

    // 通用（ 2 个）

    // 接口29：健康检查
    // 检查后端服务是否正常运行。不需要登录，不需要任何参数。返回服务状态和当前时间
    @GET("/api/v1/health")
    suspend fun healthCheck(): Response<HealthCheckResponse>

    // 接口30：用户反馈
    // 用户通过“帮助与反馈”页面提交使用建议、问题报告或其他意见。系统将记录反馈内容及可选的联系方式，便于运营团队跟进处理
    @POST("/api/v1/feedback")
    suspend fun userFeedback(@Body request: UserFeedbackRequest): Response<ApiResponse<EmptyData?>>

}