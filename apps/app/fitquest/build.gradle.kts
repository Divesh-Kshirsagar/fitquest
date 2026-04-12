plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

import java.util.Properties

val envProperties = Properties().apply {
    val envFile = rootProject.file(".env")
    if (envFile.exists()) {
        envFile.inputStream().use(::load)
    }
}

fun envOrDefault(name: String, default: String = ""): String {
    return providers.environmentVariable(name).orNull
        ?: envProperties.getProperty(name)
        ?: default
}

android {
    namespace = "com.example.mobileapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.mobileapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val mapTilerApiKey = envOrDefault("MAPTILER_API_KEY")
        val mapTilerStyleUrl = envOrDefault(
            "MAPTILER_STYLE_URL",
            "https://api.maptiler.com/maps/streets/style.json?key=$mapTilerApiKey"
        )
        buildConfigField("String", "MAPTILER_API_KEY", "\"$mapTilerApiKey\"")
        buildConfigField("String", "MAPTILER_STYLE_URL", "\"$mapTilerStyleUrl\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // DI and state stack used by the capture feature.
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.voyager.screenmodel)
    implementation(libs.orbit.core)
    implementation(libs.orbit.compose)

    // Sensor/location capture and spatial indexing.
    implementation(libs.google.play.services.location)
    implementation(libs.uber.h3)

    // TODO: Re-enable MapLibre once its Maven repository/version is finalized.

    // Local persistence for captured hexes.
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}