// Root build script. Deliberately minimal: per-module configuration lives in
// the `omnilife.kmp.module` convention plugin (build-logic/), not here.
// See README-BUILD.md for the full rationale.
//
// Plugins used by more than one module but not by the shared convention
// plugin are declared here with `apply false` and their version pinned once
// — subprojects then apply them without a version. Applying the same plugin
// with an explicit version directly in a subproject loads the Kotlin Gradle
// Plugin a second time and breaks the build ("loaded multiple times in
// different subprojects").
plugins {
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21" apply false
    id("app.cash.sqldelight") version "2.0.2" apply false
}

allprojects {
    group = "com.omnilife"
    version = "0.1.0-bootstrap"
}

// Convenience aggregate tasks for local development and CI (Engineering Plan
// §05, "code review checklist" / "testing strategy"). No business logic.
tasks.register("checkAll") {
    group = "verification"
    description = "Runs check on every module that this environment can build (see README-BUILD.md)."
    dependsOn(subprojects.map { "${it.path}:check" })
}
