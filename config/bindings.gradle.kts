/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.lwjgl.build.*
import org.lwjgl.build.natives.*

val values = mutableListOf<Binding>()

fun binding(
    id: String,
    title: String,
    projectDescription: String,
    packageName: String?,
    artifact: String = "lwjgl-$id",
    platforms: Array<Platforms> = Platforms.ALL,
    sourceConfig: PatternFilterable.() -> Unit = { include("/${packageName!!.replace('.', '/')}/*") },
    isActive: Project.(b: Binding) -> Boolean = { this.hasProperty("binding.${it.id}") && properties["binding.${it.id}"].toString().toBoolean() },
    buildLinuxConfig: (BuildNativesLinuxSpec.() -> Unit)? = null,
    buildMacOSXConfig: (BuildNativesWindowsSpec.() -> Unit)? = null,
    buildWindowsConfig: (BuildNativesWindowsSpec.() -> Unit)? = null
) = Binding(id, title, projectDescription, packageName, artifact, platforms, sourceConfig, isActive, buildLinuxConfig, buildMacOSXConfig, buildWindowsConfig)
    .apply { values.add(this) }

binding(
    "lwjgl",
    "LWJGL",
    "The LWJGL core library.",
    null,
    artifact = "lwjgl",
    isActive = { true },
    sourceConfig = {
        include("org/lwjgl/*.java")
        include("org/lwjgl/system/**")

        exclude("org/lwjgl/system/jawt/**")
        exclude("org/lwjgl/system/jemalloc/**")
        exclude("org/lwjgl/system/rpmalloc/**")
    },
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
        if (project.isActive(JAWT)) include("$srcGenNative/system/jawt/*.c")
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

binding(
    "assimp",
    "Assimp",
    "A portable Open Source library to import various well-known 3D model formats in a uniform manner.",
    "org.lwjgl.assimp"
)

binding(
    "bgfx",
    "bgfx",
    "A cross-platform, graphics API agnostic rendering library. It provides a high performance, low level abstraction for common platform graphics APIs like OpenGL, Direct3D and Apple Metal.",
    "org.lwjgl.bgfx"
)

binding(
    "egl",
    "EGL",
    "An interface between Khronos rendering APIs such as OpenGL ES or OpenVG and the underlying native platform window system.",
    "org.lwjgl.egl",
    platforms = Platforms.JAVA_ONLY
)

binding(
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

binding(
    "jemalloc",
    "Jemalloc",
    "A general purpose malloc implementation that emphasizes fragmentation avoidance and scalable concurrency support.",
    "org.lwjgl.system.jemalloc"
)

binding(
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

binding(
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

binding(
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

binding(
    "nuklear",
    "Nuklear",
    "A minimal state immediate mode graphical user interface toolkit.",
    "org.lwjgl.nuklear",
    buildWindowsConfig = {
        compilerArgs("/I${File(project.projectDir, srcNative)}/nuklear")

        include("$srcGenNative/nuklear/*.c")
    }
)

binding(
    "openal",
    "OpenAL",
    "A cross-platform 3D audio API appropriate for use with gaming applications and many other types of audio applications.",
    "org.lwjgl.openal"
)

binding(
    "opencl",
    "OpenCL",
    "An open, royalty-free standard for cross-platform, parallel programming of diverse processors found in personal computers, servers, mobile devices and embedded platforms.",
    "org.lwjgl.opencl",
    platforms = Platforms.JAVA_ONLY
)

binding(
    "opengl",
    "OpenGL",
    "The most widely adopted 2D and 3D graphics API in the industry, bringing thousands of applications to a wide variety of computer platforms.",
    "org.lwjgl.opengl",
    buildWindowsConfig = {
        compilerArgs("/I${File(project.projectDir, srcNative)}/opengl")

        include("$srcGenNative/opengl/*.c")
    }
)

binding(
    "opengles",
    "OpenGL ES",
    "A royalty-free, cross-platform API for full-function 2D and 3D graphics on embedded systems - including consoles, phones, appliances and vehicles.",
    "org.lwjgl.opengles",
    buildWindowsConfig = {
        compilerArgs("/I${File(project.projectDir, srcNative)}/opengles")

        include("$srcGenNative/opengles/*.c")
    }
)

binding(
    "openvr",
    "OpenVR",
    "OpenVR is an API and runtime that allows access to VR hardware from multiple vendors without requiring that applications have specific knowledge of the hardware they are targeting.",
    "org.lwjgl.openvr",
    buildWindowsConfig = {
        include("$srcGenNative/openvr/*.c")
    }
)

binding(
    "ovr",
    "LibOVR",
    "The API of the Oculus SDK.",
    "org.lwjgl.ovr",
    platforms = arrayOf(Platforms.WINDOWS),
    buildWindowsConfig = {
        // TODO impl
    }
)

binding(
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

binding(
    "rpmalloc",
    "rpmalloc",
    "A public domain cross platform lock free thread caching 16-byte aligned memory allocator implemented in C.",
    "org.lwjgl.system.rpmalloc",
    buildWindowsConfig = {
        compilerArgs("/I${File(project.projectDir, srcNative)}/system/rpmalloc")

        include("$srcGenNative/system/rpmalloc/*.c")
    }
)

binding(
    "sse",
    "SSE",
    "Simple SSE intrinsics.",
    "org.lwjgl.util.simd",
    buildWindowsConfig = {
        compilerArgs("/I${File(project.projectDir, srcNative)}/util")

        include("$srcGenNative/util/simd/*.c")
    }
)

binding(
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

binding(
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

binding(
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

binding(
    "vulkan",
    "Vulkan",
    "A new generation graphics and compute API that provides high-efficiency, cross-platform access to modern GPUs used in a wide variety of devices from PCs and consoles to mobile phones and embedded platforms.",
    "org.lwjgl.vulkan",
    platforms = Platforms.JAVA_ONLY
)

binding(
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

binding(
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

project.extra["bindings"] = values