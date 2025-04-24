package com.example

import com.example.gradle.DependencyConfig
import com.example.gradle.DependencyManagementPlugin
import com.example.gradle.ExecTask
import com.example.gradle.HelloPlugin
import com.example.gradle.Project
import com.example.gradle.buildScript
import com.example.gradle.configure
import com.example.gradle.getExtension
import com.example.gradle.register

fun Project.configurationPhrase() {
    apply(HelloPlugin())
    apply(DependencyManagementPlugin())

    repositories {
        mavenCentral()
        google()
        maven { "https://maven.aliyun.com/repository/public" }
    }

    dependencies {
        implementation("com.google.android.material:material:1.4.0")
        testImplementation("junit:junit:4.13")
    }

    getExtension<DependencyConfig>("dependencyConfig") {
        libVersion = "7.3.0"
    }

    configure<DependencyConfig> {
        libVersion = "1.1.1"
    }

    tasks.register("printHello") {
        group = "build"
        description = "Prints a message to the console"
        doLast {
            println("Hello, world!4444")
        }
    }

    tasks.register("compilerJava") {}
    tasks.register("processResources") { dependsOn("compilerJava") }
    tasks.register("classes") { dependsOn("processResources") }
    tasks.register("jar") { dependsOn("classes") }
    tasks.register<ExecTask>("printDir") {
        doLast { commandLine("cmd", "/c", "dir") }
    }

    tasks.register("runAll") {
        dependsOn("jar", "sayHello", "checkDependencies", "printDir")
        doFirst { println("Hello, doFirst!") }
        doLast {
            println("Hello, world!4444")
            println("Hello, world!5555")
        }
    }
}

fun Project.executionPhrase(command: String) {
    showConfiguration()
    executeTask(command)
}

fun main(args: Array<String>) {
    val command = "runAll"

    val project = buildScript {
        configurationPhrase()        // Configuration Phrase
        executionPhrase(command)     // Execution Phrase
    }

    println("\nBUILD SUCCESSFUL!")
}