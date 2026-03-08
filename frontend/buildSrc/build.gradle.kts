plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.gradle.plugin.kotlin)
    implementation(libs.gradle.plugin.serialization)
    implementation(libs.gradle.plugin.ksp)
    implementation(libs.gradle.plugin.sqldelight)


    //https://stackoverflow.com/a/70878181
    // An error in the editor, although the dependencies are correctly resolved.
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

kotlin {
    jvmToolchain(17)
}