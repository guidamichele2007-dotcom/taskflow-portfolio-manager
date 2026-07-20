plugins {
    id("omnilife.kmp.module")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain:domain-finance"))
            implementation(project(":core:core-designtokens"))
        }
    }
}
