/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
plugins {
    id("me.champeau.gradle.jmh") version "0.5.0" apply false
}

val lwjglVersion: String by project

allprojects {
    group = "org.lwjgl"
    version = lwjglVersion

    repositories {
        mavenCentral()
    }
}

tasks {
    create("generate") {
        project(":generator").tasks.whenTaskAdded {
            if (name == "generate") this@create.dependsOn(this)
        }

        rootProject.childProjects.values.filter { it.name.startsWith("lwjgl.") }.forEach { lwjglModule ->
            lwjglModule.tasks.whenTaskAdded {
                if (name == "generate") this@create.dependsOn(this)
            }
        }
    }
}