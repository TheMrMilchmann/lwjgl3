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
    artifact = "lwjgl-driftfx"
    projectName = "LWJGL - DriftFX bindings"
    projectDesc = "A library that allows you to render any OpenGL content directly into JavaFX nodes."
    platforms = Platforms.ALL
}