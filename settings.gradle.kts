pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "omnilife"

// ---------------------------------------------------------------------------
// shared — modulo aggregatore / confine di export per iOS (TDR-01, TDR-18)
// ---------------------------------------------------------------------------
include(":shared")

// ---------------------------------------------------------------------------
// core — i 10 Servizi Core (Technical Architecture Bible §13 §4) + 3 moduli
// di supporto trasversale al bootstrap (common/designtokens/testing)
// ---------------------------------------------------------------------------
include(":core:core-common")
include(":core:core-eventbus")
include(":core:core-graph")
include(":core:core-moduleregistry")
include(":core:core-capture")
include(":core:core-search")
include(":core:core-notifications")
include(":core:core-insight")
include(":core:core-sync")
include(":core:core-backup")
include(":core:core-security")
include(":core:core-designtokens")
include(":core:core-testing")

// ---------------------------------------------------------------------------
// domain — un modulo per ciascuno degli 8 domini L3 (Technical Architecture
// Bible §02 §1 / Engineering Plan, Epic con proprie entità)
// ---------------------------------------------------------------------------
include(":domain:domain-task")
include(":domain:domain-finance")
include(":domain:domain-habit")
include(":domain:domain-calendar")
include(":domain:domain-note")
include(":domain:domain-health")
include(":domain:domain-goal")
include(":domain:domain-account")

// ---------------------------------------------------------------------------
// feature — presentazione L1/L2, una per Epic con schermate dedicate
// (Engineering Plan §01 §4)
// ---------------------------------------------------------------------------
include(":feature:feature-core")
include(":feature:feature-capture")
include(":feature:feature-task")
include(":feature:feature-finance")
include(":feature:feature-habit")
include(":feature:feature-calendar")
include(":feature:feature-note")
include(":feature:feature-health")
include(":feature:feature-goal")
include(":feature:feature-search")
include(":feature:feature-notifications")
include(":feature:feature-settings")

// ---------------------------------------------------------------------------
// platform — i 6 Adattatori L5 (Technical Architecture Bible §01 §5) + il
// surface widget (proiezione di L1, aggiunta per completezza pratica)
// ---------------------------------------------------------------------------
include(":platform:platform-persistence")
include(":platform:platform-calendar")
include(":platform:platform-health")
include(":platform:platform-security")
include(":platform:platform-push")
include(":platform:platform-storage")
include(":platform:platform-widget")

// ---------------------------------------------------------------------------
// androidApp — incluso SOLO se un Android SDK è rilevabile in questo
// ambiente (ANDROID_HOME/ANDROID_SDK_ROOT o local.properties). Il modulo
// esiste comunque nel repository (androidApp/), pronto per essere costruito
// su qualunque macchina con SDK Android installato — non è escluso per
// scelta di design, ma per l'assenza del toolchain in QUESTO sandbox.
// Vedi README-BUILD.md.
// ---------------------------------------------------------------------------
val androidSdkAvailable = System.getenv("ANDROID_HOME") != null ||
    System.getenv("ANDROID_SDK_ROOT") != null ||
    file("local.properties").let { it.exists() && it.readText().contains("sdk.dir") }
if (androidSdkAvailable) {
    include(":androidApp")
}
