/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
plugins {
    `lwjgl-binding`
    kotlin("jvm")
}

lwjgl {
    artifact = "lwjgl-cuda"
    projectName = "LWJGL - CUDA bindings"
    projectDesc = "A parallel computing platform and programming model developed by NVIDIA for general computing on GPUs."
}