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
    artifact = "lwjgl-opengles"
    projectName = "LWJGL - OpenGL ES bindings"
    projectDesc = "A royalty-free, cross-platform API for full-function 2D and 3D graphics on embedded systems - including consoles, phones, appliances and vehicles."
    platforms = Platforms.ALL

    dependencies {
        compileOnly("egl")
    }
}