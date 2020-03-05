/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
plugins {
    kotlin("jvm")
    id("me.champeau.gradle.jmh")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    test {
        useTestNG()
    }
}

dependencies {
    rootProject.childProjects.values.filter { it.name.startsWith("lwjgl.") }.forEach { lwjglModule ->
        testImplementation(project(":${lwjglModule.name}"))
    }
    testImplementation(group = "org.joml", name = "joml", version = "1.9.22")
    testImplementation(group = "org.testng", name = "testng", version = "7.0.0")
    testCompileOnly(group = "com.google.code.findbugs", name = "jsr305", version = "3.0.2")
}