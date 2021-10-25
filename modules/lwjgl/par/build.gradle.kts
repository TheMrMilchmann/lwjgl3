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
    artifact = "lwjgl-par"
    projectName = "LWJGL - par_shapes bindings"
    projectDesc = "Generate parametric surfaces and other simple shapes."
    platforms = Platforms.ALL
}