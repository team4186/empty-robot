pluginManagement {
    plugins {
        id("edu.wpi.first.GradleRIO") version "2023.2.1"
        kotlin("jvm") version "1.8.0"
    }

    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven {
            name = "wpilib"
            url = uri("~/.wpilib/2023/maven")
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        maven("https://frcmaven.wpi.edu/artifactory/release/")
        maven("https://dev.studica.com/maven/release/2023/")
        maven("https://maven.ctr-electronics.com/release/")
        maven("https://maven.revrobotics.com/")
    }
}
