package com.medKnow.medknow.ui.screens

import com.medKnow.medknow.MainDispatcherRule
import com.medKnow.medknow.data.remote.LocationClient
import com.medKnow.medknow.data.repository.fakeRepository.FakeHospitalRepository
import com.medKnow.medknow.ui.screens.visitNavigation.visitNavigationViewModel.Location
import com.medKnow.medknow.ui.screens.visitNavigation.visitNavigationViewModel.NavStatus
import com.medKnow.medknow.ui.screens.visitNavigation.visitNavigationViewModel.VisitNavigationViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.junit.Test

// 假 LocationClient
private class FakeLocationClient(
    private val permissionGranted: Boolean = true,
    private val emitsLocation: Boolean = true
): LocationClient {

    // 位置点序列
    private val locations = listOf(
        Location(28.2282, 112.9388),
        Location(28.2250, 112.9400)
    )

    override fun hasPermission(): Boolean = permissionGranted

    override fun locationUpdates(): Flow<Location> = flow {
        if (emitsLocation) {
            for (point in locations) {
                emit(point)
            }
        } else {
            throw Exception("定位失败")
        }
    }
}

// 就诊导航页 ViewModel 单元测试
@OptIn(ExperimentalCoroutinesApi::class)
class VisitNavigationViewModelTest {

    // 替换 Dispatcher.mainn
    private val mainDispatcherRule = MainDispatcherRule()

    // 初始化假仓库
    private val fakeHospitalRepository = FakeHospitalRepository()
    private val fakeLocation = FakeLocationClient()
    private fun createViewModel(
        fakeLocation: LocationClient = this.fakeLocation
    ) = VisitNavigationViewModel(fakeLocation, fakeHospitalRepository)

    // 初始状态
    @Test
    fun onStartState() {
        val viewModel = createViewModel()
        val locationState = viewModel.locationState.value
        val navigationState = viewModel.navigationState.value

        assertNull(locationState.currentLocation)
        assertFalse(locationState.isLocationEnabled)
        assertEquals(0L, locationState.lastUpdated)
        assertNull(navigationState.destination)
        assertTrue(navigationState.routePolyline.isEmpty())
        assertEquals(NavStatus.IDLE, navigationState.status)
    }

    // 自动获取定位
    @Test
    fun initUpdateCurrentLocation() {
        val viewModel = createViewModel()
        val state = viewModel.locationState.value

        assertTrue(state.currentLocation != null)
        assertEquals(28.2250, state.currentLocation!!.latitude)
        assertEquals(112.9400, state.currentLocation.longitude)
        assertTrue(state.lastUpdated > 0L)
    }

    // 设置目的地
    @Test
    fun onSetDestination() {
        val viewModel = createViewModel()

        val destination = Location(28.1000, 112.9000)
        viewModel.setDestination(destination)

        val state = viewModel.navigationState.value
        assertEquals(destination, state.destination)
        assertEquals(NavStatus.NAVIGATING, state.status)
    }

    // 更新路线
    @Test
    fun onUpdateRoute() {
        val viewModel = createViewModel()

        val route = listOf(
            Location(28.2282, 112.9388),
            Location(28.1000, 112.9000)
        )
        viewModel.updateRoute(route)

        assertEquals(route, viewModel.navigationState.value.routePolyline)
    }

    // 地图定位回调
    @Test
    fun initChangeCurrentLocation() {
        val viewModel = createViewModel()

        viewModel.onLocationChanged(28.3000, 113.0000)

        val state = viewModel.locationState.value
        assertEquals(28.3000, state.currentLocation!!.latitude)
        assertEquals(113.3000, state.currentLocation!!.longitude)
        assertTrue(state.lastUpdated > 0L)
    }

    // 手动回到当前位置
    @Test
    fun onBackToCurrentLocation() {
        val viewModel = createViewModel()

        viewModel.backToCurrentLocation()

        val state = viewModel.navigationState.value
        assertTrue(state.destination != null)
        assertEquals(viewModel.locationState.value.currentLocation, state.destination)
    }

    // 无当前位置时无法手动定位
    @Test
    fun onBackToCurrentLocationWithoutLocation() {
        val viewModel = createViewModel(
            fakeLocation = FakeLocationClient(emitsLocation = false)
        )

        viewModel.backToCurrentLocation()

        assertNull(viewModel.navigationState.value.destination)
    }

    // 定位权限
    @Test
    fun hasLocationPermission() {
        val grantedViewModel = createViewModel(FakeLocationClient(permissionGranted = true))
        val deniedViewModel = createViewModel(FakeLocationClient(permissionGranted = false))

        assertTrue(grantedViewModel.hasLocationPermission())
        assertFalse(grantedViewModel.hasLocationPermission())
    }

    // 权限授予结果
    @Test
    fun hasLocationPermissionResult() {
        val viewModel = createViewModel(FakeLocationClient(permissionGranted = false))

        viewModel.onPermissionResult(true)

        assertTrue(viewModel.locationState.value.isLocationEnabled)
    }

    // 未获得权限
    @Test
    fun onHasPermissionFailed() {
        val viewModel = createViewModel(FakeLocationClient(permissionGranted = false))

        viewModel.onPermissionResult(false)

        assertFalse(viewModel.locationState.value.isLocationEnabled)
        assertEquals(NavStatus.ERROR, viewModel.navigationState.value.status)
    }

    // 定位失败
    @Test
    fun onUpdatesLocationFailure() {
        val viewModel = createViewModel(
            fakeLocation = FakeLocationClient(emitsLocation = false)
        )

        assertEquals(NavStatus.ERROR, viewModel.navigationState.value.status)
    }
}