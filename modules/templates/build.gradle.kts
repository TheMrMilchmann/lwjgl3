/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import build.*

import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.*

plugins {
    kotlin("jvm", kotlinVersion)
}

tasks {
    "compileKotlin"(KotlinCompile::class) {
        Bindings.values()
            .filter { !isActive(it) }
            .forEach { exclude("${it.packageName!!.replace('.', '/')}/**") }
    }

    "generate"(JavaExec::class) {
        dependsOn(tasks["jar"])

        description = "Runs the Generator"

        main = "org.lwjgl.generator.GeneratorKt"
        classpath = configurations["compile"] + the<JavaPluginConvention>().sourceSets["main"].runtimeClasspath

        jvmArgs("-server")
        if (hasProperty("binding.DISABLE_CHECKS")) jvmArgs("-Dbinding.DISABLE_CHECKS=${properties["binding.DISABLE_CHECKS"]}")
        jvmArgs(Bindings.values().filter { isActive(it) }.map { "-Dbinding.${it.id}=true" })

        workingDir = rootProject.projectDir

        args(
            File(projectDir, "src/main/kotlin").absolutePath,
            mkdir(File(project(":modules:core").projectDir, "src/generated/")).absolutePath
        )

        standardOutput = System.out
        errorOutput = System.err
        outputs.upToDateWhen { false }
    }
}

dependencies {
    compile(kotlin("stdlib-jre8", kotlinVersion))
    compile(project(":modules:generator"))
}