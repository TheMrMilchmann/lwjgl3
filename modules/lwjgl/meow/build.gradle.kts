/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.lwjgl.build.*

plugins {
    `lwjgl-binding`
    kotlin("jvm")
}

lwjgl {
    artifact = "lwjgl-meow"
    projectName = "LWJGL - Meow bindings"
    projectDesc = "An extremely fast non-cryptographic hash."
    platforms(Platforms.LINUX, Platforms.LINUX_ARM64, Platforms.MACOS, Platforms.WINDOWS, Platforms.WINDOWS_X86)
}