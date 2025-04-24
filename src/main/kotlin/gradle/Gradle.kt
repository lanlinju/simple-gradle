@file:Suppress("UNCHECKED_CAST")

package com.example.gradle

class DefaultRepositoryHandler() : RepositoryHandler {
    private val repos = mutableListOf<String>()
    override fun mavenCentral() {
        repos += "https://maven.google.com"
    }

    override fun google() {
        repos += "https://repo1.maven.org/maven2"
    }

    override fun maven(url: () -> String) {
        repos += url()
    }

    override fun listRepositories() = repos
}

class DefaultDependencyHandler() : DependencyHandler {
    private val dependencies = mutableListOf<String>()
    override fun implementation(dependencyNotation: String) {
        dependencies += "implementation \"$dependencyNotation\""
    }

    override fun testImplementation(dependencyNotation: String) {
        dependencies += "testImplementation \"$dependencyNotation\""
    }

    override fun listDependencies() = dependencies
}

open class DefaultTask(
    override val name: String,
    override var group: String? = null,
    override var description: String? = null
) : Task {
    private val actions = mutableListOf<() -> Unit>()
    private val dependencies = mutableListOf<String>()
    private var executed = false

    override fun dependsOn(vararg taskNames: String) {
        dependencies += taskNames
    }

    override fun doFirst(action: () -> Unit) = actions.add(0, action)
    override fun doLast(action: () -> Unit) {
        actions.add(action)
    }

    override fun execute(project: Project) {
        if (executed) return
        dependencies.forEach { project.executeTask(it) }
        println("> Task :$name")
        actions.forEach { it() }
        executed = true
    }
}

class DefaultTaskContainer() : TaskContainer {
    private val tasks = mutableMapOf<String, Task>()
    override fun register(name: String, configurationAction: Task.() -> Unit): Task {
        val task = DefaultTask(name)
        tasks[name] = task
        configurationAction(task)
        return task
    }

    override fun getByName(name: String): Task? = tasks[name]
    override fun allTasks(): Collection<Task> = tasks.values
}

class DefaultExtensionContainer : ExtensionContainer {
    val extensions = mutableMapOf<String, Any>()
    override fun <T : Any> create(type: Class<T>, configuration: T.() -> Unit): T {
        return create(type.name, type, configuration)
    }

    override fun <T : Any> create(name: String, type: Class<T>, configuration: T.() -> Unit): T {
        val extension = extensions.getOrPut(name) { type.newInstance() } as T
        configuration(extension)
        return extension
    }

    override fun <T : Any> create(name: String, extension: T) {
        extensions[name] = extension
    }

    override fun <T : Any> getByName(name: String, block: T.() -> Unit): T {
        return (extensions[name] as? T)?.apply(block)
            ?: throw IllegalStateException("Extension '$name' not found")
    }
}

class DefaultProject(override val name: String) : Project {
    override val tasks: TaskContainer = DefaultTaskContainer()
    override val extensions = DefaultExtensionContainer()
    private val repos = DefaultRepositoryHandler()
    private val depens = DefaultDependencyHandler()
    private val appliedPlugins = mutableMapOf<String, Plugin>() // 记录已应用的插件

    override fun repositories(configuration: RepositoryHandler.() -> Unit) {
        configuration(repos)
    }

    override fun dependencies(configuration: DependencyHandler.() -> Unit) {
        configuration(depens)
    }

    override fun apply(plugin: Plugin) {
        val pluginName = plugin.javaClass.simpleName
        if (appliedPlugins.contains(pluginName)) return
        appliedPlugins[pluginName] = plugin
        plugin.apply(this)
    }

    override fun executeTask(name: String) {
        tasks.getByName(name)?.execute(this) ?: println("Task '$name' not found")
    }

    private fun listPlugins(): List<String> {
        return appliedPlugins.keys.toList()
    }

    override fun showConfiguration() {
        println("Project: $name")
        println("Plugins:")
        listPlugins().forEach { println("  - plugin: $it") }
        println("Repositories:")
        repos.listRepositories().forEach { println("  - $it") }
        println("Dependencies:")
        depens.listDependencies().forEach { println("  - $it") }
        println("Tasks:")
        tasks.allTasks().forEach { println("  - ${it.group ?: "other"}: ${it.name}") }
        println()
    }
}

fun buildScript(name: String = "my-project", scope: Project.() -> Unit): Project {
    val project = DefaultProject(name)
    scope(project)
    return project
}

fun main(args: Array<String>) {
    // Configuration Phrase
    val project = buildScript {

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

        tasks.register("run") {
            dependsOn("jar", "sayHello", "checkDependencies")
            doFirst { println("Hello, doFirst!") }
            doLast {
                println("Hello, world!4444")
                println("Hello, world!5555")
            }
        }
    }

    // Execution Phrase
    with(project) {
//        val taskName = args.getOrNull(0) ?: showConfiguration()
        showConfiguration()
        executeTask("run")
    }

    println("\nBUILD SUCCESSFUL!")
}













