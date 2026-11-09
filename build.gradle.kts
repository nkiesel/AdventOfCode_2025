plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.versions.update)
    alias(libs.plugins.testBalloon)
}

repositories {
    gradlePluginPortal()
    mavenLocal()
    mavenCentral()
    google()
}

version = "2025"

tasks.test {
    minHeapSize = "1g"
    maxHeapSize = "40g"
    testLogging.showStandardStreams = true
    // the useJUnitPlatform is required for running `./gradlew test`, but is not required for running
    // `./gradlew kotest`
//    useJUnitPlatform()
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
                "collection-literals",
                "name-based-destructuring=name-mismatch",
            ).map { "-X$it" }
        )
    }

    sourceSets {
        test {
            dependencies {
                implementation(libs.nkiesel.gameutils)
                implementation(libs.testBalloon.framework.core)
                implementation(libs.testBalloon.integration.kotest.assertions)
                implementation(libs.kotest.assertions.core)
            }
        }
    }
}
