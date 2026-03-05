plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js(IR) {
        outputModuleName = "k2ts"
        browser()
        binaries.library()
        generateTypeScriptDefinitions()
        compilerOptions {
            target = "es2015"
            freeCompilerArgs.addAll(listOf("-Xir-per-module=false"))
        }

        useEsModules()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.root.presentation)
        }
    }
}
