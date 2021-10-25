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
    artifact = "lwjgl-bullet"
    projectName = "LWJGL - Bullet bindings"
    projectDesc = "Real-time collision detection and multi-physics simulation for VR, games, visual effects, robotics, machine learning etc."
    platforms = Platforms.ALL
}