/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.gradle.kotlin.dsl.*

plugins {
    kotlin("jvm", kotlinVersion)
}

dependencies {
    compile(kotlin("stdlib-jre8", kotlinVersion))
    compile(project(":modules:generator"))
}