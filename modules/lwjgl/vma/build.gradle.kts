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
    artifact = "lwjgl-vma"
    projectName = "LWJGL - Vulkan Memory Allocator bindings"
    projectDesc = "An easy to integrate Vulkan memory allocation library."
    platforms = Platforms.ALL

    dependencies {
        implementation("vulkan")
    }
}