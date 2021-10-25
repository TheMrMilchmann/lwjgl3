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
        languageVersion.set(JavaLanguageVersion.of(16))
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
    project(":generator").tasks["generate"].dependsOn(this)
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

        options.release.set(8)
    }
    templatesSourceSet.compileJavaTaskName {
        onlyIf { rootProject.extra.properties.getOrDefault("binding.${project.name.removePrefix("lwjgl.")}", false) as Boolean }
    }
    whenTaskAdded {
        if (name == "compileTemplatesKotlin") {
            onlyIf {
                rootProject.extra.properties.getOrDefault("binding.${project.name.removePrefix("lwjgl.")}", false) as Boolean
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

lwjglBinding.apply {
    dependencies {
        implementation("core")
    }
}

dependencies {
    // Explicitly declare "default" configuration because for some reason Gradle does not default to "default" here.
    api(project(":lwjgl.core", configuration = "default"))
    templatesSourceSet.implementationConfigurationName(project(":lwjgl.core", configuration = templates.name))

    testImplementation(group = "org.testng", name = "testng", version = "7.0.0")
}