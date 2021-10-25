/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
rootProject.name = "LWJGL 3"

include("extract")
project(":extract").projectDir = file("modules/extract")

include("generator")
project(":generator").projectDir = file("modules/generator")

include("samples")
project(":samples").projectDir = file("modules/samples")

file("modules/lwjgl").listFiles { file -> File(file, "build.gradle.kts").isFile }!!.forEach {
    include("lwjgl.${it.name}")
    project(":lwjgl.${it.name}").projectDir = it
}