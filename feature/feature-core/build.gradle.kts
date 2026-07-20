plugins {
    id("omnilife.kmp.module")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:core-moduleregistry"))
            implementation(project(":core:core-designtokens"))
        }
    }
}
