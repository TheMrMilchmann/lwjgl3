/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.lwjgl.build.*

plugins {
    `lwjgl-binding`
}

lwjgl {
    artifact = "lwjgl-openvr"
    projectName = "LWJGL - OpenVR bindings"
    projectDesc = "An API and runtime that allows access to VR hardware from multiple vendors without requiring that applications have specific knowledge of the hardware they are targeting."
    platforms(Platforms.LINUX, Platforms.MACOS, Platforms.WINDOWS, Platforms.WINDOWS_X86)
}