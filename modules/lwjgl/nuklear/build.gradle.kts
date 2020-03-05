/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.lwjgl.build.*

plugins {
    `lwjgl-binding`
}

lwjgl {
    artifact = "lwjgl-nuklear"
    projectName = "LWJGL - Nuklear bindings"
    projectDesc = "A minimal state immediate mode graphical user interface toolkit."
    platforms = Platforms.ALL
}