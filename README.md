# Risc V Simulator

This is a RISC-V simulator written in Kotlin, supporting the following targets:
- [Kotlin/Native](#kotlinnative)
- [Kotlin/JVM](#kotlinjvm)
- [Kotlin/JS](#kotlinjs)
- [Kotlin/Wasm](#kotlinwasm)

# Requirements:
    - Java JDK 11 or higher
    - JavaHome environment variable set

# Structure:
```
commonMain      # Shared code for all targets. The main logic of the RISC-V simulator resides here.
├── nativeMain  # Kotlin/Native specific code. Only the main function is defined here.
├── jvmMain     # Kotlin/JVM specific code. Only the main function is defined here.
├── jsMain      # Kotlin/JS specific code. Only the main function and and HTML integration code are defined here.
└── wasmJsMain  # Kotlin/Wasm specific code. Only the main function and and HTML integration code are defined here.

commonTest      # Shared tests for all targets
```


## Kotlin/Native
For debugging Kotlin/Native applications, you need to install `libxcrypt-compat` library.

On Windows you also need the `mingw-w64` toolchain installed and added to your PATH.\
On Linux, you need to install the build-essential of your distribution.\
On macOS, the Xcode command line tools must be installed.

### Running the Kotlin/Native target
To run the Kotlin/Native target, use the following command:
```bash
# Available commands on Linux
./gradlew runDebugExecutableLinuxX64
./gradlew runDebugExecutableLinuxArm64
./gradlew runReleaseExecutableLinuxX64
./gradlew runReleaseExecutableLinuxArm64

# Available commands on macOS
./gradlew runDebugExecutableMacosX64
./gradlew runDebugExecutableMacosArm64
./gradlew runReleaseExecutableMacosX64
./gradlew runReleaseExecutableMacosArm64

# Available commands on Windows
./gradlew runDebugExecutableMingwX64
./gradlew runReleaseExecutableMingwX64
```

## Kotlin/JVM
To run the Kotlin/JVM target, use the following command:
```bash
./gradlew runJvm
```

## Kotlin/JS
Requires Node.js installed.
To run the Kotlin/JS target, use the following command:
```bash
./gradlew jsBrowserDevelopmentRun
```
or for production build:
```bash
./gradlew jsBrowserProductionRun
```
Then open `http://localhost:8080` in your web browser.

## Kotlin/Wasm
Requires Node.js installed.
To run the Kotlin/Wasm target, use the following command:
```bash
./gradlew wasmBrowserDevelopmentRun
```
or for production build:
```bash
./gradlew wasmBrowserProductionRun
```
Then open `http://localhost:8080` in your web browser.

## Running Tests
To run the tests for all targets, use the following command:
```bash
./gradlew allTests
```
This will execute tests for Kotlin/Native, Kotlin/JVM, Kotlin/JS, and Kotlin/Wasm targets.