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
    artifact = "lwjgl-stb"
    projectName = "LWJGL - stb bindings"
    projectDesc = "Single-file public domain libraries for fonts, images, ogg vorbis files and more."
    platforms = Platforms.ALL
}