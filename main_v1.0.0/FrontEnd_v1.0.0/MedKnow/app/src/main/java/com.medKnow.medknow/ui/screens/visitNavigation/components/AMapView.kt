package com.medKnow.medknow.ui.screens.visitNavigation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.amap.api.maps.AMap
import com.amap.api.maps.TextureMapView
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.maps.model.PolylineOptions
import com.medKnow.medknow.ui.screens.visitNavigation.visitNavigationViewModel.VisitNavigationViewModel

// 高德地图组件
@Composable
fun AMapView (
    modifier: Modifier = Modifier,
    onMapReady: (AMap) -> Unit = {},
    onLocationChange: (LatLng) -> Unit = {},
    navigationState: VisitNavigationViewModel.NavigationUiState? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        TextureMapView(context).apply {
            // 开启定位图层与实时定位
            map.isMyLocationEnabled = true
            map.myLocationStyle = MyLocationStyle()
                .myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)

            // 定位变化时回传
            map.setOnMyLocationChangeListener { location ->
                onLocationChange(LatLng(location.latitude, location.longitude))
            }
        }
    }

    // 绑定地图生命周期
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { mapView }
    )

    // 地图就绪后回调
    LaunchedEffect(mapView) {
        onMapReady(mapView.map)
    }

    // 路线绘制
    LaunchedEffect(navigationState?.routePolyline) {
        val points = navigationState?.routePolyline ?: return@LaunchedEffect
        if (points.size >= 2) {
            val latLngs = points.map { LatLng(it.latitude, it.longitude) }
            mapView.map.addPolyline(
                PolylineOptions()
                    .addAll(latLngs)
                    .width(2f)
                    .color(android.graphics.Color.BLUE)
            )
        }
    }

}