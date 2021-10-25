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
    artifact = "lwjgl-bgfx"
    projectName = "LWJGL - bgfx bindings"
    projectDesc = "A cross-platform, graphics API agnostic rendering library. It provides a high performance, low level abstraction for common platform graphics APIs like OpenGL, Direct3D and Apple Metal."
    platforms(
        Platforms.LINUX, Platforms.LINUX_ARM64, Platforms.LINUX_ARM32,
        Platforms.MACOS, Platforms.MACOS_ARM64,
        Platforms.WINDOWS, Platforms.WINDOWS_X86
    )
}