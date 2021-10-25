/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.lwjgl.build.*

plugins {
    `lwjgl-core`
    kotlin("jvm")
}

lwjgl {
    artifact = "lwjgl"
    projectName = "LWJGL"
    projectDesc = "The LWJGL core library."
    platforms = Platforms.ALL
}

tasks {
    val compileJava9 = create<JavaCompile>("compileJava9") {
        destinationDirectory.set(File(buildDir, "classes/java9/main"))
        options.release.set(9)

        val java9Source = fileTree("src/main/java9") {
            include("**/*.java")
        }

        source = java9Source
        options.sourcepath = files(sourceSets["main"].java.srcDirs) + files(java9Source.dir)
    }

    val compileJava10 = create<JavaCompile>("compileJava10") {
        destinationDirectory.set(File(buildDir, "classes/java10/main"))
        options.release.set(10)

        val java10Source = fileTree("src/main/java10") {
            include("**/*.java")
        }

        source = java10Source
        options.sourcepath = files(sourceSets["main"].java.srcDirs) + files(java10Source.dir)
    }

    jar {
        into("META-INF/versions/9") {
            from(compileJava9.outputs.files.filter(File::isDirectory)) {
                includeEmptyDirs = false
            }
        }
        into("META-INF/versions/10") {
            from(compileJava10.outputs.files.filter(File::isDirectory)) {
                includeEmptyDirs = false
            }
        }
    }
}

dependencies {
    compileOnlyApi(group = "com.google.code.findbugs", name = "jsr305", version = "3.0.2")
}