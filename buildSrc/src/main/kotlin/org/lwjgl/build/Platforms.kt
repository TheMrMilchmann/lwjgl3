/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.build

import java.util.*

enum class Platforms(val classifier: String) {
    LINUX("linux"),
    LINUX_ARM64("linux-arm64"),
    LINUX_ARM32("linux-arm32"),
    MACOS("macos"),
    MACOS_ARM64("macos-arm64"),
    WINDOWS("windows"),
    WINDOWS_X86("windows-x86"),
    WINDOWS_ARM64("windows-arm64");

    companion object {
        val ALL = EnumSet.allOf(Platforms::class.java)
    }
}