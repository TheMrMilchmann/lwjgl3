/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
plugins {
    kotlin("jvm")
    id("me.champeau.jmh")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    val updateAssets = create("updateAssets") {

    }

    create<JavaExec>("demo") {
        dependsOn(compileTestJava, updateAssets)

        /*
        <local name="spawn"/>
        <condition property="spawn" value="true" else="false">
            <isset property="jitwatch"/>
        </condition>
         */

        jvmArgs("-XstartOnFirstThread") // TODO platform.macos only
//        jvmArgs("-Xcheck:jni")

//        jvmArgs(
//            "-XX:+UnlockDiagnosticVMOptions", "-XX:+TraceClassLoading", "-XX:+LogCompilation", "-XX:+PrintAssembly",
//            "-XX:PrintAssemblyOptions=intel", "-XX:-TieredCompilation", "-XX:-UseCompressedOops", "-XX:LogFile=${jitwatch}"
//        ) // TODO if:set="jitwatch"

        doFirst {
            if (!project.hasProperty("class")) error("Please use -Pclass=<class>; to specify the demo main class to run.")
            mainClass.set(project.properties["class"] as String)

            if (project.hasProperty("args")) {
                jvmArgs(project.properties["args"])
            }
        }
    }

    test {
        useTestNG()
    }
}

dependencies {
    rootProject.childProjects.values.filter { it.name.startsWith("lwjgl.") }.forEach { lwjglModule ->
        testImplementation(project(":${lwjglModule.name}"))
    }
    testImplementation(group = "org.joml", name = "joml", version = "1.9.22")
    testImplementation(group = "org.testng", name = "testng", version = "7.0.0")
    testCompileOnly(group = "com.google.code.findbugs", name = "jsr305", version = "3.0.2")
}