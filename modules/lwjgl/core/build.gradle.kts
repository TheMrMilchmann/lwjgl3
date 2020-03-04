/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
plugins {
    `lwjgl-core`
}

lwjgl {
    artifact = "lwjgl"
    projectName = "LWJGL"
    projectDesc = "The LWJGL core library."
    platforms = org.lwjgl.build.Platforms.ALL
}