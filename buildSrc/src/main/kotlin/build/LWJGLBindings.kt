/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package build

import build.native.*
import buildArch
import org.gradle.api.*
import org.gradle.internal.os.*
import java.io.*

enum class Bindings(
    val id: String,
    val projectName: String,
    val projectDescription: String,
    val packageName: String?,
    val artifact: String = "lwjgl-$id",
    val platforms: Array<Platforms> = Platforms.ALL,
    internal val isActive: Project.(b: Bindings) -> Boolean = { hasProperty("binding.${it.id}") && properties["binding.${it.id}"].toString().toBoolean() },
    internal val buildLinuxConfig: (BuildNativesWindows.() -> Unit)? = null,
    internal val buildMacOSXConfig: (BuildNativesWindows.() -> Unit)? = null,
    internal val buildWindowsConfig: (BuildNativesWindows.() -> Unit)? = null
) {
    CORE(
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
            spec {
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
        }
    ),
    ASSIMP(
        "assimp",
        "LWJGL - Assimp bindings",
        "A portable Open Source library to import various well-known 3D model formats in a uniform manner.",
        "org.lwjgl.assimp"
    ),
    BGFX(
        "bgfx",
        "LWJGL - bgfx bindings",
        "A cross-platform, graphics API agnostic rendering library. It provides a high performance, low level abstraction for common platform graphics APIs like OpenGL, Direct3D and Apple Metal.",
        "org.lwjgl.bgfx"
    ),
    EGL(
        "egl",
        "LWJGL - EGL bindings",
        "An interface between Khronos rendering APIs such as OpenGL ES or OpenVG and the underlying native platform window system.",
        "org.lwjgl.egl",
        platforms = Platforms.JAVA_ONLY
    ),
    GLFW(
        "glfw",
        "LWJGL - GLFW bindings",
        "An multi-platform library for OpenGL, OpenGL ES and Vulkan development on the desktop. It provides a simple API for creating windows, contexts and surfaces, receiving input and events.",
        "org.lwjgl.glfw"
    ),
    JAWT(
        "jawt",
        "LWJGL - JAWT bindings",
        "The AWT native interface.",
        "org.lwjgl.system.jawt",
        platforms = Platforms.JAVA_ONLY
    ),
    JEMALLOC(
        "jemalloc",
        "LWJGL - Jemalloc bindings",
        "A general purpose malloc implementation that emphasizes fragmentation avoidance and scalable concurrency support.",
        "org.lwjgl.system.jemalloc"
    ),
    LMDB(
        "lmdb",
        "LWJGL - LMDB bindings",
        "LWJGL - A compact, fast, powerful, and robust database that implements a simplified variant of the BerkeleyDB (BDB) API.",
        "org.lwjgl.util.lmdb",
        buildWindowsConfig = {
            spec {
                beforeCompile {
                    compileNativesWindows {
                        dest = this@spec.dest

                        setSource(project.fileTree(project.projectDir))
                        flags = mutableListOf(*"/W0 /I${File(project.projectDir, "src/main/c")}\\util\\lmdb".split(" ").toTypedArray())

                        include("/util/lmdb/*.c")
                    }
                }

                include("$srcGenNative/util/lmdb/*.c")

                compilerArgs("/I${File(project.projectDir, "src/main/c")}\\util\\lmdb")

                linkArgs("ntdll.lib", "Advapi32.lib")
            }
        }
    ),
    NANOVG(
        "nanovg",
        "LWJGL - NanoVG bindings",
        "A small antialiased vector graphics rendering library for OpenGL.",
        "org.lwjgl.nanovg"
    ),
    NFD(
        "nfd",
        "LWJGL - Native File Dialog bindings",
        "A tiny, neat C library that portably invokes native file open and save dialogs.",
        "org.lwjgl.util.nfd"
    ),
    NUKLEAR(
        "nuklear",
        "LWJGL - Nuklear bindings",
        "A minimal state immediate mode graphical user interface toolkit.",
        "org.lwjgl.nuklear"
    ),
    OPENAL(
        "openal",
        "LWJGL - OpenAL bindings",
        "A cross-platform 3D audio API appropriate for use with gaming applications and many other types of audio applications.",
        "org.lwjgl.openal"
    ),
    OPENCL(
        "opencl",
        "LWJGL - OpenCL bindings",
        "An open, royalty-free standard for cross-platform, parallel programming of diverse processors found in personal computers, servers, mobile devices and embedded platforms.",
        "org.lwjgl.opencl",
        platforms = Platforms.JAVA_ONLY
    ),
    OPENGL(
        "opengl",
        "LWJGL - OpenGL bindings",
        "The most widely adopted 2D and 3D graphics API in the industry, bringing thousands of applications to a wide variety of computer platforms.",
        "org.lwjgl.opengl"
    ),
    OPENGLES(
        "opengles",
        "LWJGL - OpenGL ES bindings",
        "A royalty-free, cross-platform API for full-function 2D and 3D graphics on embedded systems - including consoles, phones, appliances and vehicles.",
        "org.lwjgl.opengles"
    ),
    OPENVR(
        "openvr",
        "LWJGL - OpenVR bindings",
        "OpenVR is an API and runtime that allows access to VR hardware from multiple vendors without requiring that applications have specific knowledge of the hardware they are targeting.",
        "org.lwjgl.openvr"
    ),
    OVR(
        "ovr",
        "LWJGL - OVR bindings",
        "The API of the Oculus SDK.",
        "org.lwjgl.ovr",
        platforms = arrayOf(Platforms.WINDOWS)
    ),
    PAR(
        "par",
        "LWJGL - par_shapes bindings",
        "Generate parametric surfaces and other simple shapes.",
        "org.lwjgl.util.par"
    ),
    RPMALLOC(
        "rpmalloc",
        "LWJGL - rpmalloc bindings",
        "A public domain cross platform lock free thread caching 16-byte aligned memory allocator implemented in C.",
        "org.lwjgl.system.rpmalloc"
    ),
    SSE(
        "sse",
        "LWJGL - SSE bindings",
        "Simple SSE intrinsics.",
        "org.lwjgl.util.simd"
    ),
    STB(
        "stb",
        "LWJGL - stb bindings",
        "Single-file public domain libraries for fonts, images, ogg vorbis files and more.",
        "org.lwjgl.stb"
    ),
    TINYEXR(
        "tinyexr",
        "LWJGL - Tiny OpenEXR bindings",
        "A small library to load and save OpenEXR(.exr) images.",
        "org.lwjgl.util.tinyexr"
    ),
    TINYFD(
        "tinyfd",
        "LWJGL - Tiny File Dialogs bindings",
        "Provides basic modal dialogs.",
        "org.lwjgl.util.tinyfd"
    ),
    VULKAN(
        "vulkan",
        "LWJGL - Vulkan bindings",
        "A new generation graphics and compute API that provides high-efficiency, cross-platform access to modern GPUs used in a wide variety of devices from PCs and consoles to mobile phones and embedded platforms.",
        "org.lwjgl.vulkan",
        platforms = Platforms.JAVA_ONLY
    ),
    XXHASH(
        "xxhash",
        "LWJGL - xxHash bindings",
        "An Extremely fast Hash algorithm, running at RAM speed limits.",
        "org.lwjgl.util.xxhash"
    ),
    YOGA(
        "yoga",
        "LWJGL - Yoga bindings",
        "An open-source, cross-platform layout library that implements Flexbox.",
        "org.lwjgl.util.yoga"
    );

    fun artifactNotation(classifier: String? = null) =
        if (classifier == null) {
            mapOf(
                "file" to File(""),
                "name" to artifact,
                "type" to "jar"
            )
        } else {
            mapOf(
                "file" to File(""),
                "name" to artifact,
                "type" to "jar",
                "classifier" to classifier
            )
        }
}

fun Project.isActive(binding: Bindings) = binding.isActive.invoke(rootProject, binding)

fun Bindings.hasNatives() = getNativeBuildConfig() != null

fun Bindings.getNativeBuildConfig() = when {
    OperatingSystem.current().isLinux   -> compileNativeLinux
    OperatingSystem.current().isMacOsX  -> compileNativeMacOSX
    OperatingSystem.current().isWindows -> buildWindowsConfig
    else                                -> throw IllegalStateException("Native compilation for ${org.gradle.internal.os.OperatingSystem.current()} not available.")
}