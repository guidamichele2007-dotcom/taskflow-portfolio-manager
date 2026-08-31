plugins {
    id("omnilife.kmp.module")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain:domain-task"))
            // core-designsystem's own `api` export carries core-designtokens transitively
            // (same convention as feature-core's build.gradle.kts).
            implementation(project(":core:core-designsystem"))
            // L2 orchestration bridges (Technical Architecture Bible §03): Task<->Search,
            // Task<->Notifications, Task<->Sync outbox live here, never in domain-task or a
            // core-* module — see TaskSearchIndexBridge/TaskNotificationBridge/TaskSyncOutboxBridge.
            implementation(project(":core:core-search"))
            implementation(project(":core:core-notifications"))
            implementation(project(":core:core-sync"))
            implementation(libs.kotlinx.serialization.json)
            implementation(compose.foundation)
            // TaskDetailBottomSheet/TaskCreateBottomSheet expose SheetState directly (same as
            // core-designsystem's own OmniBottomSheet, whose material3 dependency does not leak
            // here transitively — it's `implementation`, not `api`, in core-designsystem itself).
            implementation(compose.material3)
        }
    }
}
