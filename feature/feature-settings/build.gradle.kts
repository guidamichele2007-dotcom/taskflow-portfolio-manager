plugins {
    id("omnilife.kmp.module")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain:domain-account"))
            // core-designsystem's own `api` export carries core-designtokens transitively.
            implementation(project(":core:core-designsystem"))
            // Settings surfaces live sync status (read-only) alongside the persisted Setting
            // catalog — no other core-sync API is needed here (SET §2 "sync settings").
            implementation(project(":core:core-sync"))
            implementation(compose.foundation)
        }
    }
}
