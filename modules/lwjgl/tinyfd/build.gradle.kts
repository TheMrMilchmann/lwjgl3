/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.lwjgl.build.*

plugins {
    `lwjgl-binding`
}

lwjgl {
    artifact = "lwjgl-tinyfd"
    projectName = "LWJGL - Tiny File Dialogs bindings"
    projectDesc = "Provides basic modal dialogs."
    platforms = Platforms.ALL
}