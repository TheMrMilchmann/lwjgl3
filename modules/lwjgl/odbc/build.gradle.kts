/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
plugins {
    `lwjgl-binding`
    kotlin("jvm")
}

lwjgl {
    artifact = "lwjgl-odbc"
    projectName = "LWJGL - ODBC bindings"
    projectDesc = "A C programming language interface that makes it possible for applications to access data from a variety of database management systems (DBMSs)."
}