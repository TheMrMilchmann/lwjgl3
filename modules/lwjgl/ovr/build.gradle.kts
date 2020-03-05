/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.lwjgl.build.*

plugins {
    `lwjgl-binding`
}

lwjgl {
    artifact = "lwjgl-ovr"
    projectName = "LWJGL - OVR bindings"
    projectDesc = "The API of the Oculus SDK."
    platforms(Platforms.WINDOWS, Platforms.WINDOWS_X86)

    dependencies {
        compileOnly("vulkan")
    }
}