plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.medKnow.medknow"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.medKnow.medknow"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8090/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            buildConfigField("String", "BASE_URL", "\"https://api.medknow.com/\"")
        }
        debug {
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8090/\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    //BOM
    implementation(platform(libs.compose.bom))

    //核心
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    //Compose(使用 BOM 统一版本）
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    //生命周期与ViewModel
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)

    //协程
    implementation(libs.kotlinx.coroutines.android)

    //网络层
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)

    //测试
    testImplementation(libs.mockk)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // desugaring
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // 加载网络图片
    implementation("io.coil-kt:coil-compose:2.6.0")

    // 高德地图
    implementation("com.amap.api:3dmap:latest.integration")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // DataStore
    implementation(libs.androidx.datastore.preferences)

}

// 统一 Kotlin、Java、KSP 版本
kotlin {
    jvmToolchain(21)
}