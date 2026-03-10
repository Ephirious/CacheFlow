plugins {
    id("presentation")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)

            implementation(projects.shared.interopSample.data)
            implementation(projects.shared.interopSample.presentation)

            implementation(projects.shared.transactions.data)
            implementation(projects.shared.transactions.presentation)

            implementation(projects.shared.stats.presentation)

            implementation(projects.shared.more.data)
            implementation(projects.shared.more.presentation)

            implementation(projects.shared.editors.data)
            implementation(projects.shared.editors.presentation)

            implementation(projects.shared.sync.data)
        }
    }
}