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
    WINDOWS("windows"),
    WINDOWS_X86("windows-x86");

    companion object {
        val ALL = EnumSet.allOf(Platforms::class.java)
    }
}