/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.build

import org.gradle.api.*
import org.gradle.api.provider.*
import org.gradle.api.publish.*
import org.gradle.api.publish.maven.*
import org.gradle.kotlin.dsl.*
import java.util.*

abstract class LWJGLModule(private val project: Project) {

    private lateinit var _artifact: String

    var artifact: String
        get() = _artifact
        set(value) {
            with (project) {
                project.extensions.configure<PublishingExtension> {
                    (publications.getByName("mavenJava") as MavenPublication).artifactId = value
                }
            }

            _artifact = value
        }

    var platforms: EnumSet<Platforms> = EnumSet.noneOf(Platforms::class.java)
    fun platforms(first: Platforms, vararg rest: Platforms) { EnumSet.of(first, *rest) }

    internal val _projectName: Property<String> = project.objects.property(String::class)
    var projectName: String
        get() = _projectName.get()
        set(value) = _projectName.set(value)

    internal val _projectDesc: Property<String> = project.objects.property(String::class)
    var projectDesc: String
        get() = _projectDesc.get()
        set(value) = _projectDesc.set(value)

}

open class LWJGLBinding(private val project: Project) : LWJGLModule(project) {

    fun dependencies(configuration: LWJGLDependencyHandlerScope.() -> Unit) {
        configuration(LWJGLDependencyHandlerScope(project))
    }

}

open class LWJGLCore(project: Project) : LWJGLModule(project)