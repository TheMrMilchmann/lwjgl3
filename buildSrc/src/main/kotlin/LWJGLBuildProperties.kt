/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.gradle.api.*
import org.gradle.internal.os.*
import org.gradle.kotlin.dsl.*
import org.lwjgl.build.*

// LWJGL version
const val lwjglVersion = "3.1.4"

// Build dependencies
const val kotlinVersion = "1.1.51"
const val testNGVersion = "6.11"
const val jcommanderVersion = "1.72"

fun Project.loadBuildProperties() {
    require(parent === null) { "Build properties must be loaded from root project." }

    extra["build.platform"] = when {
        OperatingSystem.current().isLinux -> Platforms.LINUX
        OperatingSystem.current().isMacOsX -> Platforms.MACOS
        OperatingSystem.current().isWindows -> Platforms.WINDOWS
        else -> throw RuntimeException("Unsupported OS")
    }

    extra["build.type"] = when {
        "build.type" in properties -> properties["build.type"] as String
        else -> System.getenv().getOrDefault("LWJGL_BUILD_TYPE", "nightly")
    }

    extra["build.output"] = when {
        "build.output" in properties -> properties["build.output"] as String
        else -> System.getenv().getOrDefault("LWJGL_BUILD_OUTPUT", null)
    }

    extra["build.arch"] = when {
        "build.arch" in properties -> properties["build.arch"] as String
        "LWJGL_BUILD_ARCH" in System.getenv() -> System.getenv("LWJGL_BUILD_ARCH")
        "64" in System.getProperty("os.arch") -> "x64"
        else -> "x86"
    }

    extra["build.offline"] = when {
        "build.offline" in properties -> properties["build.offline"] as String
        else -> System.getenv().getOrDefault("LWJGL_BUILD_OFFLINE", "false")
    }.toBoolean()
}

val Project.buildPlatform get() = rootProject.extra["build.platform"] as Platforms

/**
 * This is used as the source of binary dependencies. Valid values:
 * - nightly
 *      the latest successful build. Dependency repos can be found here: https://github.com/LWJGL-CI
 *      this is the default, set the LWJGL_BUILD_TYPE environment variable to override.
 * - stable
 *      the latest nightly build that has been verified to work with LWJGL.
 * - release/latest
 *      the latest stable build that has been promoted to an official LWJGL release.
 * - release/{build.version}
 *      a specific previously released build.
 */
val Project.buildType get() = rootProject.extra["build.type"] as String

/**
 * This is used to override the default output directory. By default, the directories
 * bin, generated and release will be created in the same directory as the main build
 * script. These 3 directories will contain thousands of tiny files, so you may want
 * to override their location due to performance characteristics of the storage
 * hardware.
 *
 * Note that when this property is set, the directories bin, generated and release
 * will be symlinks to the corresponding directories in LWJGL_BUILD_OUTPUT. The gradle
 * scripts and IDE projects always work with paths relative to the project root.
 */
val Project.buildOutput get() = rootProject.extra["build.output"] as String

/**
 * The target native architecture. Must be either x86 or x64. By default, os.arch of the JVM
 * that runs Gradle is used, but this can be overriden for cross-compiling to another architecture.
 */
val Project.buildArch get() = rootProject.extra["build.arch"] as String

/**
 * Offline build flag. This is useful when working offline, or when custom binary dependencies
 * are used (so they are not overriden). Set to one of true/on/yes to enable.
 */
val Project.buildOffline get() = rootProject.extra["build.offline"] as Boolean

@Suppress("UNCHECKED_CAST")
val Project.bindings get() = rootProject.extra["bindings"] as List<Binding>