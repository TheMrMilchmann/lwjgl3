/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.jetbrains.kotlin.gradle.tasks.*

plugins {
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
                "-progressive",
                "-Xuse-experimental=kotlin.Experimental",
                "-Xuse-experimental=kotlin.contracts.ExperimentalContracts"
            )
        }
    }

    create<JavaExec>("run") {
        main = "org.lwjgl.extract.MainKt"
        workingDir = project.rootProject.projectDir

        classpath(
            sourceSets["main"].runtimeClasspath,
            File(project(":samples").projectDir, "src/test/resources")
        )

        jvmArgs(
            "-Dfile.encoding=utf-8",
            "-Dline.separator=\"\n\""
        )
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":lwjgl.llvm"))
}