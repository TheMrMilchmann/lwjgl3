/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.build

import org.gradle.api.*
import org.gradle.kotlin.dsl.*

class LWJGLDependencyHandlerScope internal constructor(private val project: Project) {

    fun implementation(module: String) {
        project.dependencies {
            "implementation"(project(":lwjgl.$module"))
            "templatesImplementation"(project(":lwjgl.$module", "templates"))
        }
    }

    fun compileOnly(module: String) {
        project.dependencies {
            "compileOnly"(project(":lwjgl.$module"))
            "templatesImplementation"(project(":lwjgl.$module", "templates"))
        }
    }

}