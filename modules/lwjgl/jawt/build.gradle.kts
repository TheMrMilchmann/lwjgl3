/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
plugins {
    `lwjgl-binding`
    kotlin("jvm")
}

lwjgl {
    artifact = "lwjgl-jawt"
    projectName = "LWJGL - JAWT bindings"
    projectDesc = "The AWT native interface."
}