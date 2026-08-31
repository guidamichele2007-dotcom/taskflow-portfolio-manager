// Android application entry point (TDR-01: Kotlin/Jetpack Compose UI).
//
// This module is deliberately NOT part of the omnilife.kmp.module convention
// plugin (build-logic/) — that plugin builds *library* modules consumed by
// both platforms. An application module has its own plugin
// (com.android.application) and its own concerns (manifest, entry point,
// signing) that don't apply to :shared or any core/domain/feature/platform
// module.
//
// Only ever evaluated when the Android SDK is present, because this module
// is included conditionally from settings.gradle.kts. Using the typed AGP
// DSL directly here is therefore safe (see build-logic/build.gradle.kts for
// why build-logic itself avoids a compile-time AGP dependency).
plugins {
    id("com.android.application") version "8.5.2"
    id("org.jetbrains.kotlin.android") version "2.0.21"
    // Kotlin 2.0+ moved the Compose compiler out of the Kotlin plugin itself;
    // buildFeatures.compose = true alone is no longer sufficient.
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
}

android {
    namespace = "com.omnilife.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.omnilife.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0-bootstrap"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // TDR-37 (Sprint 5): androidApp depends on :shared PLUS the specific feature-* modules whose
    // screens it actually renders — shared itself is unchanged (still only core-*/domain-*). The
    // composition root (AppContainer.kt) also needs direct core-*/domain-* access to construct
    // concrete repositories/bridges (TDR-19: manual DI, composition root knows everything).
    implementation(project(":shared"))
    implementation(project(":feature:feature-core"))
    implementation(project(":feature:feature-task"))
    implementation(project(":feature:feature-search"))
    implementation(project(":feature:feature-settings"))
    implementation(project(":core:core-designsystem"))
    implementation(project(":core:core-eventbus"))
    implementation(project(":core:core-search"))
    implementation(project(":core:core-sync"))
    implementation(project(":core:core-notifications"))
    implementation(project(":domain:domain-task"))
    implementation(project(":domain:domain-account"))

    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.9.2")
}
