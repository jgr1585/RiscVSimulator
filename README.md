# RISC-V Simulator in Kotlin

A comprehensive RISC-V instruction set simulator written in **Kotlin**, demonstrating multiplatform development with support for multiple compilation targets.

## About Kotlin

**Kotlin** is a statically-typed, expressive programming language developed by JetBrains that runs on the Java Virtual Machine, native platforms, and JavaScript. Key features include:

- **Concise Syntax**: Reduces boilerplate code compared to Java
- **Null Safety**: Built-in null safety features to prevent NullPointerExceptions
- **Interoperability**: Seamless interoperability with Java libraries and code
- **Multiplatform Support**: Write once, compile to multiple targets:
  - **Kotlin/JVM**: Runs on Java Virtual Machine
  - **Kotlin/Native**: Compiles to native binaries for desktop/mobile
  - **Kotlin/JS**: Transpiles to JavaScript for web browsers
  - **Kotlin/Wasm**: Compiles to WebAssembly for high-performance web

This project showcases Kotlin's multiplatform capabilities by implementing the same RISC-V simulator that compiles to all these targets.

## Project Overview

This RISC-V simulator is built to run on multiple platforms:
- **[Kotlin/Native](#kotlinnative)**: Native executables for Linux, macOS, and Windows
- **[Kotlin/JVM](#kotlinjvm)**: Java-based execution
- **[Kotlin/JS](#kotlinjs)**: Web browser with interactive UI
- **[Kotlin/Wasm](#kotlinwasm)**: WebAssembly for web browsers

## Prerequisites

### General Requirements
- **Java Development Kit (JDK)** 11 or higher
- **Gradle** (included via Gradle Wrapper `./gradlew`)

### Platform-Specific Requirements

#### Linux - Debian/Ubuntu
```bash
# Install build tools
sudo apt-get update
sudo apt-get install build-essential libxcrypt-compat
```

#### Linux - Fedora
```bash
# Install build tools
sudo dnf install @development-tools glibc-devel libxcrypt-compat
```

##### macOS
```bash
# Install Xcode Command Line Tools
xcode-select --install
```

#### Windows
- Install **MinGW-w64** toolchain
- Add MinGW-w64 `bin` directory to PATH

#### For Web Targets (JS/Wasm)
- **Node.js** 14+ (for running development servers)

## Project Structure

```
src/
├── commonMain/          # Shared Kotlin code (simulator core logic)
│   ├── kotlin/         # Main simulator implementation
│   └── resources/      # Shared resources
├── commonTest/         # Shared unit tests
├── nativeMain/         # Kotlin/Native specific code
├── jvmMain/           # Kotlin/JVM specific code
├── jsMain/            # Kotlin/JS specific code with HTML UI
├── wasmJsMain/        # Kotlin/Wasm specific code with HTML UI

gradle/                # Gradle configuration and version management
build.gradle.kts       # Main build configuration (Kotlin DSL)
settings.gradle.kts    # Project structure definition
```

## Building the Project

### Build All Targets
```bash
./gradlew build
```

## Running the Application

### Kotlin/Native

Native executables are compiled for your specific platform.

**Linux/macOS:**
```bash
# Debug build
./gradlew runDebugExecutableLinuxX64      # Linux x86_64
./gradlew runDebugExecutableLinuxArm64    # Linux ARM64
./gradlew runDebugExecutableMacosX64      # macOS Intel
./gradlew runDebugExecutableMacosArm64    # macOS Apple Silicon

# Release build (optimized)
./gradlew runReleaseExecutableLinuxX64
./gradlew runReleaseExecutableLinuxArm64
./gradlew runReleaseExecutableMacosX64
./gradlew runReleaseExecutableMacosArm64
```

**Windows:**
```bash
./gradlew runDebugExecutableMingwX64
./gradlew runReleaseExecutableMingwX64
```

The compiled executable can also be found in:
```
build/bin/[target]/[buildType]/
```

### Kotlin/JVM

Standard Java-based execution with full interoperability with Java libraries.

```bash
./gradlew runJvm
```

Output is printed to the console with standard Java performance characteristics.

### Kotlin/JS

Runs in a web browser with an interactive HTML UI.

**Development mode** (with hot-reload):
```bash
./gradlew jsBrowserDevelopmentRun
```

**Production mode** (optimized):
```bash
./gradlew jsBrowserProductionRun
```

The application will automatically open at `http://localhost:8080` in your browser.

**Build only (without running):**
```bash
./gradlew jsBrowserDistribution
```
Output in `build/dist/js/productionExecutable/`

### Kotlin/Wasm

WebAssembly target for high-performance browser execution.

**Development mode:**
```bash
./gradlew wasmBrowserDevelopmentRun
```

**Production mode:**
```bash
./gradlew wasmBrowserProductionRun
```

Access at `http://localhost:8080` in your browser.

**Build only:**
```bash
./gradlew wasmBrowserDistribution
```

## Testing

### Run All Tests
```bash
./gradlew allTests
```
Executes test suites for all targets (Native, JVM, JS, Wasm).

### Run Tests for Specific Target
```bash
./gradlew jvmTest            # Kotlin/JVM tests
./gradlew jsTest             # Kotlin/JS tests
./gradlew wasmJsTest         # Kotlin/Wasm tests
```

#### Native Tests
For Kotlin/Native, run tests for specific targets:
```bash
./gradlew linuxX64Test       # Linux x86_64 tests
./gradlew linuxArm64Test     # Linux ARM64 tests
./gradlew macosX64Test       # macOS Intel tests
./gradlew macosArm64Test     # macOS Apple Silicon tests
./gradlew mingwX64Test       # Windows tests
```

Test results are available in:
```
build/reports/tests/allTests/
```

## Development Workflow

### IDE Setup (IntelliJ IDEA / VS Code)
1. Open project in IDE
2. IDE automatically recognizes `build.gradle.kts`
3. Gradle will download all dependencies
4. Start developing in `src/commonMain/kotlin/`

### Code Organization
- **Core Logic**: Place RISC-V simulator code in `commonMain/`
- **Platform-Specific**: Only put platform-dependent code in target-specific directories
- **Tests**: Add tests to `commonTest/` for shared functionality

### Helpful Gradle Tasks
```bash
./gradlew clean              # Clean build artifacts
./gradlew tasks              # List all available tasks
./gradlew dependencies       # Show project dependencies
./gradlew --refresh-dependencies  # Force dependency refresh
```

## Troubleshooting

### Build Fails
```bash
# Clear gradle cache
./gradlew clean

# Rebuild with verbose output
./gradlew build --info
```

### Native Build Issues
- Ensure native toolchain is installed (see Prerequisites)
- On Linux: `sudo ldconfig` to update library cache

### Web Target Issues
- Ensure Node.js is installed: `node --version`
- Clear npm cache: `rm -rf node_modules/ && npm install`

### Java Version Mismatch
```bash
# Check Java version
java -version

# Select correct JDK if multiple versions are installed with update-alternatives (Linux)
sudo update-alternatives --config java
sudo update-alternatives --config javac
```
> **Note**: The authors does not know how you can have multiple JDKs installed on Windows or macOS.
> Please refer to your OS documentation.
> Optionally, you can report back to the authors if you found a solution. This would be highly appreciated.

## References

- [Kotlin Official Documentation](https://kotlinlang.org/docs/)
- [Kotlin Multiplatform Guide](https://kotlinlang.org/docs/multiplatform.html)
- [Gradle Documentation](https://gradle.org/docs/)
- [RISC-V Specification](https://riscv.org/technical/specifications/)