plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.devtools.ksp") version "2.3.5" apply false
    id("com.google.dagger.hilt.android") version "2.59.2" apply false
    id("androidx.room") version "2.8.4" apply false
    kotlin("plugin.serialization") version "2.3.20" apply false
}