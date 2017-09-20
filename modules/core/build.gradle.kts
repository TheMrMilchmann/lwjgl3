/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
plugins {
    java
    maven
    signing
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

dependencies {
    testCompile("org.testng:testng:$testNGVersion")
    testCompile("com.beust:jcommander:$jcommanderVersion")
}