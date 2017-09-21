/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.build

import org.lwjgl.build.natives.*
import kotlin.reflect.*

enum class LWJGLBuildType {
    LOCAL,
    SNAPSHOT,
    RELEASE;

    override fun toString() = name.toLowerCase()
}

data class Deployment(
    val type: LWJGLBuildType,
    val repo: String,
    val user: String? = null,
    val password: String? = null
)

enum class Platforms(
    val classifier: String,
    val taskClass: KClass<out BuildNatives<*>>
) {
    LINUX("linux", BuildNativesLinux::class) {

        override fun invoke(spec: BuildNativesSpec, binding: Binding) {
            binding.buildLinuxConfig?.invoke(spec as BuildNativesLinuxSpec)
        }

    },
    MACOS("macos", BuildNativesWindows::class) { // TODO

        override fun invoke(spec: BuildNativesSpec, binding: Binding) {
            binding.buildMacOSXConfig?.invoke(spec as BuildNativesWindowsSpec) // TODO
        }

    },
    WINDOWS("windows", BuildNativesWindows::class) {

        override fun invoke(spec: BuildNativesSpec, binding: Binding) {
            binding.buildWindowsConfig?.invoke(spec as BuildNativesWindowsSpec)
        }

    };

    companion object {
        val JAVA_ONLY = emptyArray<Platforms>()
        val ALL = Platforms.values()
    }

    abstract fun invoke(spec: BuildNativesSpec, binding: Binding)

}