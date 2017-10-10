/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.build

import org.gradle.api.*
import org.gradle.api.tasks.util.*
import org.gradle.internal.os.*
import org.lwjgl.build.natives.*
import java.io.*

data class Binding(
    val id: String,
    val title: String,
    val projectDescription: String,
    val packageName: String?,
    val artifact: String = "lwjgl-$id",
    val platforms: Array<Platforms>,
    val sourceConfig: PatternFilterable.() -> Unit,
    internal val isActive: Project.(b: Binding) -> Boolean,
    internal val buildLinuxConfig: (BuildNativesLinuxSpec.() -> Unit)?,
    internal val buildMacOSXConfig: (BuildNativesWindowsSpec.() -> Unit)?, // TODO
    internal val buildWindowsConfig: (BuildNativesWindowsSpec.() -> Unit)?
) {

    val isCore = id == "lwjgl"

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

fun Project.isActive(binding: Binding) = binding.isActive.invoke(rootProject, binding)

fun Binding.hasNatives() = getNativeBuildConfig() != null

fun Binding.getNativeBuildConfig() = when {
    OperatingSystem.current().isLinux   -> buildLinuxConfig
    OperatingSystem.current().isMacOsX  -> buildMacOSXConfig
    OperatingSystem.current().isWindows -> buildWindowsConfig
    else                                -> throw IllegalStateException("Native compilation for ${OperatingSystem.current()} not available.")
}