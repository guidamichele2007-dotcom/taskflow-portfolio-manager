/*
 * Convention plugin applied by every shared/core/domain/feature/platform module.
 *
 * Bootstrap scope only (Engineering Plan, ENG-00-1): configures the Kotlin
 * Multiplatform targets consistently across the whole module graph. No
 * business logic lives here.
 *
 * Target gating (both documented in docs/omnilife/technology_decision_record.md
 * TDR-01/TDR-18 and README-BUILD.md):
 *  - jvm(): always enabled. Used in this environment to verify that every
 *    module's commonMain/commonTest actually compiles and its tests run,
 *    standing in for the native targets below where a full platform
 *    toolchain isn't available.
 *  - androidTarget(): enabled only when an Android SDK is detected
 *    (ANDROID_HOME env var or local.properties `sdk.dir`). This is an
 *    environmental gate, not a platform limitation - Android compiles fine
 *    from Linux once the SDK is present.
 *  - iosArm64()/iosSimulatorArm64()/iosX64(): enabled only when the build is
 *    running on a macOS host. This is a fundamental Kotlin/Native
 *    constraint, not specific to any one environment: Apple platform
 *    targets can only be compiled (and definitely only linked/packaged)
 *    from a Mac with Xcode installed.
 */

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jlleitschuh.gradle.ktlint")
    id("io.gitlab.arturbosch.detekt")
}

group = "com.omnilife"
version = "0.1.0-bootstrap"

val androidSdkAvailable = providers.environmentVariable("ANDROID_HOME").isPresent ||
    providers.environmentVariable("ANDROID_SDK_ROOT").isPresent ||
    file("${rootDir}/local.properties").let { it.exists() && it.readText().contains("sdk.dir") }

val isMacOsHost = System.getProperty("os.name").contains("Mac", ignoreCase = true)

extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    if (androidSdkAvailable) {
        // com.android.library is applied and configured only here, inside
        // this runtime-guarded branch: on an environment without an Android
        // SDK this line never executes, so Gradle never attempts to resolve
        // the plugin (see build-logic/build.gradle.kts for why that matters).
        pluginManager.apply("com.android.library")
        androidTarget {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }
    }

    if (isMacOsHost) {
        iosArm64()
        iosSimulatorArm64()
        iosX64()
    }

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
        }
    }

    explicitApi()
}

if (androidSdkAvailable) {
    // Configured dynamically (no compile-time AGP dependency, see above).
    extensions.getByName("android").withGroovyBuilder {
        setProperty(
            "namespace",
            "com.omnilife.${project.path.trimStart(':').replace(':', '.').replace('-', '.')}",
        )
        setProperty("compileSdk", 34)
        getProperty("defaultConfig").withGroovyBuilder {
            setProperty("minSdk", 26)
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

// Lint/formatter (README-BUILD.md §7). ktlint uses its built-in default
// style (.editorconfig at the repo root refines a few base rules); detekt
// uses the shared ruleset in config/detekt/detekt.yml.
//
// detekt's default `source` is the single-platform `main` source set, which
// no KMP module has (Kotlin Multiplatform registers `commonMain`/`jvmMain`/
// etc. instead) — without this override every module's detekt task is
// silently NO-SOURCE and lints nothing.
detekt {
    config.setFrom(rootDir.resolve("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    source.setFrom(
        "src/commonMain/kotlin",
        "src/commonTest/kotlin",
        "src/jvmMain/kotlin",
    )
}

// ktlint scans by Kotlin source set, not by physical directory, so — unlike
// detekt above — it also picks up generated code that a plugin (e.g.
// SQLDelight) registers onto commonMain from build/. Generated code is not
// ours to format.
extensions.configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    filter {
        exclude { entry -> entry.file.path.contains("${File.separatorChar}build${File.separatorChar}") }
    }
}
