/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.lwjgl.build.*

plugins {
    `lwjgl-binding`
}

lwjgl {
    artifact = "lwjgl-lmdb"
    projectName = "LWJGL - LMDB bindings"
    projectDesc = "A compact, fast, powerful, and robust database that implements a simplified variant of the BerkeleyDB (BDB) API."
    platforms = Platforms.ALL
}