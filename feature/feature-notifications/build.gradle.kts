plugins {
    id("omnilife.kmp.module")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:core-notifications"))
            implementation(project(":core:core-designtokens"))
        }
    }
}
