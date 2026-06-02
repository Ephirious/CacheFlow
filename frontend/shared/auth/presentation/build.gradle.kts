plugins {
    id("presentation")
    id("ksp-on")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.shared.auth.domain)
                implementation(projects.shared.coreValidation)
            }
        }
    }
}