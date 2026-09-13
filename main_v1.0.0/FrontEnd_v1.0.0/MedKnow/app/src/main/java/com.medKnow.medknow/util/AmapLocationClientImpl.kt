package com.medKnow.medknow.util

import android.content.Context
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.medKnow.medknow.data.remote.LocationClient
import com.medKnow.medknow.ui.screens.visitNavigation.visitNavigationViewModel.Location
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

// LocationClient 具体实现类
class AMapLocationClientImpl(private val context: Context): LocationClient {

    private var client: AMapLocationClient? = null

    override fun locationUpdates(): Flow<Location> = callbackFlow {
        // 初始化定位客户端
        val locationClient = AMapLocationClient(context.applicationContext)
        val option = AMapLocationClientOption().apply {
            locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            interval = 2000
            isNeedAddress = false
            isOnceLocation = false
        }
        locationClient.setLocationOption(option)

        val listener = AMapLocationListener { aMapLocation ->
            if (aMapLocation != null && aMapLocation.errorCode == 0) {
                trySend (
                    Location(
                        latitude = aMapLocation.latitude,
                        longitude = aMapLocation.longitude
                    )
                )
            }
        }
        locationClient.setLocationListener(listener)
        locationClient.startLocation()

        client = locationClient

        awaitClose {
            locationClient.stopLocation()
            locationClient.onDestroy()
        }

    }

    override fun hasPermission(): Boolean {
        return true
    }

}