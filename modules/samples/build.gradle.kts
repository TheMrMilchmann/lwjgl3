/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import me.champeau.jmh.*

plugins {
    kotlin("jvm")
    id("me.champeau.jmh")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

jmh {
    fork.set(1)
//    profilers.add("perf")
//    profilers.add("gc")
    warmupIterations.set(2)
    iterations.set(3)
    timeOnIteration.set("1s")
    warmup.set("1s")
    benchmarkMode.add("avgt")
    timeUnit.set("ns")
    jvmArgsPrepend.add("-server")
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

    named<JMHTask>("jmh") {
        if ("benchmark-filter" in project.properties) {
            includes.add(project.properties["benchmark-filter"] as String)
        } else {
            throw GradleException("Run with:\n\tgradlew jmh -Pbenchmark-filter=<regex>")
        }
    }
}

dependencies {
    rootProject.childProjects.values.filter { it.name.startsWith("lwjgl.") }.forEach { lwjglModule ->
        testImplementation(project(":${lwjglModule.name}"))
    }
    testImplementation(group = "org.joml", name = "joml", version = "1.9.22")
    testImplementation(group = "org.testng", name = "testng", version = "7.0.0")

    jmh(group = "org.openjdk.jmh", name = "jmh-core", version = "1.33")
    jmh(group = "org.openjdk.jmh", name = "jmh-generator-annprocess", version = "1.33")
}