/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import build.*
import build.native.*
import org.gradle.api.tasks.bundling.*
import org.gradle.internal.jvm.*
import org.gradle.kotlin.dsl.*

project.group = "org.lwjgl"
project.version = lwjglVersion

loadBuildProperties()

val isJDK9OrNewer = JavaVersion.current() != null && JavaVersion.current() >= JavaVersion.VERSION_1_9

class Bindings {

    companion object {
        val values = mutableListOf<Binding>()

        private fun binding(
            id: String,
            title: String,
            projectDescription: String,
            packageName: String?,
            artifact: String = "lwjgl-$id",
            platforms: Array<Platforms> = Platforms.ALL,
            isActive: Project.(b: Binding) -> Boolean = { this.hasProperty("binding.${it.id}") && properties["binding.${it.id}"].toString().toBoolean() },
            buildLinuxConfig: (BuildNativesWindowsSpec.() -> Unit)? = null,
            buildMacOSXConfig: (BuildNativesWindowsSpec.() -> Unit)? = null,
            buildWindowsConfig: (BuildNativesWindowsSpec.() -> Unit)? = null
        ) = Binding(id, title, projectDescription, packageName, artifact, platforms, isActive, buildLinuxConfig, buildMacOSXConfig, buildWindowsConfig)
            .apply { values.add(this) }

        val CORE = binding(
            "lwjgl",
            "LWJGL",
            "The LWJGL core library.",
            null,
            artifact = "lwjgl",
            isActive = { true },
            buildLinuxConfig = {

            },
            buildMacOSXConfig = {

            },
            buildWindowsConfig = {
                compilerArgs("/I${File(project.projectDir, "src/main/c")}/system/dyncall")

                include("$srcNative/system/*.c")
                exclude("$srcNative/system/lwjgl_malloc.c")
                include("$srcGenNative/system/*.c")
                include("$srcGenNative/system/dyncall/*.c")
                if (project.isActive(Bindings.JAWT)) include("$srcGenNative/system/jawt/*.c")
                include("$srcGenNative/system/jni/*.c")
                include("$srcGenNative/system/libc/*.c")
                include("$srcGenNative/system/windows/*.c")

                beforeLink { // TODO might want to make this a task dep
                    updateDependency("dyncall", "${project.buildArch}/dyncall_s.lib")
                    updateDependency("dyncallback", "${project.buildArch}/dyncallback_s.lib")
                    updateDependency("dynload", "${project.buildArch}/dynload_s.lib")
                }

                link = File(project.rootDir, "lib/windows/x64").listFiles { file -> file.name.matches("dyn(.*)\\.lib".toRegex()) }
                    .map { it.absolutePath }
            }
        )

        val ASSIMP = binding(
            "assimp",
            "Assimp",
            "A portable Open Source library to import various well-known 3D model formats in a uniform manner.",
            "org.lwjgl.assimp"
        )

        val BFX = binding(
            "bgfx",
            "bgfx",
            "A cross-platform, graphics API agnostic rendering library. It provides a high performance, low level abstraction for common platform graphics APIs like OpenGL, Direct3D and Apple Metal.",
            "org.lwjgl.bgfx"
        )

        val EGL = binding(
            "egl",
            "EGL",
            "An interface between Khronos rendering APIs such as OpenGL ES or OpenVG and the underlying native platform window system.",
            "org.lwjgl.egl",
            platforms = Platforms.JAVA_ONLY
        )

        val GLFW = binding(
            "glfw",
            "GLFW",
            "An multi-platform library for OpenGL, OpenGL ES and Vulkan development on the desktop. It provides a simple API for creating windows, contexts and surfaces, receiving input and events.",
            "org.lwjgl.glfw"
        )

        val JAWT = binding(
            "jawt",
            "JAWT",
            "The AWT native interface.",
            "org.lwjgl.system.jawt",
            platforms = Platforms.JAVA_ONLY
        )

        val JEMALLOC = binding(
            "jemalloc",
            "Jemalloc",
            "A general purpose malloc implementation that emphasizes fragmentation avoidance and scalable concurrency support.",
            "org.lwjgl.system.jemalloc"
        )

        val LMDB = binding(
            "lmdb",
            "LMDB",
            "LWJGL - A compact, fast, powerful, and robust database that implements a simplified variant of the BerkeleyDB (BDB) API.",
            "org.lwjgl.util.lmdb",
            buildWindowsConfig = {
                val inheritDest = dest

                beforeCompile {
                    compileNativesWindows {
                        dest = inheritDest
                        flags = mutableListOf(*"/W0 /I${File(project.projectDir, "src/main/c")}/util/lmdb".split(" ").toTypedArray())

                        setSource(project.fileTree(project.projectDir))
                        include("$srcNative/util/lmdb/*.c")
                    }
                }

                compilerArgs("/I${File(project.projectDir, srcNative)}/util/lmdb")

                include("$srcGenNative/util/lmdb/*.c")

                linkArgs("ntdll.lib", "Advapi32.lib")
            }
        )

        val NANOVG = binding(
            "nanovg",
            "NanoVG",
            "A small antialiased vector graphics rendering library for OpenGL.",
            "org.lwjgl.nanovg",
            buildWindowsConfig = {
                compilerArgs("/I${File(project.projectDir, srcNative)}/nanovg")
                compilerArgs("/I${File(project.projectDir, srcNative)}/stb")

                include("$srcNative/system/lwjgl_malloc.c")
                include("$srcGenNative/nanovg/*.c")
            }
        )

        val NFD = binding(
            "nfd",
            "Native File Dialog",
            "A tiny, neat C library that portably invokes native file open and save dialogs.",
            "org.lwjgl.util.nfd",
            buildWindowsConfig = {
                val inheritDest = dest

                beforeCompile {
                    compileNativesWindows {
                        dest = inheritDest
                        flags = mutableListOf()

                        compilerArgs("/I${File(project.projectDir, srcNative)}/util/nfd")
                        compilerArgs("/I${File(project.projectDir, srcNative)}/util/nfd/include")

                        setSource(project.fileTree(project.projectDir))
                        include("$srcNative/util/nfd/nfd_common.c")
                        include("$srcNative/util/nfd/nfd_win.cpp")
                    }
                }

                compilerArgs("/I${File(project.projectDir, srcNative)}/util/nfd")
                compilerArgs("/I${File(project.projectDir, srcNative)}/util/nfd/include")

                include("$srcNative/system/lwjgl_malloc.c")
                include("$srcGenNative/util/nfd/*.c")

                linkArgs("Ole32.lib", "Shell32.lib")
            }
        )

        val NUKLEAR = binding(
            "nuklear",
            "Nuklear",
            "A minimal state immediate mode graphical user interface toolkit.",
            "org.lwjgl.nuklear",
            buildWindowsConfig = {
                compilerArgs("/I${File(project.projectDir, srcNative)}/nuklear")

                include("$srcGenNative/nuklear/*.c")
            }
        )

        val OPENAL = binding(
            "openal",
            "OpenAL",
            "A cross-platform 3D audio API appropriate for use with gaming applications and many other types of audio applications.",
            "org.lwjgl.openal"
        )

        val OPENCL = binding(
            "opencl",
            "OpenCL",
            "An open, royalty-free standard for cross-platform, parallel programming of diverse processors found in personal computers, servers, mobile devices and embedded platforms.",
            "org.lwjgl.opencl",
            platforms = Platforms.JAVA_ONLY
        )

        val OPENGL = binding(
            "opengl",
            "OpenGL",
            "The most widely adopted 2D and 3D graphics API in the industry, bringing thousands of applications to a wide variety of computer platforms.",
            "org.lwjgl.opengl",
            buildWindowsConfig = {
                compilerArgs("/I${File(project.projectDir, srcNative)}/opengl")

                include("$srcGenNative/opengl/*.c")
            }
        )

        val OPENGLES = binding(
            "opengles",
            "OpenGL ES",
            "A royalty-free, cross-platform API for full-function 2D and 3D graphics on embedded systems - including consoles, phones, appliances and vehicles.",
            "org.lwjgl.opengles",
            buildWindowsConfig = {
                compilerArgs("/I${File(project.projectDir, srcNative)}/opengles")

                include("$srcGenNative/opengles/*.c")
            }
        )

        val OPENVR = binding(
            "openvr",
            "OpenVR",
            "OpenVR is an API and runtime that allows access to VR hardware from multiple vendors without requiring that applications have specific knowledge of the hardware they are targeting.",
            "org.lwjgl.openvr",
            buildWindowsConfig = {
                include("$srcGenNative/openvr/*.c")
            }
        )

        val OVR = binding(
            "ovr",
            "LibOVR",
            "The API of the Oculus SDK.",
            "org.lwjgl.ovr",
            platforms = arrayOf(Platforms.WINDOWS),
            buildWindowsConfig = {
                // TODO impl
            }
        )

        val PAR = binding(
            "par",
            "par",
            "Generate parametric surfaces and other simple shapes.",
            "org.lwjgl.util.par",
            buildWindowsConfig = {
                compilerArgs("/I${File(project.projectDir, srcNative)}/util/par")

                include("$srcNative/system/lwjgl_malloc.c")
                include("$srcGenNative/util/par/*.c")
            }
        )

        val RPMALLOC = binding(
            "rpmalloc",
            "rpmalloc",
            "A public domain cross platform lock free thread caching 16-byte aligned memory allocator implemented in C.",
            "org.lwjgl.system.rpmalloc",
            buildWindowsConfig = {
                compilerArgs("/I${File(project.projectDir, srcNative)}/system/rpmalloc")

                include("$srcGenNative/system/rpmalloc/*.c")
            }
        )

        val SSE = binding(
            "sse",
            "SSE",
            "Simple SSE intrinsics.",
            "org.lwjgl.util.simd",
            buildWindowsConfig = {
                compilerArgs("/I${File(project.projectDir, srcNative)}/util")

                include("$srcGenNative/util/simd/*.c")
            }
        )

        val STB = binding(
            "stb",
            "stb",
            "Single-file public domain libraries for fonts, images, ogg vorbis files and more.",
            "org.lwjgl.stb",
            buildWindowsConfig = {
                compilerArgs("/I${File(project.projectDir, srcNative)}/stb")

                include("$srcNative/system/lwjgl_malloc.c")
                include("$srcGenNative/stb/*.c")
            }
        )

        val TINYEXR = binding(
            "tinyexr",
            "Tiny OpenEXR",
            "A small library to load and save OpenEXR(.exr) images.",
            "org.lwjgl.util.tinyexr",
            buildWindowsConfig = {
                val inheritDest = dest

                beforeCompile {
                    compileNativesWindows {
                        dest = inheritDest

                        compilerArgs("/I${File(project.projectDir, srcNative)}/util/tinyexr")

                        setSource(project.fileTree(project.projectDir))
                        include("$srcNative/util/tinyexr/*.cc")
                    }
                }

                compilerArgs("/I$srcNative/util/tinyexr")

                include("$srcGenNative/util/tinyexr/*.c")
            }
        )

        val TINYFD = binding(
            "tinyfd",
            "Tiny File Dialogs",
            "Provides basic modal dialogs.",
            "org.lwjgl.util.tinyfd",
            buildWindowsConfig = {
                val inheritDest = dest

                beforeCompile {
                    compileNativesWindows {
                        dest = inheritDest

                        compilerArgs("/I${File(project.projectDir, srcNative)}/util/tinyfd")

                        setSource(project.fileTree(project.projectDir))
                        include("$srcNative/util/tinyfd/*.c")
                    }
                }

                compilerArgs("/I${File(project.projectDir, srcNative)}/util/tinyfd")

                include("$srcGenNative/util/tinyfd/*.c")

                linkArgs("Comdlg32.lib", "Ole32.lib", "Shell32.lib", "User32.lib")
            }
        )

        val VULKAN = binding(
            "vulkan",
            "Vulkan",
            "A new generation graphics and compute API that provides high-efficiency, cross-platform access to modern GPUs used in a wide variety of devices from PCs and consoles to mobile phones and embedded platforms.",
            "org.lwjgl.vulkan",
            platforms = Platforms.JAVA_ONLY
        )

        val XXHASH = binding(
            "xxhash",
            "xxHash",
            "An Extremely fast Hash algorithm, running at RAM speed limits.",
            "org.lwjgl.util.xxhash",
            buildWindowsConfig = {
                compilerArgs("/I${File(project.projectDir, srcNative)}/system")
                compilerArgs("/I${File(project.projectDir, srcNative)}/util/xxhash")

                include("$srcNative/system/lwjgl_malloc.c")
                include("$srcGenNative/util/xxhash/*.c")
            }
        )

        val YOGA = binding(
            "yoga",
            "yoga",
            "An open-source, cross-platform layout library that implements Flexbox.",
            "org.lwjgl.util.yoga",
            buildWindowsConfig = {
                val inheritDest = dest

                beforeCompile {
                    compileNativesWindows {
                        dest = inheritDest

                        compilerArgs("/I${File(project.projectDir, srcNative)}/util/yoga")

                        setSource(project.fileTree(project.projectDir))
                        include("$srcNative/util/yoga/*.c")
                    }
                }

                compilerArgs("/I${File(project.projectDir, srcNative)}/util/yoga")

                include("$srcGenNative/util/yoga/*.c")
            }
        )
    }

}

