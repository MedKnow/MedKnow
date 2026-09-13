package com.medKnow.medknow.data.remote

import com.medKnow.medknow.ui.screens.visitNavigation.visitNavigationViewModel.Location
import kotlinx.coroutines.flow.Flow

// 定位服务接口
interface LocationClient {

    // 获取定位信息
    fun locationUpdates(): Flow<Location>

    // 是否具有定位权限
    fun hasPermission(): Boolean

}