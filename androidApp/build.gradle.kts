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

    // Sprint 6 §14: three real, reproducible variants — Debug (local development, the AGP
    // default), Internal (installable alongside Debug/Release for testers, signed with the same
    // auto-generated debug keystore so no secret needs to exist for it), Release (store-shaped:
    // minified/shrunk, must be signed with a real upload key supplied out-of-band). No variant
    // here fakes a signature or bundles a secret into the repo — see the `signingConfigs` block.
    signingConfigs {
        create("release") {
            // Deliberately never hardcoded: a release build without these four environment
            // variables set is simply unsigned — `assembleRelease`/`bundleRelease` then fails
            // Gradle's own signing-config validation with a clear error, not a silent fake key.
            val storeFilePath = providers.environmentVariable("OMNILIFE_RELEASE_STORE_FILE").orNull
            val storePasswordEnv = providers.environmentVariable("OMNILIFE_RELEASE_STORE_PASSWORD").orNull
            val keyAliasEnv = providers.environmentVariable("OMNILIFE_RELEASE_KEY_ALIAS").orNull
            val keyPasswordEnv = providers.environmentVariable("OMNILIFE_RELEASE_KEY_PASSWORD").orNull
            if (storeFilePath != null && storePasswordEnv != null && keyAliasEnv != null && keyPasswordEnv != null) {
                storeFile = file(storeFilePath)
                storePassword = storePasswordEnv
                keyAlias = keyAliasEnv
                keyPassword = keyPasswordEnv
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isDebuggable = true
        }
        create("internal") {
            // Signed with AGP's own auto-generated debug keystore (~/.android/debug.keystore) —
            // the standard pattern for a tester-installable build that isn't the store release,
            // needing no secret of its own. Installable side-by-side with debug/release thanks to
            // the distinct applicationId suffix.
            applicationIdSuffix = ".internal"
            versionNameSuffix = "-internal"
            isDebuggable = false
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
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
    // NotificationFireReceiver (Sprint 6) posts the real system notification directly via
    // NotificationCompat/NotificationManagerCompat — same androidx.core version core-notifications
    // already depends on for ContextCompat, kept in sync rather than left to transitive resolution.
    implementation("androidx.core:core-ktx:1.13.1")
}
