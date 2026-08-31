plugins {
    id("omnilife.kmp.module")
}

// Same gating as core-security/domain-task — only relevant when an Android SDK is actually
// present to resolve `androidx.core` (ContextCompat) against.
val androidSdkAvailable =
    providers.environmentVariable("ANDROID_HOME").isPresent ||
        providers.environmentVariable("ANDROID_SDK_ROOT").isPresent ||
        file("$rootDir/local.properties").let { it.exists() && it.readText().contains("sdk.dir") }

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:core-common"))
            implementation(project(":core:core-eventbus"))
            api(libs.kotlinx.datetime)
        }
        if (androidSdkAvailable) {
            sourceSets.getByName("androidMain").dependencies {
                implementation("androidx.core:core-ktx:1.13.1")
            }
        }
    }
}
