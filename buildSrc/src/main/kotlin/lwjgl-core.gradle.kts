/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.lwjgl.build.*

plugins {
    `java-library`
    `maven-publish`
    signing
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }

    withSourcesJar()
    withJavadocJar()
}

val lwjglBinding = extensions.create<LWJGLBinding>("lwjgl", project)
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

tasks {
    val generate = create("generate") {
        dependsOn(project(":generator").tasks["generate"])
    }
    compileJava {
        dependsOn(generate)

        options.encoding = "utf-8"
        options.compilerArgs = listOf(
            "-Xlint:all",
            "-XDignore.symbol.file" // Suppresses internal API (e.g. Unsafe) usage warnings
        )
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

dependencies {
    // Explicitly declare "default" configuration because for some reason Gradle does not default to "default" here.
    templatesSourceSet.implementationConfigurationName(project(":generator", configuration = "default"))

    testImplementation(group = "org.testng", name = "testng", version = "7.0.0")
}