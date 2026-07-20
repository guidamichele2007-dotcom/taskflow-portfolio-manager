plugins {
    id("omnilife.kmp.module")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain:domain-task"))
            implementation(project(":core:core-designtokens"))
        }
    }
}
