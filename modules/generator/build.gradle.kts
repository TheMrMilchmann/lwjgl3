/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.jetbrains.kotlin.gradle.tasks.*
import org.lwjgl.build.tasks.*

plugins {
    `java-library`
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
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

    val generate = create<Generate>("generate")

    val configureGenerate = create("configureGenerate") {
        doLast {
            var classpath: FileCollection = files()
            val bindings = mutableListOf<String>()

            rootProject.childProjects.values.filter { it.name.startsWith("lwjgl.") }.forEach { lwjglModule ->
                if (gradle.taskGraph.hasTask(lwjglModule.tasks["generate"])) {
                    classpath += lwjglModule.sourceSets["templates"].runtimeClasspath
                    bindings.add(lwjglModule.name.removePrefix("lwjgl."))
                }
            }

            generate.classpath = classpath
            generate.bindings = bindings
        }
    }

    getByName<Generate>(generate.name) {
        dependsOn(configureGenerate)
        onlyIf { bindings.isNotEmpty() }

        classpath = files()
        bindings = mutableListOf()
    }

}

dependencies {
    api(kotlin("stdlib-jdk8"))
}