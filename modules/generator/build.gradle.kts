/*
* Copyright LWJGL. All rights reserved.
* License terms: https://www.lwjgl.org/license
*/
@file:Suppress("UnstableApiUsage")
import org.jetbrains.kotlin.gradle.tasks.*
import org.lwjgl.build.*
import org.lwjgl.build.tasks.*

plugins {
    `java-library`
    kotlin("jvm")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf(
                "-Xno-call-assertions",
                "-Xno-param-assertions",
                "-Xreport-perf",
                "-progressive"
            )
        }
    }

    /*
     * Running the Generator
     *
     * The "generate" task of this subproject is resonsible for running the
     * generator. However, there are multiple things to keep in mind before
     * attempting to work on this task:
     *  1. The generator must support only generating a selection of bindings.
     *  2. Due to the current design of the generator, it may only be invoked
     *     once.
     *  3. The task is quite expensive and therefore must be made cachable.
     *
     * To support generating code for bindings selectively, the task execution
     * graph of the current Gradle invocation is inspected. If the "generate" [1]
     * task of a binding subproject was executed or is scheduled to be executed,
     * the generator will generate code for the binding.
     * However, since this information is not available at configuration time,
     * the task graph must be inspected before, during, or after the execution
     * of a task. The "configureGenerate" serves as a "configuration tasks"
     * which will gather the bindings to generate from the task graph and
     * configure the "generate" task accordingly, thus making the latter
     * cacheable again.
     *
     * Finally, there is a "generate" task in the root project which can be
     * thought of as "generate-everything" that depends on the "generate" tasks
     * of every binding subprojects and, thus, runs the generator including all
     * bindings.
     *
     *
     * [1] A "generate" task of a binding subproject depends on
     * ":generator:generate" and is a requirement for the bindings build process.
     */

    val generate = create<Generate>("generate")

    val configureGenerate = create("configureGenerate") {
        doLast {
            /*
             * This is a workaround to avoid rerunning the generator again if
             * all bindings that are active now were active during the
             * previous iteration (given that all other up-to-date checks
             * succeeded).
             *
             * This is especially useful when running tools (such as extract)
             * since this only depends on a subset of available bindings.
             */
            val prevActiveBindings = if (generate.bindings.isFile) emptyList() else generate.bindings.readText().lines()

            var classpath: FileCollection = files()
            val bindings = mutableListOf<String>()

            rootProject.childProjects.values.filter { it.name.startsWith("lwjgl.") }.forEach { lwjglModule ->
                if (gradle.taskGraph.hasTask(lwjglModule.tasks["generate"])) {
                    fun String.enable() {
                        rootProject.extra["binding.${this.removePrefix("lwjgl.")}"] = true
                    }

                    lwjglModule.name.enable()
                    lwjglModule.the<LWJGLModule>().bindingDependencies.forEach(String::enable)

                    classpath += lwjglModule.sourceSets["templates"].runtimeClasspath
                    bindings.add(lwjglModule.name.removePrefix("lwjgl."))
                }
            }

            generate.classpath = classpath
            if (!prevActiveBindings.containsAll(bindings)) generate.bindings.writeText(bindings.joinToString(separator = "\n"))
        }
    }

    generate.apply {
        dependsOn(configureGenerate)

        classpath = files()

        bindings = File(mkdir("$buildDir/lwjgl"), "active-bindings.txt")
        if (!bindings.isFile) bindings.writeText("")
    }

    create<JavaExec>("formatter") {
        dependsOn(compileJava)
        description = "Runs the template formatter tool"

        mainClass.set("org.lwjgl.generator.util.TemplateFormatter")
        workingDir = project.rootProject.projectDir

        classpath(
            sourceSets["main"].runtimeClasspath,
            File(project(":samples").projectDir, "src/test/resources")
        )
    }

    create<JavaExec>("urlValidator") {
        dependsOn(compileJava)
        description = "Runs the URL validator tool"

        mainClass.set("org.lwjgl.generator.util.URLValidator")
        workingDir = project.rootProject.projectDir

        classpath(
            sourceSets["main"].runtimeClasspath,
            File(project(":samples").projectDir, "src/test/resources")
        )

        doFirst {
            if (!project.hasProperty("args")) error("No arguments have been specified. Please use gradlew :generator:urlValidator -Pargs=<args>")
            jvmArgs(project.property("args"))
        }
    }
}

dependencies {
    api(kotlin("stdlib-jdk8"))
}