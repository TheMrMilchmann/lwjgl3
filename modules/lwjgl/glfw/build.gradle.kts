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
    artifact = "lwjgl-glfw"
    projectName = "LWJGL - GLFW bindings"
    projectDesc = "A multi-platform library for OpenGL, OpenGL ES and Vulkan development on the desktop. It provides a simple API for creating windows, contexts and surfaces, receiving input and events."
    platforms = Platforms.ALL

    dependencies {
        compileOnly("egl")
        compileOnly("opengl")
        compileOnly("opengles")
        compileOnly("vulkan")
    }
}