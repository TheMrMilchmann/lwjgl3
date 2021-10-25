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
    artifact = "lwjgl-sse"
    projectName = "LWJGL - SSE bindings"
    projectDesc = "Simple SSE intrinsics."
    platforms(Platforms.LINUX, Platforms.MACOS, Platforms.WINDOWS, Platforms.WINDOWS_X86)
}