plugins {
    id("omnilife.kmp.module")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain:domain-account"))
            implementation(project(":core:core-designtokens"))
        }
    }
}
