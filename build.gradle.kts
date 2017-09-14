/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import org.gradle.internal.jvm.*
import org.gradle.kotlin.dsl.*

project.group = "org.lwjgl"
project.version = lwjglVersion

loadBuildProperties()

val isJDK9OrNewer = JavaVersion.current() != null && JavaVersion.current() >= JavaVersion.VERSION_1_9

allprojects {
    evaluationDependsOnChildren()

    repositories {
        mavenCentral()
    }
}

val pGenerator: Project = project(":modules:generator") {
    dependencies {
        "compile"(kotlin("stdlib-jre8", kotlinVersion))
        println(Jvm.current().javaHome.resolve("lib/tools.jar"))
        if (!isJDK9OrNewer) "compile"(files(Jvm.current().javaHome.resolve("lib/tools.jar")))
    }
}

project(":modules:core") {
    tasks {
        "javadoc"(Javadoc::class) {
            description = "Generates the LWJGL JavaDoc"

            (options as StandardJavadocDocletOptions).apply {
                source = "1.8"
                windowTitle = "LWJGL $version"
                encoding = "UTF-8"
                docEncoding = "UTF-8"
                charSet = "UTF-8"
                //  useexternalfile="true" // TODO
                isNoHelp = true
                isNoTree = true
                showFromPublic()
                isSplitIndex = true
                doclet = "org.lwjgl.system.ExcludeDoclet"
                //options.docletpath = "" // TODO
                maxMemory = "1G"
                isFailOnError = true

                docTitle = "<![CDATA[<h1>Lightweight Java Game Library</h1>]]>"
                bottom = "<![CDATA[<i>Copyright LWJGL. All Rights Reserved. <a href=\"https://www.lwjgl.org/license\">License terms</a>.</i>]]>"

                addStringOption("-Xdoclint")
                addStringOption("-Xmaxwarns", "1000")
                addStringOption("-J-Dfile.encoding", "UTF8")

                if (isJDK9OrNewer) {
                    addStringOption("-html")
                    addStringOption("-stylesheetfile", "${file("config")}/javadoc.css")
                    addStringOption("--add-exports", "jdk.javadoc/com.sun.tools.doclets=ALL-UNNAMED")
                }
            }

            /* TODO
            <get-quiet name="favicon" url="https://www.lwjgl.org/favicon.ico" dest="${bin.html.javadoc}"/>

            <java classname="org.lwjgl.system.JavadocPostProcess" fork="true" failonerror="true">
            <classpath path="${bin.generator}"/>

            <arg value="${bin.html.javadoc}/org/lwjgl"/>
            </java>
            */
        }


    }

}