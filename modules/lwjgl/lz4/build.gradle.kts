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
    artifact = "lwjgl-lz4"
    projectName = "LWJGL - LZ4 bindings"
    projectDesc = "A lossless data compression algorithm that is focused on compression and decompression speed."
    platforms = Platforms.ALL
}