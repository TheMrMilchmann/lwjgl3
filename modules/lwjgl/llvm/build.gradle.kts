/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.lwjgl.build.*

plugins {
    `lwjgl-binding`
}

lwjgl {
    artifact = "lwjgl-llvm"
    projectName = "LWJGL - LLVM/Clang bindings"
    projectDesc = "A collection of modular and reusable compiler and toolchain technologies."
    platforms = Platforms.ALL
}