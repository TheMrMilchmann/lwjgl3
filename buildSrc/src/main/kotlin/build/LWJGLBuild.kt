/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package build

enum class BuildType {
    LOCAL,
    SNAPSHOT,
    RELEASE;

    override fun toString() = name.toLowerCase()
}

data class Deployment(
    val type: BuildType,
    val repo: String,
    val user: String? = null,
    val password: String? = null
)

enum class Platforms(
    val classifier: String
) {
    LINUX("linux"),
    MACOS("macos"),
    WINDOWS("windows");

    companion object {
        val JAVA_ONLY = emptyArray<Platforms>()
        val ALL = Platforms.values()
    }
}