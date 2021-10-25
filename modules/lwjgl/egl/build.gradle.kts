/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
plugins {
    `lwjgl-binding`
    kotlin("jvm")
}

lwjgl {
    artifact = "lwjgl-egl"
    projectName = "LWJGL - EGL bindings"
    projectDesc = "An interface between Khronos rendering APIs such as OpenGL ES or OpenVG and the underlying native platform window system."
}