/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.build.tasks

import org.gradle.api.*
import org.gradle.api.file.*
import org.gradle.api.tasks.*
import org.gradle.internal.jvm.*

@CacheableTask
open class Generate : DefaultTask() {

    @InputFiles
    @Classpath
    lateinit var classpath: FileCollection

    @Input
    lateinit var bindings: List<String>

    @TaskAction
    fun generate() {
        project.javaexec {
            executable = Jvm.current().javaExecutable.absolutePath
            main = "org.lwjgl.generator.GeneratorKt"
            workingDir = project.rootProject.projectDir

            classpath(this@Generate.classpath)

            jvmArgs(
                "-Dfile.encoding=utf-8",
                "-Dline.separator=\"\n\""
            )
            bindings.forEach { binding -> jvmArgs("-Dbinding.$binding=true") }

            args(project.rootProject.file("modules/lwjgl"))
        }.rethrowFailure()
    }

}