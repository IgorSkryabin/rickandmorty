plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("androidx.room")
    kotlin("plugin.serialization")
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "com.example.rickandmorty"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.rickandmorty"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Compose Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    // Compose Material
    implementation(libs.androidx.compose.material)
    // Compose Navigation
    implementation(libs.androidx.navigation.compose)
    // Compose ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    // Moshi
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    // OkHttp logging
    implementation(libs.logging.interceptor)
    // Serialization
    implementation(libs.kotlinx.serialization.json)
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    // Hilt Navigation Compose
    implementation(libs.androidx.hilt.navigation.compose)
    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}