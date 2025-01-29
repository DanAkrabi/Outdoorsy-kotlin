plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.services) apply false
}

buildscript {

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Add Safe Args classpath for Navigation
        classpath(libs.androidx.navigation.safe.args.gradle.plugin) // Replace with the latest version if needed
//        var hilt_version = "2.48"
        classpath (libs.hilt.android.gradle.plugin)
    }
}
//// Top-level build file where you can add configuration options common to all sub-projects/modules.
//plugins {
//    alias(libs.plugins.android.application) apply false
//    alias(libs.plugins.kotlin.android) apply false
//    alias(libs.plugins.kotlin.compose) apply false
//    alias(libs.plugins.google.services) apply false
//}
