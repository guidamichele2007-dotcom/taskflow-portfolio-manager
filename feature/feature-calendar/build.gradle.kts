plugins {
    id("omnilife.kmp.module")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain:domain-calendar"))
            implementation(project(":core:core-designtokens"))
        }
    }
}
