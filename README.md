
# simple-gradle

A minimal implementation of a Gradle-like DSL in Kotlin, demonstrating how to configure projects, plugins, tasks, dependencies, and repositories in a style similar to Gradle's build scripts.

## Features

- **Gradle-style DSL**: Configure plugins, dependencies, repositories, and tasks using a familiar syntax.
- **Plugin System**: Apply plugins with dependencies between plugins.
- **Task System**: Register and execute tasks with dependencies, actions (`doFirst`, `doLast`), and custom task types.
- **Extension Mechanism**: Add and configure custom extensions for plugins.
- **Repository & Dependency Management**: Declare repositories and dependencies in a DSL block.

## Example Usage

```kotlin
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
```

## Running

The entry point is `main` in `Main.kt`. It builds the project using the DSL and executes the specified task.

## Structure

- `gradle/Api.kt`: Core interfaces for Project, Task, Plugin, etc.
- `gradle/Gradle.kt`: Default implementations and the buildScript entry point.
- `gradle/Plugins.kt`: Example plugins and extension classes.
- `gradle/Tasks.kt`: Example custom task (`ExecTask`).
- `gradle/Extensions.kt`: DSL extension functions.
- `Main.kt`: Example usage and entry point.
