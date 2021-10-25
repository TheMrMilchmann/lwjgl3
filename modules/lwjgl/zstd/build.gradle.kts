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
    artifact = "lwjgl-zstd"
    projectName = "LWJGL - Zstandard bindings"
    projectDesc = "A fast lossless compression algorithm, targeting real-time compression scenarios at zlib-level and better compression ratios."
    platforms = Platforms.ALL
}