plugins {
    id("omnilife.kmp.module")
    id("app.cash.sqldelight")
}

// Same gating as `domain-task`'s build.gradle.kts — see README-BUILD.md §11.
val androidSdkAvailable =
    providers.environmentVariable("ANDROID_HOME").isPresent ||
        providers.environmentVariable("ANDROID_SDK_ROOT").isPresent ||
        file("$rootDir/local.properties").let { it.exists() && it.readText().contains("sdk.dir") }

val isMacOsHost = System.getProperty("os.name").contains("Mac", ignoreCase = true)

sqldelight {
    databases {
        create("AccountDatabase") {
            packageName.set("com.omnilife.domain.account.persistence")
        }
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:core-security"))
            // `api`: this module's public surface (Setting, OnboardingState,
            // SqlDelightSettingsRepository...) exposes core-common types directly in its
            // signatures — same reasoning as domain-task's build.gradle.kts.
            api(project(":core:core-common"))
            api(libs.kotlinx.datetime)
            api("app.cash.sqldelight:runtime:2.0.2")
        }
        jvmMain.dependencies {
            implementation("app.cash.sqldelight:sqlite-driver:2.0.2")
        }
        if (androidSdkAvailable) {
            sourceSets.getByName("androidMain").dependencies {
                implementation("app.cash.sqldelight:android-driver:2.0.2")
            }
        }
        if (isMacOsHost) {
            sourceSets.getByName("iosMain").dependencies {
                implementation("app.cash.sqldelight:native-driver:2.0.2")
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
}
