package com.example.gradle

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

inline fun <reified T : Any> Project.configure(noinline configuration: T.() -> Unit) {
    extensions.create(T::class.java, configuration)
}

fun <T : Any> Project.extensions(name: String, extension: T) {
    extensions.create(name, extension)
}

fun <T : Any> Project.getExtension(name: String, block: T.() -> Unit = {}): T {
    return extensions.getByName(name, block)
}

inline fun <reified T : Any> ExtensionContainer.getByType(): T {
    return getByName(T::class.java.name)
}

inline fun <reified T : Task> TaskContainer.register(name: String, noinline configurationAction: T.() -> Unit): T {
    return register(name, T::class, configurationAction)
}

inline fun <reified T : Task> TaskContainer.register(name: String, type: KClass<T>, noinline configurationAction: T.() -> Unit): T {
    val constructor = type.primaryConstructor ?: error("Class ${T::class.simpleName} must have primary Constructor")
    val args = mutableMapOf<KParameter, Any?>()
    constructor.parameters.firstOrNull { it.name == "name" }?.let { args[it] = name }
    val task = constructor.callBy(args)
    configurationAction(task)
    add(name, task)
    return task
}

inline fun <reified T : Task> TaskContainer.getByName(name: String): T {
    return getByName(name) as? T ?: error("Task $name is not of type ${T::class.simpleName}")
}

fun Project.applyApplicationPlugin() {
    apply(ApplicationPlugin())
}

fun Project.apply(pluginId: String) {
    when (pluginId) {
        "java" -> apply(JavaPlugin())
        "application" -> apply(ApplicationPlugin())
        else -> println("Plugin '$pluginId' not found in registry.")
    }
}