plugins {
    id("presentation")
    id("ksp-on")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.shared.transactions.domain)
                implementation(projects.shared.editors.presentation)
                implementation(projects.shared.editors.domain)
                implementation(projects.shared.coreValidation)
            }
        }
    }
}

