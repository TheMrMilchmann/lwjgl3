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
    artifact = "lwjgl-tootle"
    projectName = "LWJGL - AMD Tootle bindings"
    projectDesc = "A 3D triangle mesh optimization library that improves on existing mesh preprocessing techniques."
    platforms(Platforms.LINUX, Platforms.MACOS, Platforms.WINDOWS, Platforms.WINDOWS_X86)
}