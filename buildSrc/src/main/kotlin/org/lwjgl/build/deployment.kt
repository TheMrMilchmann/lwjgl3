/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.build

import java.net.*
import org.gradle.api.*
import org.gradle.kotlin.dsl.*

enum class BuildType {
    LOCAL,
    SNAPSHOT,
    RELEASE
}

data class Deployment private constructor(
    val type: BuildType,
    val repo: URI,
    val user: String? = null,
    val password: String? = null
) {

    companion object {
        operator fun invoke(project: Project): Deployment = with (project) {
            val sonatypeUsername: String by project
            val sonatypePassword: String by project

            return when {
                hasProperty("release") -> Deployment(
                    type = BuildType.RELEASE,
                    repo = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/"),
                    user = sonatypeUsername,
                    password = sonatypePassword
                )
                hasProperty("snapshot") -> {
                    version = "$version-SNAPSHOT"
                    Deployment(
                        type = BuildType.SNAPSHOT,
                        repo = uri("https://oss.sonatype.org/content/repositories/snapshots/"),
                        user = sonatypeUsername,
                        password = sonatypePassword
                    )
                }
                else -> {
                    version = "$version-SNAPSHOT"
                    Deployment(
                        type = BuildType.LOCAL,
                        repo = repositories.mavenLocal().url
                    )
                }
            }.also {
                println("${it.type.name} BUILD")
            }
        }
    }

}