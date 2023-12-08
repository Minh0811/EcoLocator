// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.2")
        classpath("io.realm:realm-gradle-plugin:10.9.0")
    }
}

// Removed allprojects block repository declarations
allprojects {
    // No repository declarations are needed here
}

// Rest of your build script...
