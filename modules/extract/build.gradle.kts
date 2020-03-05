/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
plugins {
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
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
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":lwjgl.llvm"))
}