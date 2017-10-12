/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.gradle.internal.jvm.*
import org.gradle.kotlin.dsl.*
import org.lwjgl.build.*
import org.lwjgl.build.natives.*

plugins {
    kotlin("jvm") version kotlinVersion apply false
}

apply {
    from("config/build-bindings.gradle.kts")
}

project.group = "org.lwjgl"
project.version = lwjglVersion

loadBuildProperties()

val isJDK9OrNewer = JavaVersion.current() >= JavaVersion.VERSION_1_9

allprojects {
    evaluationDependsOnChildren()

    repositories {
        mavenCentral()
    }
}

val pGenerator: Project = project(":modules:generator") {
    dependencies {
        "compile"(kotlin("stdlib-jre8", kotlinVersion))
        // println(Jvm.current().javaHome.resolve("lib/tools.jar")) TODO verify tools location accross different JVMs
        if (!isJDK9OrNewer) "compile"(files(Jvm.current().javaHome.resolve("lib/tools.jar")))
    }
}

project(":modules:templates") {
    tasks {
        "compileKotlin"(SourceTask::class) {
            bindings.filter { !isActive(it) }
                .forEach { exclude("${it.packageName!!.replace('.', '/')}/**") }
        }

        "generate"(JavaExec::class) {
            dependsOn(tasks["jar"])

            description = "Runs the Generator"

            main = "org.lwjgl.generator.GeneratorKt"
            classpath = configurations["compile"] + the<JavaPluginConvention>().sourceSets["main"].runtimeClasspath

            jvmArgs("-server")
            if (hasProperty("binding.DISABLE_CHECKS")) jvmArgs("-Dbinding.DISABLE_CHECKS=${properties["binding.DISABLE_CHECKS"]}")
            jvmArgs(bindings.filter { isActive(it) }.map { "-Dbinding.${it.id}=true" })

            workingDir = rootProject.projectDir

            args(
                File(projectDir, "src/main/kotlin").absolutePath,
                mkdir(File(project(":modules:core").projectDir, "src/generated/")).absolutePath
            )

            standardOutput = System.out
            errorOutput = System.err
        }
    }
}

