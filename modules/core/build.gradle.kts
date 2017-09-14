/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import build.*
import build.native.*
import org.gradle.internal.jvm.*

plugins {
    java
    maven
    signing
}

evaluationDependsOn(":modules:templates")

val deployment = when {
    hasProperty("release") -> Deployment(
        BuildType.RELEASE,
        "https://oss.sonatype.org/service/local/staging/deploy/maven2/",
        "sonatypeUsername", // TODO
        "sonatypePassword" // TODO
    )
    hasProperty("snapshot") -> {
        project.version = "${project.version}-SNAPSHOT"

        Deployment(
            BuildType.SNAPSHOT,
            "https://oss.sonatype.org/content/repositories/snapshots/",
            "sonatypeUsername", // TODO
            "sonatypePassword" // TODO
        )
    }
    else -> Deployment(BuildType.LOCAL, repositories.mavenLocal().url.toString())
}

fun SourceDirectorySet.excludeDisabled() = this.apply {
    Bindings.values()
        .filter { !isActive(it) }
        .forEach { exclude("${it.packageName!!.replace('.', '/')}/**") }
}

java.sourceSets["main"].java.apply {
    srcDir("src/generated/java")
    excludeDisabled()
}

java.sourceSets["test"].java.excludeDisabled().apply {
    Bindings.values()
        .filter { !isActive(it) }
        .forEach { exclude("org/lwjgl/demo/${it.packageName!!.removePrefix("org.lwjgl.").replace('.', '/')}/**") }
}

artifacts {
    /*
    Ideally, we'd have the following structure:
    -------------------------------------------
    lwjgl
        lwjgl-windows (depends on lwjgl)
    glfw (depends on lwjgl)
        glfw-windows (depends on glfw & lwjgl-windows)
    stb (depends on lwjgl)
        stb-windows (depends on stb & lwjgl-windows)
    -------------------------------------------
    If a user wanted to use GLFW + stb in their project, running on
    the Windows platform, they'd only have to define glfw-windows
    and stb-windows as dependencies. This would automatically
    resolve stb, glfw, lwjgl and lwjgl-windows as transitive
    dependencies. Unfortunately, it is not possible to define such
    a relationship between Maven artifacts when using classifiers.
    A method to make this work is make the natives-<arch> classified
    JARs separate artifacts. We do not do it for aesthetic reasons.
    Instead, we assume that a tool is available (on the LWJGL website)
    that automatically generates POM/Gradle dependency structures for
    projects wanting to use LWJGL. The output is going to be verbose;
    the above example is going to look like this in Gradle:
    -------------------------------------------
    compile 'org.lwjgl:lwjgl:$lwjglVersion' // NOTE: this is optional, all binding artifacts have a dependency on lwjgl
        runtime 'org.lwjgl:lwjgl:$lwjglVersion:natives-$lwjglArch'
    compile 'org.lwjgl:lwjgl-glfw:$lwjglVersion'
        runtime 'org.lwjgl:lwjgl-glfw:$lwjglVersion:natives-$lwjglArch'
    compile 'org.lwjgl:lwjgl-stb:$lwjglVersion'
        runtime 'org.lwjgl:lwjgl-stb:$lwjglVersion:natives-$lwjglArch'
    -------------------------------------------
    and a whole lot more verbose in Maven. Hopefully, the automation
    is going to alleviate the pain.
    */
    Bindings.values().forEach {
        val binding = it

        if (isActive(binding)) {
            add("archives", binding.artifactNotation())
            add("archives", binding.artifactNotation("sources"))
            add("archives", binding.artifactNotation("javadoc"))
            binding.platforms.forEach {
                add("archives", binding.artifactNotation("natives-${it.classifier}"))
            }
        }
    }
}

signing {
    isRequired = deployment.type == BuildType.RELEASE
    sign(configurations["archives"])
}

tasks {
    "compileJava"(JavaCompile::class) {
        dependsOn(project(":modules:templates").tasks["generate"])

        /*
         * Supresses internal API (e.g. Unsafe) usage warnings
         *
         * Gradle (by default) uses the JDK Compiler API which does not support
         * implementation specific command line options such as
         * "-XDignore.symbol.file=true". However, we can circumvent this issue
         * by simply forcing gradle to use the javac executable.
         *
         * See:
         * - https://discuss.gradle.org/t/gradle-2-10-cant-suppress-java-1-8-compiler-warnings/13921/4
         * - http://mail.openjdk.java.net/pipermail/compiler-dev/2013-October/007660.html
         */
        options.isFork = true
        options.forkOptions.executable = Jvm.current().javacExecutable.absolutePath
        options.compilerArgs.add("-XDignore.symbol.file=true")
    }

    "test"(Test::class) {
        description = "Runs the LWJGL test suite"

        useTestNG()

        // TODO
    }

    "demo"(JavaExec::class) {
        description = "Runs an LWJGL demo"
        isIgnoreExitValue = false

        classpath = java.sourceSets["test"].runtimeClasspath

        standardOutput = System.out
        errorOutput = System.err
        outputs.upToDateWhen { false }

        doFirst {
            main = System.getProperty("class", null) ?: throw IllegalArgumentException("Please use -Dclass=<class> to specify the demo main class to run.")
        }
    }

    "uploadArchives"(Upload::class) {
        repositories {
            withConvention(MavenRepositoryHandlerConvention::class) {
                mavenDeployer {
                    withGroovyBuilder {
                        "repository"("url" to deployment.repo) {
                            "authentication"(
                                "userName" to deployment.user,
                                "password" to deployment.password
                            )
                        }
                    }

                    beforeDeployment {
                        signing.signPom(this)
                    }

                    Bindings.values().forEach {
                        pom.project {
                            withGroovyBuilder {
                                "artifactId"(it.artifact)

                                "name"(it.projectName)
                                "description"(it.projectDescription)
                                "packaging"("jar")
                                "url"("https://www.lwjgl.org")

                                "scm" {
                                    "connection"("scm:git:https://github.com/LWJGL/lwjgl3.git")
                                    "developerConnection"("scm:git:https://github.com/LWJGL/lwjgl3.git")
                                    "url"("https://github.com/LWJGL/lwjgl3.git")
                                }

                                "licenses" {
                                    "license" {
                                        "name"("BSD")
                                        "url"("https://www.lwjgl.org/license")
                                        "distribution"("repo")
                                    }
                                }

                                "developers" {
                                    "developer" {
                                        "id"("spasi")
                                        "name"("Ioannis Tsakpinis")
                                        "email"("iotsakp@gmail.com")
                                        "url"("https://github.com/Spasi")
                                    }
                                }

                                if (it != Bindings.CORE) {
                                    "dependencies" {
                                        "dependency" {
                                            "groupId"("org.lwjgl")
                                            "artifactId"("lwjgl")
                                            "version"(project.version)
                                            "scope"("compile")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

val compileNative = tasks.create("compileNative")

lwjglRegisterNativeTasks(compileNative) {
    dependsOn(project(":modules:templates").tasks["generate"])
}

dependencies {
    testCompile("org.testng:testng:$testNGVersion")
    testCompile("com.beust:jcommander:$jcommanderVersion")
}