/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
plugins {
    kotlin("jvm") version "1.5.31" apply false
    id("me.champeau.jmh") version "0.6.6" apply false
}

val lwjglVersion: String by project

subprojects {
    group = "org.lwjgl"
    version = lwjglVersion

    repositories {
        mavenCentral()
    }
}

tasks {
    // Convenience for `gradlew :extract:run`
    create("extract") {
        project(":extract").tasks.whenTaskAdded {
            if (name == "run") this@create.dependsOn(this)
        }
    }

    // See :generator:generate for more information
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

    create<Javadoc>("javadoc") {
        // TODO impl
    }
}