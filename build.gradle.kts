plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotest)
    alias(libs.plugins.versions.update)
}

version = "2025"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotest.assertions.core)
    implementation(libs.kotest)
}

tasks.test {
    minHeapSize = "1g"
    maxHeapSize = "30g"
    testLogging.showStandardStreams = true
    filter {
        setIncludePatterns("*Test", "Day*")
    }
}

kotlin {
    jvmToolchain(21)
    @Suppress("UnsafeCompilerArguments")
    compilerOptions {
        suppressWarnings = true
        freeCompilerArgs.set(
            listOf(
                "-Xcontext-sensitive-resolution",
                "-Xcontext-parameters",
                "-Xnested-type-aliases",
            )
        )
    }
}
