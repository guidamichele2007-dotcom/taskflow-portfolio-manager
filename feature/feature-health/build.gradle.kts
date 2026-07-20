plugins {
    id("omnilife.kmp.module")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain:domain-health"))
            implementation(project(":core:core-designtokens"))
        }
    }
}
