plugins {
    id("omnilife.kmp.module")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:core-search"))
            // core-designsystem's own `api` export carries core-designtokens transitively.
            implementation(project(":core:core-designsystem"))
            implementation(compose.foundation)
        }
    }
}
