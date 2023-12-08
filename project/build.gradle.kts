// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Project-level build.gradle.kts
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.5.1") // Android Gradle Plugin
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31") // Replace with your Kotlin version
    }
}

allprojects {
    repositories {
    }
}
