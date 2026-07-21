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
        jvmTest.dependencies {
            implementation(compose.desktop.uiTestJUnit4)
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.omnilife.core.designsystem.gallery.GalleryAppKt"
    }
}
