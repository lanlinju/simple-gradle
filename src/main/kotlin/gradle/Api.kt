package com.example.gradle

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DependsOn(val plugins: Array<KClass<out Plugin>>)

interface Action<T> {
    fun execute(t: T)
}

interface Project {
    val name: String
    var version: String
    var group: String
    val tasks: TaskContainer
    val extensions: ExtensionContainer
    fun repositories(configuration: RepositoryHandler.() -> Unit)
    fun dependencies(configuration: DependencyHandler.() -> Unit)
    fun apply(plugin: Plugin)
    fun executeTask(name: String)
    fun showConfiguration()
}

interface RepositoryHandler {
    fun mavenCentral()
    fun google()
    fun maven(url: () -> String)
    fun listRepositories(): List<String>
}

interface DependencyHandler {
    fun implementation(dependencyNotation: String)
    fun testImplementation(dependencyNotation: String)
    fun listDependencies(): List<String>
}

interface TaskContainer {
    fun register(name: String, configurationAction: Task.() -> Unit): Task
    fun getByName(name: String): Task?
    fun add(name: String, task: Task)
    fun allTasks(): Collection<Task>
}

interface Task {
    val name: String
    var group: String?
    var description: String?
    fun dependsOn(vararg taskNames: String)
    fun doFirst(action: () -> Unit)
    fun doLast(action: () -> Unit)
    fun execute(project: Project)
}

interface Plugin {
    fun apply(project: Project)
}

interface ExtensionContainer {
    fun <T : Any> create(name: String, type: Class<T>, configuration: T.() -> Unit = {}): T
    fun <T : Any> create(type: Class<T>, configuration: T.() -> Unit = {}): T
    fun <T : Any> create(name: String, extension: T)
    fun <T : Any> getByName(name: String, block: T.() -> Unit = {}): T
}
