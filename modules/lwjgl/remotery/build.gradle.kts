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
    artifact = "lwjgl-remotery"
    projectName = "LWJGL - Remotery bindings"
    projectDesc = "A realtime CPU/GPU profiler hosted in a single C file with a viewer that runs in a web browser."
    platforms = Platforms.ALL
}