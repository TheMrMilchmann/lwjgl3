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
    artifact = "lwjgl-openal"
    projectName = "LWJGL - OpenAL bindings"
    projectDesc = "A cross-platform 3D audio API appropriate for use with gaming applications and many other types of audio applications."
    platforms = Platforms.ALL
}