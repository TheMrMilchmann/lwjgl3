/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.lwjgl.build.*

plugins {
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val lwjglCore = extensions.create<LWJGLBinding>("lwjgl", project)
sourceSets["main"].java.srcDir("src/generated/java")

val templatesSourceSet = sourceSets.create("templates")
val templates = configurations.create("templates") {
    isCanBeConsumed = true
    isCanBeResolved = false

    extendsFrom(configurations[templatesSourceSet.implementationConfigurationName], configurations[templatesSourceSet.runtimeOnlyConfigurationName])
}

val templatesJar = tasks.create<Jar>("templatesJar") {
    from(templatesSourceSet.output)
}

artifacts {
    add(templates.name, templatesJar)
}

dependencies {
    // Explicitly declare "default" configuration because for some reason Gradle does not default to "default" here.
    api(project(":lwjgl.core", configuration = "default"))
    templatesSourceSet.implementationConfigurationName(project(":lwjgl.core", configuration = templates.name))

    compileOnly(group = "com.google.code.findbugs", name = "jsr305", version = "3.0.2")
}