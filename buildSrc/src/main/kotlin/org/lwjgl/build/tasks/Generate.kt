/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.build.tasks

import org.gradle.api.*
import org.gradle.api.file.*
import org.gradle.api.tasks.*
import org.gradle.internal.jvm.*
import java.io.*

@CacheableTask
open class Generate : DefaultTask() {

    @InputFiles
    @Classpath
    lateinit var classpath: FileCollection

    @InputFile
    @PathSensitive(PathSensitivity.NONE)
    lateinit var bindings: File

    @OutputDirectories
    @Suppress("UNUSED")
    val outputDirectories = File(project.rootDir, "modules/lwjgl")
        .listFiles(FileFilter { it.isDirectory })!!
        .map { File(it, "src/main/generated") }
        .filter { it.isDirectory }

    @TaskAction
    fun generate() {
        project.javaexec {
            executable = Jvm.current().javaExecutable.absolutePath
            mainClass.set("org.lwjgl.generator.GeneratorKt")
            workingDir = project.rootProject.projectDir

            classpath(this@Generate.classpath)

            jvmArgs(
                "-Dfile.encoding=utf-8",
                "-Dline.separator=\"\n\""
            )
            bindings.readText().lines().forEach { binding -> jvmArgs("-Dbinding.$binding=true") }

            args(project.rootProject.file("modules/lwjgl"))
        }.rethrowFailure()
    }

}