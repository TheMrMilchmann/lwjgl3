/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.lwjgl.build.*

plugins {
    `lwjgl-binding`
}

lwjgl {
    artifact = "lwjgl-opengl"
    projectName = "LWJGL - OpenGL bindings"
    projectDesc = "The most widely adopted 2D and 3D graphics API in the industry, bringing thousands of applications to a wide variety of computer platforms."
    platforms = Platforms.ALL
}