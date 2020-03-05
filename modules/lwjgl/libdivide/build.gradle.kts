/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.lwjgl.build.*

plugins {
    `lwjgl-binding`
}

lwjgl {
    artifact = "lwjgl-libdivide"
    projectName = "LWJGL - libdivide bindings"
    projectDesc = "A library that replaces expensive integer divides with comparatively cheap multiplication and bitshifts."
    platforms = Platforms.ALL
}