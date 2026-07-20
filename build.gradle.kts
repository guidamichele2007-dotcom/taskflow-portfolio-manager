// Root build script. Deliberately minimal: per-module configuration lives in
// the `omnilife.kmp.module` convention plugin (build-logic/), not here.
// See README-BUILD.md for the full rationale.

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
