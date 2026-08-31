plugins {
    id("omnilife.kmp.module")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain:domain-goal"))
            implementation(project(":core:core-designtokens"))
        }
    }
}
