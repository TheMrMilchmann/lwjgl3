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
    artifact = "lwjgl-rpmalloc"
    projectName = "LWJGL - rpmalloc bindings"
    projectDesc = "A public domain cross platform lock free thread caching 16-byte aligned memory allocator implemented in C."
    platforms = Platforms.ALL
}