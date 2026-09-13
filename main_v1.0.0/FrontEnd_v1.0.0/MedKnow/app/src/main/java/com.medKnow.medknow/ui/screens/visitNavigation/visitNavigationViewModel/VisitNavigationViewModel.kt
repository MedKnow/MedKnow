package com.medKnow.medknow.ui.screens.visitNavigation.visitNavigationViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medKnow.medknow.data.remote.LocationClient
import com.medKnow.medknow.data.repository.HospitalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

// 定义位置
data class Location(
    val latitude: Double,
    val longitude: Double
)

// 导航执行状态
enum class NavStatus {
    IDLE, SEARCHING, NAVIGATING, ARRIVED, ERROR
}

@HiltViewModel
class VisitNavigationViewModel @Inject constructor(
    private val userLocation: LocationClient,
    private val hospitalRepository: HospitalRepository
): ViewModel() {

    // 定义 LocationUiState 类
    data class LocationUiState(
        val currentLocation: Location? = null,
        val isLocationEnabled: Boolean = false,
        val lastUpdated: Long = 0L
    )

    // 定义 NavigationUiState 类
    data class NavigationUiState(
        val destination: Location? = null,
        val routePolyline: List<Location> = emptyList(),
        val status: NavStatus = NavStatus.IDLE
    )

    // 定义用户位置 UI 状态
    private val _locationState = MutableStateFlow(LocationUiState())
    val locationState: StateFlow<LocationUiState> = _locationState.asStateFlow()

    // 定义导航 UI 状态
    private val _navigationState = MutableStateFlow(NavigationUiState())
    val navigationState: StateFlow<NavigationUiState> = _navigationState.asStateFlow()

    // 自动获取定位
    init {
        startLocationUpdates()
    }

    // 持续获取定位
    fun startLocationUpdates() {
        viewModelScope.launch {
            try {
                userLocation.locationUpdates().collect { point ->
                    _locationState.update {
                        it.copy(
                            currentLocation = point,
                            lastUpdated = System.currentTimeMillis()
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _navigationState.update {
                    it.copy(
                        status = NavStatus.ERROR
                    )
                }
            }
        }
    }

    // 设置目的地
    fun setDestination(point: Location) {
        _navigationState.update {
            it.copy(
                destination = point,
                status = NavStatus.NAVIGATING
            )
        }
    }

    // 更新路线
    fun updateRoute(points: List<Location>) {
        _navigationState.update {
            it.copy(
                routePolyline = points
            )
        }
    }

    // 由地图定位回调触发
    fun onLocationChanged(latitude: Double, longitude: Double) {
        _locationState.update {
            it.copy(
                currentLocation = Location(latitude, longitude),
                lastUpdated = System.currentTimeMillis()
            )
        }
    }

    // 手动定位
    fun backToCurrentLocation() {
        val current = _locationState.value.currentLocation ?: return
        _navigationState.update {
            it.copy(
                destination = current
            )
        }
    }

    // 是否有定位权限
    fun hasLocationPermission(): Boolean = userLocation.hasPermission()

    // 权限请求结果
    fun onPermissionResult(granted: Boolean) {
        _locationState.update {
            it.copy(
                isLocationEnabled = granted
            )
        }
        if (granted) {
            startLocationUpdates()
        } else {
            _navigationState.update {
                it.copy(
                    status = NavStatus.ERROR
                )
            }
        }
    }

}