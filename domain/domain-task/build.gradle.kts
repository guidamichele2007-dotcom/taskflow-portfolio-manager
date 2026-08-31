plugins {
    id("omnilife.kmp.module")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("app.cash.sqldelight")
}

// Same gating as the omnilife.kmp.module convention plugin (build-logic/) —
// duplicated here because only this module needs a platform-specific
// SQLDelight driver dependency; see README-BUILD.md §11.
val androidSdkAvailable =
    providers.environmentVariable("ANDROID_HOME").isPresent ||
        providers.environmentVariable("ANDROID_SDK_ROOT").isPresent ||
        file("$rootDir/local.properties").let { it.exists() && it.readText().contains("sdk.dir") }

val isMacOsHost = System.getProperty("os.name").contains("Mac", ignoreCase = true)

sqldelight {
    databases {
        create("TaskDatabase") {
            packageName.set("com.omnilife.domain.task.persistence")
        }
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // `api`, not `implementation`: this module's public surface (Task,
            // TaskList, RecurrenceRule, TaskEvent, SqlDelightTaskRepository...)
            // exposes types from all three directly in its signatures, so
            // consumers (feature-task) need them on their own compile
            // classpath too — see README-BUILD.md §11.
            api(project(":core:core-common"))
            api(project(":core:core-eventbus"))
            api(libs.kotlinx.datetime)
            api("app.cash.sqldelight:runtime:2.0.2")
            implementation(libs.kotlinx.serialization.json)
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

// DatabaseDriverFactory (expect/actual class) is still Beta in Kotlin 2.0.21;
// this only silences the compiler warning, it does not change behavior.
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
}
