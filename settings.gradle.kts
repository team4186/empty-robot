rootProject.name = "empty-robot"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    plugins {
        // Versions at https://plugins.gradle.org/plugin/edu.wpi.first.GradleRIO
        id("edu.wpi.first.GradleRIO") version "2025.1.1"
        // Versions at https://kotlinlang.org/docs/releases.html#release-details
        kotlin("jvm") version "2.1.0"
    }

    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven {
            name = "wpilib"
            url = uri("~/wpilib/2025/maven")
        }
        if("windows" in System.getProperty("os.name").lowercase()) {
            maven {
                name = "wpilib-alt"
                url = uri("/Users/Public/wpilib/2025/maven")
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven {
            name = "wpilib"
            url = uri("~/wpilib/2025/maven")
        }
        if("windows" in System.getProperty("os.name").lowercase()) {
            maven {
                name = "wpilib-alt"
                url = uri("/Users/Public/wpilib/2025/maven")
            }
        }
        maven("https://frcmaven.wpi.edu/artifactory/release/")
        maven("https://dev.studica.com/maven/release/2025/")
        maven("https://maven.ctr-electronics.com/release/")
        maven("https://maven.revrobotics.com/")
    }
}

include(":robot")
