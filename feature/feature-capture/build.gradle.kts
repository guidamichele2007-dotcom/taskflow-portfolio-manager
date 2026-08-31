plugins {
    id("omnilife.kmp.module")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:core-capture"))
            implementation(project(":core:core-designtokens"))
        }
    }
}
