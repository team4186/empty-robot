import edu.wpi.first.deployutils.deploy.artifact.FileTreeArtifact
import edu.wpi.first.gradlerio.deploy.roborio.FRCJavaArtifact
import edu.wpi.first.gradlerio.deploy.roborio.RoboRIO
import edu.wpi.first.toolchain.NativePlatforms

plugins {
    kotlin("jvm")
    id("edu.wpi.first.GradleRIO")
}

group = "org.aztechs"
version = "2025"

kotlin {
    jvmToolchain(17)
}

// Define my targets (RoboRIO) and artifacts (deployable files)
// This is added by GradleRIO's backing project DeployUtils.
deploy {
    targets {
        val roborio by register<RoboRIO>(name = "roborio") {
            // Team number is loaded either from the .wpilib/wpilib_preferences.json
            // or from command line. If not found an exception will be thrown.
            // You can use getTeamOrDefault(team) instead of getTeamNumber if you
            // want to store a team number in this file.
            team = 4186
//            debug = project.frc.getDebugOrDefault(false)
            directory = "/home/lvuser/deploy"
        }

        roborio.artifacts {
            register<FRCJavaArtifact>("frcJava") {
                dependsOn(tasks.jar.get())
                setJarTask(tasks.jar.get())
            }

            register<FileTreeArtifact>("frcStaticFileDeploy") {
                files(project.fileTree("src/main/deploy"))
            }
        }
    }
}

wpi {
    // Simulation configuration (e.g. environment variables).
    with(sim) {
        addGui().defaultEnabled.set(true)
        addDriverstation()
    }

    with(java) {
        // Configure jar and deploy tasks
        configureExecutableTasks(tasks.jar.get())
        configureTestTasks(tasks.test.get())
        // Set to true to use debug for JNI.
        debugJni.set(false)
    }
}

dependencies {
    with(wpi.java) {
        deps.wpilib().forEach { implementation(it.get()) }
        vendor.java().forEach { implementation(it.get()) }

        // `roborio` prefix comes from the `deploy.target` section
        deps.wpilibJniDebug(NativePlatforms.roborio).forEach { "roborioDebug"(it.get()) }
        vendor.jniDebug(NativePlatforms.roborio).forEach { "roborioDebug"(it.get()) }

        deps.wpilibJniRelease(NativePlatforms.roborio).forEach { "roborioRelease"(it.get()) }
        vendor.jniRelease(NativePlatforms.roborio).forEach { "roborioRelease"(it.get()) }

        deps.wpilibJniDebug(NativePlatforms.desktop).forEach { nativeDebug(it) }
        vendor.jniDebug(NativePlatforms.desktop).forEach { nativeDebug(it) }

        deps.wpilibJniRelease(NativePlatforms.desktop).forEach { nativeRelease(it) }
        vendor.jniRelease(NativePlatforms.desktop).forEach { nativeRelease(it) }
    }

    wpi.sim.enableRelease().forEach { simulationRelease(it) }
    wpi.sim.enableDebug().forEach { simulationDebug(it) }

    testImplementation(platform("io.kotest:kotest-bom:5.8.0"))
    testImplementation("io.kotest:kotest-runner-junit5")
    testImplementation("io.kotest:kotest-assertions-core")
    testImplementation("io.mockk:mockk:1.13.9")
}

tasks {
    jar {
        group = "build"
        description = """
            Setting up my Jar File. In this case, adding all libraries into the main jar ('fat jar')
            in order to make them all available at runtime. Also adding the manifest so WPILib
            knows where to look for our Robot Class.
        """.trimIndent()
        dependsOn(configurations.runtimeClasspath)

        manifest {
            attributes["Main-Class"] = "frc.robot.MainKt"
        }

        from(
            configurations
                .runtimeClasspath
                .get()
                .map { if (it.isDirectory) it else zipTree(it) }
        )
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    test {
        useJUnitPlatform()
    }
}
