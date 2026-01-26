
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

group = "at.jeb.riscv"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    compilerOptions {
        // Opt-in for unsigned types for JVM target. Native target has it enabled by default.
        optIn.add("kotlin.ExperimentalUnsignedTypes")
    }

    jvm()

    js {
        browser {
            testTask {
                useKarma {
                    useChromium()
                }
            }
        }
        binaries.executable()
    }

    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" && isArm64 -> macosArm64()
        hostOs == "Mac OS X" && !isArm64 -> macosX64()
        hostOs == "Linux" && isArm64 -> linuxArm64()
        hostOs == "Linux" && !isArm64 -> linuxX64()
        isMingwX64 -> mingwX64()
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinxSerializationJson)
            implementation(libs.kotlinxIoCore)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        webMain.dependencies {
            implementation(libs.kotlinxCoroutinesCore)
        }

    }

    applyDefaultHierarchyTemplate()
}
