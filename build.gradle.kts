@file:OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

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

    // Add Kotlin/JVM target
    jvm {
        compilerOptions {
            optIn.add("kotlin.ExperimentalUnsignedTypes")
        }
        mainRun { mainClass = "MainKt" }

    }

    // Add Kotlin/JS target
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

    // Add Kotlin/WASM target
    wasmJs {
        browser {
            testTask {
                useKarma {
                    useChromium()
                }
            }
        }
        binaries.executable()
    }

    // Add Kotlin/Native target based on host OS
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

    // Configure source sets and dependencies
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
        wasmJsMain.dependencies {
            implementation(libs.kotlinxBrowser)
        }

    }

    applyDefaultHierarchyTemplate()
}
