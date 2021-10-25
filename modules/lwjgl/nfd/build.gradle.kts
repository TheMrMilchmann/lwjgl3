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
    artifact = "lwjgl-nfd"
    projectName = "LWJGL - Native File Dialog bindings"
    projectDesc = "A tiny, neat C library that portably invokes native file open and save dialogs."
    platforms(Platforms.LINUX, Platforms.MACOS, Platforms.WINDOWS, Platforms.WINDOWS_X86)
}