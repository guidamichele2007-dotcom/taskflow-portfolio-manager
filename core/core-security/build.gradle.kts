plugins {
    id("omnilife.kmp.module")
}

// Same gating as every other module needing a platform-specific dependency
// (see domain/domain-task/build.gradle.kts) — only relevant when an Android
// SDK is actually present to resolve `androidx.security:security-crypto`
// against.
val androidSdkAvailable =
    providers.environmentVariable("ANDROID_HOME").isPresent ||
        providers.environmentVariable("ANDROID_SDK_ROOT").isPresent ||
        file("$rootDir/local.properties").let { it.exists() && it.readText().contains("sdk.dir") }

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:core-common"))
            implementation(libs.kotlinx.datetime)
        }
        if (androidSdkAvailable) {
            sourceSets.getByName("androidMain").dependencies {
                implementation("androidx.security:security-crypto:1.1.0-alpha06")
            }
        }
    }
}
