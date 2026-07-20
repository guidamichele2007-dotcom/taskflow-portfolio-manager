plugins {
    id("omnilife.kmp.module")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain:domain-note"))
            implementation(project(":core:core-designtokens"))
        }
    }
}
