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
import java.io.*

private val Project.LIB_POSTFIX get() = when (buildArch) {
    "x64" -> ""
    else -> "32"
}

open class CompileNativesWindowsSpec(
    override val project: Project,
    override var name: String = "",
    override var dest: File? = null,
    var compilerArgs: MutableList<String> = mutableListOf(),
    override var flags: MutableList<String> = mutableListOf()
): CompileNativesSpec {

    override val source = mutableListOf<Any>()
    override val patternSet = PatternSet()

    fun compilerArgs(vararg args: String) = apply { compilerArgs.addAll(args) }

}

class BuildNativesWindowsSpec(
    project: Project,
    var beforeCompile: (Project.() -> Unit)? = null,
    var beforeLink: (Project.() -> Unit)? = null,
    var link: Iterable<String>? = null,
    var linkArgs: MutableList<String> = mutableListOf()
): CompileNativesWindowsSpec(
    project,
    flags = mutableListOf("/Wall", "/WX")
), BuildNativesSpec {

    fun beforeCompile(action: Project.() -> Unit) { beforeCompile = action }
    fun beforeLink(action: Project.() -> Unit) { beforeLink = action }
    fun linkArgs(vararg args: String) = apply { linkArgs.addAll(args) }

}

open class BuildNativesWindows: BuildNatives<BuildNativesWindowsSpec>({ BuildNativesWindowsSpec(it) }) {

    override fun run() = project.buildNatives(spec)

}

private fun Project.buildNatives(spec: BuildNativesWindowsSpec): ExecResult {
    spec.beforeCompile?.invoke(this)

    val compileRes = compileNatives(spec.apply { flags.addAll(compilerArgs) })
    compileRes.rethrowFailure()
    compileRes.assertNormalExitValue()

    /*
     * x86: Generate DEF file to drop __stdcall decorations from JavaCritical
     * functions. This is required because of a bug (wrong args_size) in
     * hotspot/src/share/vm/prims/nativeLookup.cpp#lookup_critical_entry.
     */
    if (buildArch == "x86") {
        // TODO impl undecorate
    }

    spec.beforeLink?.invoke(this)

    return exec {
        executable = "cl"

        val buildDir = mkdir(File(spec.dest, "build"))

        args("/LD", "/WX", "/nologo")
        args("/Fe:\"$buildDir/${spec.name}$LIB_POSTFIX.dll\"")
        if (spec.link != null) args(spec.link)
        args(*spec.dest!!.listFiles { file: File -> file.name.endsWith(".obj") })
        args("/link", "/OPT:REF,ICF", "/DLL", "/LTCG")
        args(spec.linkArgs)
    }
}

private fun Project.compileNatives(spec: CompileNativesWindowsSpec) = exec {
    executable = "cl"

    args("/c")
    args(spec.flags)
    args(spec.compilerArgs)
    args("/EHsc", "/Ox", "/GF", "/Gy", "/GL", "/GR-", "/GS-", "/MT", "/MP", "/nologo", "/DNDEBUG", "/DLWJGL_WINDOWS", "/DLWJGL_$buildArch")
    args("/Fo${spec.dest}/")

    args("/I$JNI_HEADERS", "/I$JNI_HEADERS/win32")
    args("/I${File(project.projectDir, "src/main/c")}/system")
    args("/I${File(project.projectDir, "src/main/c")}/system/windows")

    args(spec.getSource())
}

fun Project.compileNativesWindows(init: CompileNativesWindowsSpec.() -> Unit) = compileNatives(CompileNativesWindowsSpec(this).apply(init))