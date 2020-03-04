/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.lwjgl.build.*

plugins {
    `lwjgl-binding`
}

lwjgl {
    artifact = "lwjgl-yoga"
    projectName = "LWJGL - Yoga bindings"
    projectDesc = "An open-source, cross-platform layout library that implements Flexbox."
    platforms = Platforms.ALL
}