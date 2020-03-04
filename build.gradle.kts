/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
val lwjglVersion: String by project

allprojects {
    group = "org.lwjgl"
    version = lwjglVersion

    repositories {
        mavenCentral()
    }
}