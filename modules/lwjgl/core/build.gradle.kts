/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.lwjgl.build.*

plugins {
    `lwjgl-core`
}

lwjgl {
    artifact = "lwjgl"
    projectName = "LWJGL"
    projectDesc = "The LWJGL core library."
    platforms = Platforms.ALL
}