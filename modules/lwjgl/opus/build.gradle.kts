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
    artifact = "lwjgl-opus"
    projectName = "LWJGL - Opus bindings"
    projectDesc = "A totally open, royalty-free, highly versatile audio codec."
    platforms = Platforms.ALL
}