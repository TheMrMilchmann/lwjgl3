/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.lwjgl.build.*
import org.lwjgl.build.BuildType
import org.lwjgl.build.tasks.*

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

val deployment = Deployment(project)

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
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
    withType<JavaCompile> {
        options.encoding = "utf-8"
    }

    val generate = create<Generate>("generate") {
        classpath = templatesSourceSet.runtimeClasspath
    }
    compileJava {
        dependsOn(generate)
    }
    jar {
        archiveBaseName.set(lwjglBinding._artifact)
    }
    create<Jar>("sourcesJar") {
        archiveBaseName.set((tasks["jar"] as Jar).archiveBaseName)
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }
    javadoc {
        isFailOnError = false

        options.encoding = "utf-8"
    }
    create<Jar>("javadocJar") {
        dependsOn(javadoc)

        archiveBaseName.set((tasks["jar"] as Jar).archiveBaseName)
        archiveClassifier.set("javadoc")
        from(javadoc.get().outputs)
    }
}

publishing {
    repositories {
        maven {
            url = uri(deployment.repo)

            credentials {
                username = deployment.user
                password = deployment.password
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])

            pom {
                name.set(lwjglBinding._projectName)
                description.set(lwjglBinding._projectDesc)
                url.set("https://www.lwjgl.org")
                packaging = "jar"

                scm {
                    connection.set("scm:git:https://github.com/LWJGL/lwjgl3.git")
                    developerConnection.set("scm:git:https://github.com/LWJGL/lwjgl3.git")
                    url.set("https://github.com/LWJGL/lwjgl3.git")
                }

                licenses {
                    license {
                        name.set("BSD")
                        url.set("https://www.lwjgl.org/license")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("spasi")
                        name.set("Ioannis Tsakpinis")
                        email.set("iotsakp@gmail.com")
                        url.set("https://github.com/Spasi")
                    }
                }
            }
        }
    }
}

signing {
    isRequired = (deployment.type === BuildType.RELEASE)
    sign(publishing.publications)
}

dependencies {
    // Explicitly declare "default" configuration because for some reason Gradle does not default to "default" here.
    api(project(":lwjgl.core", configuration = "default"))
    templatesSourceSet.implementationConfigurationName(project(":lwjgl.core", configuration = templates.name))

    compileOnly(group = "com.google.code.findbugs", name = "jsr305", version = "3.0.2")
}