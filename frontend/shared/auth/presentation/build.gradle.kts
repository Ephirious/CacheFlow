plugins {
    id("presentation")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.shared.auth.domain)
            }
        }
    }
}