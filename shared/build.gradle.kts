import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
}

kotlin {
    //select iOS target platform depending on the Xcode environment variables
    val iOSTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
            ::iosArm64
        else
            ::iosX64

    iOSTarget("ios") {
        binaries {
            framework {
                baseName = "shared"
            }
        }
    }

    jvm("android")

    sourceSets["commonMain"].dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
        implementation("io.ktor:ktor-client-core:1.3.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
    }

    sourceSets["androidMain"].dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib")
        implementation("io.ktor:ktor-client-android:1.3.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.3")
    }

    sourceSets["iosMain"].dependencies {
        implementation("io.ktor:ktor-client-ios:1.3.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:1.3.3")
    }
}

val packForXcode by tasks.creating(Sync::class) {
    /**
     * Sets a directory for the framework and determines the correct framework to build based on the
     * selected target in the Xcode projetc, with a default of DEBUG
     */
    val targetDir = File(buildDir, "xcode-frameworks")

    /// selecting the right configuration for the iOS
    /// framework depending on the environment
    /// variables set by Xcode build
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val framework = kotlin.targets
        .getByName<KotlinNativeTarget>("ios")
        .binaries.getFramework(mode)
    inputs.property("mode", mode)
    dependsOn(framework.linkTask)

    /**
     * Copies the files from the build directory into the framework directory
     */
    from({ framework.outputDirectory })
    into(targetDir)

    /// generate a helpful ./gradlew wrapper with embedded Java path
    /**
     * a bash script named gradlew is created in the framework directory the Xcode will call to build
     * the shared framework.
     * The script uses the version of the JDK that's embedded in Android Studio
     */
    doLast {
        val gradlew = File(targetDir, "gradlew")
        gradlew.writeText("#!/bin/bash\n"
            + "export 'JAVA_HOME=${System.getProperty("java.home")}'\n"
            + "cd '${rootProject.rootDir}'\n"
            + "./gradlew \$@\n")
        gradlew.setExecutable(true)
    }
}
// specify that the shared code build task depends on the packForXcode task
tasks.getByName("build").dependsOn(packForXcode)