plugins {
    id("omnilife.kmp.module")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:core-common"))
            implementation(project(":core:core-security"))
        }
    }
}
