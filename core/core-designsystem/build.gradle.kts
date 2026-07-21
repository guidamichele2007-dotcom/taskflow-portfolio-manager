// Compose Multiplatform component library (TDR-22). Targets Android
// (gated, same androidSdkAvailable mechanism as every other module) and
// Desktop/JVM (always on — the verification surface for this sandbox: real
// compile, real compose-ui-test, real screenshots, real gallery app). No
// iOS target: TDR-01 keeps iOS on native SwiftUI, unaffected by this module.
@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

plugins {
    id("omnilife.kmp.module")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core:core-designtokens"))
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.animation)
            implementation(compose.components.resources)
        }
        commonTest.dependencies {
            implementation(compose.uiTest)
        }
        jvmMain.dependencies {
            // Needed to run the GalleryApp (fun main()) and to host the
            // Skiko rendering surface used by desktop compose-ui-test.
            implementation(compose.desktop.currentOs)
        }
        // jvmTest deliberately has no extra dependency: compose.uiTest
        // (declared on commonTest above) already provides runComposeUiTest
        // for the JVM target. compose.desktop.uiTestJUnit4 was tried and
        // reverted — its transitive graph requires androidx.lifecycle /
        // androidx.arch.core straight from dl.google.com, which this
        // sandbox's network policy blocks outright (verified: 403 on
        // CONNECT, and the same artifacts don't exist on Maven Central
        // either) — an environmental constraint, not a code issue.
    }
}

compose.desktop {
    application {
        mainClass = "com.omnilife.core.designsystem.gallery.GalleryAppKt"
    }
}