project(":modules:core") {
    val java = the<JavaPluginConvention>()

    fun SourceDirectorySet.excludeDisabled() = this.apply {
        bindings.filter { !isActive(it) }
            .forEach { exclude("${it.packageName!!.replace('.', '/')}/**") }
    }

    java.sourceSets["main"].java.apply {
        srcDir("src/generated/java")
        excludeDisabled()
    }

    java.sourceSets["test"].java.excludeDisabled().apply {
        bindings.filter { !isActive(it) }
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
        bindings.forEach {
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

    val deployment = when {
        hasProperty("release") -> Deployment(
            LWJGLBuildType.RELEASE,
            "https://oss.sonatype.org/service/local/staging/deploy/maven2/",
            "sonatypeUsername", // TODO
            "sonatypePassword" // TODO
        )
        hasProperty("snapshot") -> {
            project.version = "${project.version}-SNAPSHOT"

            Deployment(
                LWJGLBuildType.SNAPSHOT,
                "https://oss.sonatype.org/content/repositories/snapshots/",
                "sonatypeUsername", // TODO
                "sonatypePassword" // TODO
            )
        }
        else -> Deployment(LWJGLBuildType.LOCAL, repositories.mavenLocal().url.toString())
    }

    configure<SigningExtension> {
        isRequired = deployment.type === LWJGLBuildType.RELEASE
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
                            the<SigningExtension>().signPom(this)
                        }

                        bindings.forEach {
                            pom.project {
                                withGroovyBuilder {
                                    "artifactId"(it.artifact)

                                    "name"(if (it.isCore) "The LWJGL core library." else "LWJGL - ${it.title} bindings")
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

                                    if (!it.isCore) {
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

        val jar: Jar by tasks.getting
        jar.enabled = false

        val javadoc: Javadoc by tasks.getting
        javadoc.enabled = false

        val javadocJar by tasks.creating
        val sourcesJar by tasks.creating
        val compileNative by tasks.creating

        bindings.map { (if (it.isCore) "lwjgl" else it.id) to it }.forEach {
            val id = it.first
            val binding = it.second

            val bindingJar = jarTask("$id-jar", binding) {
                destinationDir = jar.destinationDir
                baseName = if (binding.isCore) id else "lwjgl-$id"

                from(jar.source)
                binding.sourceConfig.invoke(this)

                onlyIf { isActive(binding) }
                jar.dependsOn(this)
            }

            val bindingJavadoc = javadocTask("$id-javadoc") {
                binding.sourceConfig.invoke(this)

                onlyIf { isActive(binding) }
                javadoc.dependsOn(this)
            }

            "$id-javadocJar"(Jar::class) {
                destinationDir = jar.destinationDir
                baseName = bindingJar.baseName
                classifier = "javadoc"

                from(bindingJavadoc.outputs)
                binding.sourceConfig.invoke(this)

                onlyIf { isActive(binding) }
                javadocJar.dependsOn(this)
            }

            "$id-sourcesJar"(Jar::class) {
                destinationDir = jar.destinationDir
                baseName = bindingJar.baseName
                classifier = "sources"

                from(java.sourceSets["main"].allSource)
                binding.sourceConfig.invoke(this)

                onlyIf { isActive(binding) }
                sourcesJar.dependsOn(this)
            }

            if (binding.hasNatives()) {
                compileNativeTask("$id-compileNative", binding) {
                    dependsOn(project(":modules:templates").tasks["generate"])

                    onlyIf { isActive(binding) }
                    compileNative.dependsOn(this)
                }
            }
        }
    }

}

fun NamedDomainObjectContainerScope<Task>.jarTask(name: String, binding: Binding, conf: Jar.() -> Unit) =
    name(Jar::class) {
        onlyIf { isActive(binding) }

        manifest {
            attributes(mapOf(
                "Specification-Title" to "Lightweight Java Game Library - ${binding.title}",
                "Specification-Version" to project.version as String,
                "Specification-Vendor" to "lwjgl.org",
                "Implementation-Title" to "module",
                "Implementation-Version" to "revision",
                "Implementation-Vendor" to "lwjgl.org",
                "Automatic-Module-Name" to "package"
            ))

            // TODO <attribute name="Multi-Release" value="true" if:true="@{multi-release}"/>
        }

        if (binding.platforms !== Platforms.Companion.JAVA_ONLY) {

        }
    }.also(conf)

fun NamedDomainObjectContainerScope<Task>.javadocTask(name: String, conf: Javadoc.() -> Unit) =
    name(Javadoc::class) {
        description = "Generates the LWJGL JavaDoc"

        (options as StandardJavadocDocletOptions).apply {
            source = "1.8"
            windowTitle = "LWJGL $version"
            encoding = "UTF-8"
            docEncoding = "UTF-8"
            charSet = "UTF-8"
            //  useexternalfile="true" // TODO
            isNoHelp = true
            isNoTree = true
            showFromPublic()
            isSplitIndex = true
            doclet = "org.lwjgl.system.ExcludeDoclet"
            //options.docletpath = "" // TODO
            maxMemory = "1G"
            isFailOnError = true

            docTitle = "<![CDATA[<h1>Lightweight Java Game Library</h1>]]>"
            bottom = "<![CDATA[<i>Copyright LWJGL. All Rights Reserved. <a href=\"https://www.lwjgl.org/license\">License terms</a>.</i>]]>"

            addStringOption("-Xdoclint")
            addStringOption("-Xmaxwarns", "1000")
            addStringOption("-J-Dfile.encoding", "UTF8")

            if (isJDK9OrNewer) {
                addStringOption("-html")
                addStringOption("-stylesheetfile", "${file("config")}/javadoc.css")
                addStringOption("--add-exports", "jdk.javadoc/com.sun.tools.doclets=ALL-UNNAMED")
            }
        }

        /* TODO
        <get-quiet name="favicon" url="https://www.lwjgl.org/favicon.ico" dest="${bin.html.javadoc}"/>

        <java classname="org.lwjgl.system.JavadocPostProcess" fork="true" failonerror="true">
        <classpath path="${bin.generator}"/>

        <arg value="${bin.html.javadoc}/org/lwjgl"/>
        </java>
        */
    }.also(conf)

fun NamedDomainObjectContainerScope<Task>.compileNativeTask(name: String, binding: Binding, conf: BuildNatives<*>.() -> Unit) =
    name(buildPlatform.taskClass) {
        spec {
            this.name = if (binding.isCore) binding.id else "lwjgl_${binding.id}" // TODO Refers to the wrong `name` without `this`. Kotlin Compiler bug?
            dest = File(buildDir, "bin/native/windows/x64/${if (binding.isCore) "core" else binding.id}")

            source(project.fileTree(project.projectDir))
        }
    }.also {
        buildPlatform.invoke(it.spec, binding)
    }.also(conf)

fun NamedDomainObjectContainerScope<Task>.uploadNativeTask(name: String, conf: Task.() -> Unit) =
    name {
        doFirst {
            if (!(("AWS_ACCESS_KEY_ID" in System.getenv() && "AWS_SECRET_ACCESS_KEY" in System.getenv()) || "AWS_CONFIG_FILE" in System.getenv()))
                throw RuntimeException("AWS credentials not configured.")
        }

        doLast {
            exec {
                executable = "git"
                args("log", "--pretty=format:%H", "HEAD~2..HEAD~1", "-1")
            }

            // TODO

            exec {
                executable = "aws"
                args("s3", "cp")
                args("s3://build.lwjgl.org/$buildType/${buildPlatform.classifier}/$buildArch/") // TODO

            }
        }
    }.also(conf)