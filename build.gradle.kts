plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotest)
    alias(libs.plugins.versions.update)
    alias(libs.plugins.testballon)
}

version = "2025"

repositories {
    mavenCentral()
}

tasks.test {
    minHeapSize = "1g"
    maxHeapSize = "40g"
    testLogging.showStandardStreams = true
    // the useJUnitPlatform is required for running `./gradlew test`, but is not required for running
    // `./gradlew kotest`
    useJUnitPlatform()
    filter {
        setIncludePatterns("*Test", "Day*")
    }
}

kotlin {
    jvmToolchain(25)
    @Suppress("UnsafeCompilerArguments")
    compilerOptions {
        suppressWarnings = true
        freeCompilerArgs.set(
            listOf(
                "context-sensitive-resolution",
                "context-parameters",
                "nested-type-aliases",
                "explicit-backing-fields",
            ).map { "-X$it" }
        )
    }

    sourceSets {
        test {
            dependencies {
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.runner.junit5)
                implementation(libs.kotest.assertions.core)
                implementation(libs.testballon.framework.core)
            }
        }
    }
}
