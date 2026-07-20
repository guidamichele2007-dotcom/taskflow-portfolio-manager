plugins {
    id("omnilife.kmp.module")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain:domain-habit"))
            implementation(project(":core:core-designtokens"))
        }
    }
}
