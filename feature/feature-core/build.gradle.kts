// Home "Oggi" (Sprint 4). Compose Multiplatform (TDR-22), same JVM/Android-gated
// target setup as core-designsystem — no iOS target here either (TDR-01: iOS stays
// native SwiftUI). Depends exclusively on the four Core services this sprint scopes
// (Core UI Kit, Core Search, Core Sync, Core Notifications) — no domain-* module,
// per the task's explicit constraint; design tokens arrive transitively via
// core-designsystem's own `api` export, so no separate dependency on them either.
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
