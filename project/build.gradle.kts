// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.2") // Android Gradle Plugin
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10") // Kotlin Gradle Plugin
        classpath("io.realm:realm-gradle-plugin:10.11.1")
    }
}

// Removed allprojects block repository declarations
allprojects {
    // No repository declarations are needed here
}

// Rest of your build script...
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.2" apply false
    id ("io.realm.kotlin") version "1.11.0" apply false
}