/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.lwjgl.build.*

plugins {
    `lwjgl-binding`
}

lwjgl {
    artifact = "lwjgl-xxhash"
    projectName = "LWJGL - xxHash bindings"
    projectDesc = "An extremely fast hash algorithm, running at RAM speed limits."
    platforms = Platforms.ALL
}