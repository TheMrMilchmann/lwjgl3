/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package build.native

import build.*
import buildArch
import org.gradle.api.*
import org.gradle.api.tasks.util.*
import org.gradle.process.*
import java.io.*

private val Project.LIB_POSTFIX get() = when (buildArch) {
    "x64" -> ""
    else -> "32"
}

open class CompileNativesWindowsSpec(
    override val project: Project,
    var name: String = "",
    override var dest: File? = null,
    override var flags: MutableList<String> = mutableListOf()
): CompileNativesSpec {

    override val source = mutableListOf<Any>()
    override val patternSet = PatternSet()

}

class BuildNativesWindowsSpec(
    project: Project,
    var beforeCompile: (Project.() -> Unit)? = null,
    var compilerArgs: MutableList<String> = mutableListOf(),
    var beforeLink: (Project.() -> Unit)? = null,
    var link: Iterable<String>? = null,
    var linkArgs: MutableList<String> = mutableListOf()
): CompileNativesWindowsSpec(
    project,
    flags = mutableListOf("/Wall", "/WX")
), BuildNativesSpec {

    fun beforeCompile(action: Project.() -> Unit) { beforeCompile = action }
    fun compilerArgs(vararg args: String) = apply { compilerArgs.addAll(args) }
    fun beforeLink(action: Project.() -> Unit) { beforeLink = action }
    fun linkArgs(vararg args: String) = apply { linkArgs.addAll(args) }

}

open class BuildNativesWindows: BuildNatives<BuildNativesWindowsSpec>({ BuildNativesWindowsSpec(it) }) {

    override fun run() = project.buildNatives(spec)

}

private fun Project.buildNatives(spec: BuildNativesWindowsSpec): ExecResult {
    spec.beforeCompile?.invoke(this)

    val compileRes = compileNatives(spec)
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

        args("/LD", "/WX", "/nologo")
        args("/Fe:\"${spec.dest}/build/${spec.name}$LIB_POSTFIX.dll\"")
        args(spec.link)
        args(*spec.dest!!.listFiles { file: File -> file.name.endsWith(".obj") })
        args("/link", "/OPT:REF,ICF", "/DLL", "/LTCG")
        args(spec.linkArgs)
    }
}

private fun Project.compileNatives(spec: CompileNativesWindowsSpec) = exec {
    executable = "cl"

    args("/c")
    args(spec.flags)
    args("/EHsc", "/Ox", "/GF", "/Gy", "/GL", "/GR-", "/GS-", "/MT", "/MP", "/nologo", "/DNDEBUG", "/DLWJGL_WINDOWS", "/DLWJGL_$buildArch")
    args("/Fo${spec.dest}")

    args("/I$JNI_HEADERS", "/I$JNI_HEADERS/win32")
    args("/I${File(project.projectDir, "src/main/c")}/system")
    args("/I${File(project.projectDir, "src/main/c")}/system/windows")

    args(spec.getSource())
}

internal fun Project.compileNativesWindows(init: CompileNativesWindowsSpec.() -> Unit) = compileNatives(CompileNativesWindowsSpec(this).apply(init))