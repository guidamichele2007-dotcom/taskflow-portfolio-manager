plugins {
    `kotlin-dsl`
}

repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    // Exposes the Kotlin Multiplatform Gradle Plugin API to the precompiled
    // convention script plugins in src/main/kotlin.
    //
    // Deliberately NOT depending on the Android Gradle Plugin here: doing so
    // would make its resolution eager (part of build-logic's own compile
    // classpath) even though the Android target is only ever applied
    // conditionally at runtime (see omnilife.kmp.module.gradle.kts). The
    // android{} extension is instead configured dynamically via
    // `withGroovyBuilder`, so AGP is resolved lazily, only on environments
    // where the Android branch actually runs.
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")

    // Lint/formatter plugins (README-BUILD.md §7). Versions kept in sync by
    // hand with gradle/libs.versions.toml — build-logic is a separate Gradle
    // build and does not automatically see the root catalog.
    implementation("org.jlleitschuh.gradle:ktlint-gradle:12.1.1")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.7")
}
