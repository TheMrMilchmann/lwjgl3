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
    artifact = "lwjgl-nanovg"
    projectName = "LWJGL - NanoVG & NanoSVG bindings"
    projectDesc = "A small antialiased vector graphics rendering library for OpenGL. Also includes NanoSVG, a simple SVG parser."
    platforms = Platforms.ALL
}