allprojects {
    evaluationDependsOnChildren()

    repositories {
        mavenCentral()
    }
}

val pGenerator: Project = project(":modules:generator") {
    dependencies {
        "compile"(kotlin("stdlib-jre8", kotlinVersion))
        println(Jvm.current().javaHome.resolve("lib/tools.jar"))
        if (!isJDK9OrNewer) "compile"(files(Jvm.current().javaHome.resolve("lib/tools.jar")))
    }
}

project(":modules:templates") {
    tasks {
        "compileKotlin"(SourceTask::class) {
            Bindings.values.filter { !isActive(it) }
                .forEach { exclude("${it.packageName!!.replace('.', '/')}/**") }
        }

        "generate"(JavaExec::class) {
            dependsOn(tasks["jar"])

            description = "Runs the Generator"

            main = "org.lwjgl.generator.GeneratorKt"
            classpath = configurations["compile"] + the<JavaPluginConvention>().sourceSets["main"].runtimeClasspath

            jvmArgs("-server")
            if (hasProperty("binding.DISABLE_CHECKS")) jvmArgs("-Dbinding.DISABLE_CHECKS=${properties["binding.DISABLE_CHECKS"]}")
            jvmArgs(Bindings.values.filter { isActive(it) }.map { "-Dbinding.${it.id}=true" })

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
        Bindings.values.filter { !isActive(it) }
            .forEach { exclude("${it.packageName!!.replace('.', '/')}/**") }
    }

    java.sourceSets["main"].java.apply {
        srcDir("src/generated/java")
        excludeDisabled()
    }

    java.sourceSets["test"].java.excludeDisabled().apply {
        Bindings.values.filter { !isActive(it) }
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
        Bindings.values.forEach {
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

    configure<SigningExtension> {
        isRequired = deployment.type === BuildType.RELEASE
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

                        Bindings.values.forEach {
                            pom.project {
                                withGroovyBuilder {
                                    "artifactId"(it.artifact)

                                    "name"(if (it === Bindings.CORE) "The LWJGL core library." else "LWJGL - ${it.title} bindings")
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

        val jar: Jar by tasks.getting
        jar.apply {
            baseName = "lwjgl"


        }

        val javadoc = javadocTask("javadoc") {

        }

        "javadocJar"(Jar::class) {
            destinationDir = jar.destinationDir
            baseName = jar.baseName
            classifier = "javadoc"

            from(javadoc.outputs)
        }

        "sourcesJar"(Jar::class) {
            destinationDir = jar.destinationDir
            baseName = jar.baseName
            classifier = "sources"

            from(java.sourceSets["main"].allSource)
            // TODO includes
        }

        Bindings.values.filter { it !== Bindings.CORE }.forEach {
            val bindingJar = jarTask("${it.id}-jar", it) {
                destinationDir = jar.destinationDir
                baseName = "lwjgl-${it.id}"

                from(jar.source)
                include("/${it.packageName!!.replace('.', '/')}/*")
             }

            val bindingJavadoc = javadocTask("${it.id}-javadoc") {
                include("/${it.packageName!!.replace('.', '/')}/*")
            }

            "${it.id}-javadocJar"(Jar::class) {
                destinationDir = jar.destinationDir
                baseName = bindingJar.baseName
                classifier = "javadoc"

                from(bindingJavadoc.outputs)
            }

            "${it.id}-sourcesJar"(Jar::class) {
                destinationDir = jar.destinationDir
                baseName = bindingJar.baseName
                classifier = "sources"

                from(java.sourceSets["main"].allSource)
                include("/${it.packageName!!.replace('.', '/')}/*")
            }
        }
    }

    val compileNative = tasks.create("compileNative")

    lwjglRegisterNativeTasks(compileNative, Bindings.values) {
        dependsOn(project(":modules:templates").tasks["generate"])
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

fun NamedDomainObjectContainerScope<Task>.uploadNative(name: String, conf: Task.() -> Unit) =
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