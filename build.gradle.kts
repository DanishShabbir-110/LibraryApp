// Top-level build file where you can add configuration options common to all subprojects /modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}
buildscript {
    repositories {
        google()
    }
    dependencies {
        val nav_version = "2.10.1"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
    }
}