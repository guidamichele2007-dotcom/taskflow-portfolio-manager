plugins {
    id("omnilife.kmp.module")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:core-common"))
            implementation(project(":core:core-eventbus"))
            implementation(project(":core:core-graph"))
            implementation(project(":core:core-moduleregistry"))
            implementation(project(":core:core-capture"))
            implementation(project(":core:core-search"))
            implementation(project(":core:core-notifications"))
            implementation(project(":core:core-insight"))
            implementation(project(":core:core-sync"))
            implementation(project(":core:core-backup"))
            implementation(project(":core:core-security"))
            implementation(project(":core:core-designtokens"))
            implementation(project(":domain:domain-task"))
            implementation(project(":domain:domain-finance"))
            implementation(project(":domain:domain-habit"))
            implementation(project(":domain:domain-calendar"))
            implementation(project(":domain:domain-note"))
            implementation(project(":domain:domain-health"))
            implementation(project(":domain:domain-goal"))
            implementation(project(":domain:domain-account"))
        }
    }
}
