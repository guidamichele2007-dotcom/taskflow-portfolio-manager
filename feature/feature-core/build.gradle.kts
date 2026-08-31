// Home "Oggi" (Sprint 4) + Onboarding (Sprint 5). Compose Multiplatform (TDR-22), same
// JVM/Android-gated target setup as core-designsystem — no iOS target here either (TDR-01: iOS
// stays native SwiftUI). Sprint 4 scoped this module to Core-only; Sprint 5's Vertical Slice
// explicitly requires Home to show real task data and onboarding to create a real first task and
// persist real completion state, so domain-task/domain-account are now genuine dependencies —
// design tokens still arrive transitively via core-designsystem's own `api` export.
plugins {
    id("omnilife.kmp.module")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:core-designsystem"))
            implementation(project(":core:core-search"))
            implementation(project(":core:core-sync"))
            implementation(project(":core:core-notifications"))
            implementation(project(":domain:domain-task"))
            implementation(project(":domain:domain-account"))
            implementation(compose.foundation)
        }
        // core-eventbus isn't used by this module's own production code (core-notifications
        // depends on it internally via `implementation`, not `api`, so it doesn't leak here) —
        // only test code needs it directly, to construct a NotificationBroker for its fakes.
        commonTest.dependencies {
            implementation(project(":core:core-eventbus"))
        }
    }
}
