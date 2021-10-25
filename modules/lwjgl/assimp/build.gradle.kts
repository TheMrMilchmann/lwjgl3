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
    artifact = "lwjgl-assimp"
    projectName = "LWJGL - Assimp bindings"
    projectDesc = "A portable Open Source library to import various well-known 3D model formats in a uniform manner."
    platforms = Platforms.ALL
}