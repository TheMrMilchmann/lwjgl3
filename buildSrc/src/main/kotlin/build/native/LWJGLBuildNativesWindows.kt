/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package build.native

import build.*
import buildArch
import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.util.*
import org.gradle.internal.os.*
import org.gradle.process.*
import java.io.*

private val Project.LIB_POSTFIX: String
    get() = when (buildArch) {
        "x86" -> "32"
        else -> ""
    }

internal fun Project.compileNativesWindows(init: CompileNativesWindowsSpec.() -> Unit) = compileNativesWindows(CompileNativesWindowsSpec(this).apply(init))

private fun Project.compileNativesWindows(spec: CompileNativesWindowsSpec): ExecResult {
    return exec {
        executable = "cl"
        args("/c ${if (spec.flags.isNotEmpty()) "${spec.flags} " else ""}/EHsc /Ox /GF /Gy /GL /GR- /GS- /MT /MP /nologo /DNDEBUG /DLWJGL_WINDOWS /DLWJGL_$buildArch".split(" "))
        args("/Fo${spec.dest}\\")

        args("/I${JNI_HEADERS.absolutePath}")
        args("/I${JNI_HEADERS.absolutePath}\\win32")

        args("/I${File(project.projectDir, "src/main/c").absolutePath}\\system")
        args("/I${File(project.projectDir, "src/main/c").absolutePath}\\system\\windows")

        args(spec.getSource().joinToString(" ").split(" "))
    }
}

private fun Project.buildNativesWindows(init: BuildNativesWindowsSpec.() -> Unit) = buildNativesWindows(BuildNativesWindowsSpec(this).apply(init))

private fun Project.buildNativesWindows(spec: BuildNativesWindowsSpec): ExecResult {
    spec.beforeCompile?.invoke(this)
    val res = compileNativesWindows(spec.apply { flags += compilerArgs?.joinToString(" ") ?: ""})
    res.rethrowFailure()
    res.assertNormalExitValue()

    // TODO undecorate

    spec.beforeLink?.invoke(this)

    return exec {
        executable = "cl"

        args("${spec.linkArgs?.joinToString(" ") ?: ""} /LD /WX /nologo".split(" "))
        args("/Fe:\"${spec.dest}\\build\\${spec.name}$LIB_POSTFIX.dll\"")

        spec.dest?.listFiles()
            ?.filter {
                it.name.endsWith(".obj")
            }?.forEach {
            args(it.absolutePath)
        }

        spec.link?.forEach {
            args(it)
        }

        args("/link")
        args("/OPT:REF,ICF")
        args("/DLL")
        args("/LTCG")
    }
}

internal open class CompileNativesWindowsSpec(
    override val project: Project,
    var name: String = "",
    var dest: File? = null,
    var flags: String = ""
): CompileNativesSpec {

    override val source = mutableListOf<Any>()
    override val patternSet = PatternSet()

}

internal open class BuildNativesWindowsSpec(
    project: Project,
    var beforeCompile: (Project.() -> Unit)? = null,
    var compilerArgs: Iterable<String>? = null,
    var beforeLink: (Project.() -> Unit)? = null,
    var link: Iterable<String>? = null,
    var linkArgs: Iterable<String>? = null
): CompileNativesWindowsSpec(
    project,
    flags = "/Wall /WX"
)

open class BuildNativesWindows: BuildNatives() {

    @Input
    var libName: String = ""

    @OutputDirectory
    var dest: File? = null

    @Input
    var flags: String = ""

    var beforeCompile: Project.() -> Unit = {}

    var compilerArgs: Iterable<String> = emptyList()

    var beforeLink: Project.() -> Unit = {}

    var link: Iterable<String>? = null

    @Input
    var linkArgs: Iterable<String> = emptyList()

    override fun run() = project.buildNativesWindows {
        name = this@BuildNativesWindows.libName
        dest = this@BuildNativesWindows.dest
        flags = this@BuildNativesWindows.flags
        beforeCompile = this@BuildNativesWindows.beforeCompile
        compilerArgs = this@BuildNativesWindows.compilerArgs
        beforeLink = this@BuildNativesWindows.beforeLink
        link = this@BuildNativesWindows.link
        linkArgs = this@BuildNativesWindows.linkArgs

        setSource(this@BuildNativesWindows.getSource())
    }

}

fun Bindings.compileNativeConfiguration() = when {
    OperatingSystem.current().isLinux   -> compileNativeLinux
    OperatingSystem.current().isMacOsX  -> compileNativeMacOSX
    OperatingSystem.current().isWindows -> compileNativeWindows
    else                                -> throw IllegalStateException("Native compilation for ${org.gradle.internal.os.OperatingSystem.current()} not available.")
}

fun Bindings.withNative() = compileNativeConfiguration() != null