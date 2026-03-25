@file:OptIn(ExperimentalKotlinGradlePluginApi::class, ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl


plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    jvm()
    js(IR) {
        browser()
    }
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xexpect-actual-classes"
        )
        optIn.addAll(
            "kotlin.js.ExperimentalJsExport"
        )
    }
}