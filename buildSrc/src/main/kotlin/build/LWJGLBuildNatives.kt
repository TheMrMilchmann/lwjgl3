/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package build

import build.native.*
import de.undercouch.gradle.tasks.download.*
import groovy.lang.*
import org.gradle.api.*
import org.gradle.api.file.*
import org.gradle.api.specs.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.util.*
import org.gradle.internal.jvm.*
import org.gradle.internal.os.*
import org.gradle.process.*
import java.io.*

internal val JNI_HEADERS = when (Jvm.current().javaVersion) {
    JavaVersion.VERSION_1_9 -> File(Jvm.current().javaHome, "include")
    else                    -> File(Jvm.current().javaHome, "include")
}

internal interface CompileNativesSpec: PatternFilterable {

    val project: Project

    val source: MutableList<Any>
    val patternSet: PatternSet

    fun getSource(): FileTree {
        val copy = ArrayList<Any>(source)
        val src = project.files(copy).asFileTree
        return if (src == null) project.files().asFileTree else src.matching(patternSet)
    }

    fun setSource(source: FileTree) {
        setSource(source as Any)
    }

    fun setSource(source: Any) {
        this.source.clear()
        this.source.add(source)
    }

    fun source(vararg sources: Any): CompileNativesSpec {
        for (source in sources) {
            this.source.add(source)
        }
        return this
    }

    override fun include(vararg includes: String): CompileNativesSpec {
        patternSet.include(*includes)
        return this
    }

    override fun include(includes: Iterable<String>): CompileNativesSpec {
        patternSet.include(includes)
        return this
    }

    override fun include(includeSpec: Spec<FileTreeElement>): CompileNativesSpec {
        patternSet.include(includeSpec)
        return this
    }

    override fun include(includeSpec: Closure<*>): CompileNativesSpec {
        patternSet.include(includeSpec)
        return this
    }

    override fun exclude(vararg excludes: String): CompileNativesSpec {
        patternSet.exclude(*excludes)
        return this
    }

    override fun exclude(excludes: Iterable<String>): CompileNativesSpec {
        patternSet.exclude(excludes)
        return this
    }

    override fun exclude(excludeSpec: Spec<FileTreeElement>): CompileNativesSpec {
        patternSet.exclude(excludeSpec)
        return this
    }

    override fun exclude(excludeSpec: Closure<*>): CompileNativesSpec {
        patternSet.exclude(excludeSpec)
        return this
    }

    @Internal
    override fun getIncludes(): Set<String> {
        return patternSet.includes
    }

    override fun setIncludes(includes: Iterable<String>): CompileNativesSpec {
        patternSet.setIncludes(includes)
        return this
    }

    @Internal
    override fun getExcludes(): Set<String> {
        return patternSet.excludes
    }

    override fun setExcludes(excludes: Iterable<String>): CompileNativesSpec {
        patternSet.setExcludes(excludes)
        return this
    }

}

internal interface BuildNativesSpec: CompileNativesSpec {

}

fun buildNatives() =
    when {
        //org.gradle.internal.os.OperatingSystem.current().isLinux   -> compileNativeLinux
        //org.gradle.internal.os.OperatingSystem.current().isMacOsX  -> compileNativeMacOSX
        OperatingSystem.current().isWindows -> BuildNativesWindows::class
        else                                -> throw IllegalStateException("Native compilation for ${org.gradle.internal.os.OperatingSystem.current()} not available.")
    }

abstract class BuildNatives: SourceTask() {

    @TaskAction
    fun buildNatives() {
        val res = run()

        res.rethrowFailure()
    }

    fun updateDependency(name: String, artifact: String) {
        val action = DownloadAction(project)
        action.src("https://build.lwjgl.org/nightly/windows/$artifact")
        action.dest(File(project.rootDir, "lib/windows/$artifact"))

        action.execute()
    }

    abstract fun run(): ExecResult

}

fun Project.lwjglRegisterNativeTasks(umbrella: Task, commonInit: Task.() -> Unit) {
    Bindings.values()
        .filter { isActive(it) && it.withNative() }
        .forEach {
            val compileNativeBinding = tasks.create("compileNative-${it.id}", buildNatives().java)
            commonInit.invoke(compileNativeBinding)
            it.compileNativeConfiguration()!!.invoke(compileNativeBinding)

            umbrella.dependsOn(compileNativeBinding)
        }
}