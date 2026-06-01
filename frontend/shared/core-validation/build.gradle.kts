plugins {
    id("shared")
}


kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.utils.pure)
        }
    }
}