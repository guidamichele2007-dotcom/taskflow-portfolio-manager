plugins {
    id("omnilife.kmp.module")
    id("app.cash.sqldelight")
}

// Same gating as core/core-search/build.gradle.kts and
// domain/domain-task/build.gradle.kts — only this module's SQLDelight
// driver dependency needs it (see build-logic/build.gradle.kts for why the
// convention plugin itself avoids a compile-time AGP dependency).
val androidSdkAvailable =
    providers.environmentVariable("ANDROID_HOME").isPresent ||
        providers.environmentVariable("ANDROID_SDK_ROOT").isPresent ||
        file("$rootDir/local.properties").let { it.exists() && it.readText().contains("sdk.dir") }

val isMacOsHost = System.getProperty("os.name").contains("Mac", ignoreCase = true)

sqldelight {
    databases {
        create("SyncDatabase") {
            packageName.set("com.omnilife.core.sync.persistence")
        }
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:core-common"))
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

// DatabaseDriverFactory (expect/actual class) is still Beta in Kotlin 2.0.21.
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
}
