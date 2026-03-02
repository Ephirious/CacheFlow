plugins {
    id("presentation-setup")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)

            implementation(projects.shared.interopTest.presentation)

            // implementation(projects.shared.interopTest.data)
        }
    }
}