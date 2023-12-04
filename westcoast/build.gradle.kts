import edu.wpi.first.deployutils.deploy.artifact.FileTreeArtifact
import edu.wpi.first.gradlerio.deploy.roborio.FRCJavaArtifact
import edu.wpi.first.gradlerio.deploy.roborio.RoboRIO
import edu.wpi.first.gradlerio.wpi.dependencies.tools.ToolInstallTask
import edu.wpi.first.toolchain.NativePlatforms
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    java
    id("edu.wpi.first.GradleRIO")
}

group = "org.aztechs"
version = "2023"

JavaVersion.VERSION_11.let {javaVersion ->
    java {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = javaVersion.majorVersion
        }
    }
}

// Define my targets (RoboRIO) and artifacts (deployable files)
// This is added by GradleRIO's backing project DeployUtils.
deploy {
    targets {
        register<RoboRIO>(name = "roborio") {
            // Team number is loaded either from the .wpilib/wpilib_preferences.json
            // or from command line. If not found an exception will be thrown.
            // You can use getTeamOrDefault(team) instead of getTeamNumber if you
            // want to store a team number in this file.
            team = project.frc.teamNumber
//            debug = project.frc.getDebugOrDefault(false)
            directory = "/home/lvuser/deploy"
            this.artifacts {
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

    implementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("io.kotest:kotest-runner-junit5:5.5.4")
    testImplementation("io.kotest:kotest-assertions-core:5.5.4")
    testImplementation("io.mockk:mockk:1.13.2")
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
                .map { if(it.isDirectory) it else zipTree(it) }
        )
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    test {
        useJUnitPlatform()
    }

    ToolInstallTask.setToolsFolder(project.rootProject.file(".wpilib/tools/"))
    create<JavaExec>("launchShuffleboard") {
        group = "tools"
        classpath = files(project.rootProject.file(".wpilib/tools/ShuffleBoard.jar"))
    }

    create<JavaExec>("launchSmartdash") {
        group = "tools"
        classpath = files(project.rootProject.file(".wpilib/tools/ShuffleBoard.jar"))
    }
}
