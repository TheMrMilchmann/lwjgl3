/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.build.natives

import buildArch
import org.gradle.api.*
import org.gradle.api.tasks.util.*
import org.gradle.process.*
import org.lwjgl.build.*
import org.lwjgl.build.natives.*
import java.io.*

private val Project.LIB_POSTFIX get() = when (buildArch) {
    "x64" -> ""
    else -> "32"
}

open class CompileNativeLinuxSpec(
    override val project: Project,
    override var name: String = "",
    override var dest: File? = null,
    override var flags: MutableList<String> = mutableListOf()
): CompileNativesSpec {

    override val source = mutableListOf<Any>()
    override val patternSet = PatternSet()

}

class BuildNativesLinuxSpec(
    project: Project,
    var beforeCompile: (Project.() -> Unit)? = null,
    var compilerArgs: MutableList<String> = mutableListOf(),
    var beforeLink: (Project.() -> Unit)? = null,
    var link: Iterable<String>? = null,
    var linkArgs: MutableList<String> = mutableListOf()
): CompileNativeLinuxSpec(
    project,
    flags = mutableListOf("/Wall", "/WX")
), BuildNativesSpec {

    fun beforeCompile(action: Project.() -> Unit) { beforeCompile = action }
    fun compilerArgs(vararg args: String) = apply { compilerArgs.addAll(args) }
    fun beforeLink(action: Project.() -> Unit) { beforeLink = action }
    fun linkArgs(vararg args: String) = apply { linkArgs.addAll(args) }

}

open class BuildNativesLinux: BuildNatives<BuildNativesLinuxSpec>({ BuildNativesLinuxSpec(it) }) {

    override fun run() = project.buildNatives(spec)

}

private fun Project.buildNatives(spec: BuildNativesLinuxSpec): ExecResult {


    return exec {

    }
}