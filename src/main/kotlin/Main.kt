package com.example

import com.example.gradle.*

fun Project.configurationPhase() {
    apply("application")
    apply(HelloPlugin())
    apply(DependencyManagementPlugin())

    repositories {
        mavenCentral()
        google()
        maven { "https://maven.aliyun.com/repository/public" }
    }

    group = "com.example"
    version = "1.0.0"

    dependencies {
        implementation("com.google.android.material:material:1.4.0")
        testImplementation("junit:junit:4.13")
    }

    application {
        mainClass = "com.example.HelloDialogKt"
        applicationName = "simple-gradle"
        applicationClassPath = "build/classes/kotlin/main"
    }

    tasks.register("printHello") {
        group = "help"
        description = "Prints a message to the console"
        doLast {
            println("Hello, world!!!!")
        }
    }

    tasks.register<ExecTask>("printDir") {
        doLast { commandLine("cmd", "/c", "dir") }
    }

    tasks.register("runAll") {
        dependsOn("jar", "sayHello", "checkDependencies", "printDir", "run")
        doFirst { println("Hello, doFirst!") }
        doLast {
            println("Hello, doLast!4444")
            println("Hello, doLast!5555")
        }
    }

    getExtension<DependencyConfig>("dependencyConfig") { libVersion = "7.3.0" }
    configure<DependencyConfig> { libVersion = "1.1.1" }
}

fun Project.executionPhase(command: String) {
    showConfiguration()
    executeTask(command)
}

fun main(args: Array<String>) {
    val command = "runAll"

    val project = buildScript {
        configurationPhase()        // Configuration Phase
        executionPhase(command)     // Execution Phase
    }

    println("\nBUILD SUCCESSFUL!")
}