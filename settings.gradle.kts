enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    plugins {
        id("edu.wpi.first.GradleRIO") version "2024.1.1"
        kotlin("jvm") version "1.9.20"
    }

    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven {
            name = "wpilib"
            url = uri("~/wpilib/2024/maven")
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        maven("https://frcmaven.wpi.edu/artifactory/release/")
        maven("https://dev.studica.com/maven/release/2024/")
        maven("https://maven.ctr-electronics.com/release/")
        maven("https://maven.revrobotics.com/")
    }
}

include(":robot")
