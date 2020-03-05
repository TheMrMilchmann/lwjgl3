/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.lwjgl.build.*

plugins {
    `lwjgl-binding`
}

lwjgl {
    artifact = "lwjgl-jemalloc"
    projectName = "LWJGL - jemalloc bindings"
    projectDesc = "A general purpose malloc implementation that emphasizes fragmentation avoidance and scalable concurrency support."
    platforms = Platforms.ALL
}