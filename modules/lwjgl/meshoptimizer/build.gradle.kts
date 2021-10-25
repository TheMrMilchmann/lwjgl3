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
    artifact = "lwjgl-meshoptimizer"
    projectName = "LWJGL - meshoptimizer bindings"
    projectDesc = "A library that provides algorithms to help optimize meshes."
    platforms = Platforms.ALL
}