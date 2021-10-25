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
    artifact = "lwjgl-tinyexr"
    projectName = "LWJGL - Tiny OpenEXR bindings"
    projectDesc = "A small library to load and save OpenEXR(.exr) images."
    platforms = Platforms.ALL
}