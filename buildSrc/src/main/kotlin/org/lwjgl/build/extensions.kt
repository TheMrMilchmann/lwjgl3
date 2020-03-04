/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.build

import org.gradle.api.*

abstract class LWJGLModule {

}

open class LWJGLBinding(private val project: Project) : LWJGLModule() {

    fun dependencies(configuration: LWJGLDependencyHandlerScope.() -> Unit) {
        configuration(LWJGLDependencyHandlerScope(project))
    }

}

open class LWJGLCore(private val project: Project) : LWJGLModule() {



}