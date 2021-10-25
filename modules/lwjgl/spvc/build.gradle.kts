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
    artifact = "lwjgl-spvc"
    projectName = "LWJGL - SPIRV-Cross bindings"
    projectDesc = "A library for performing reflection on SPIR-V and disassembling SPIR-V back to high level languages."
    platforms = Platforms.ALL
}