package com.example.gradle

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

inline fun <reified T : Task> TaskContainer.register(name: String, noinline configuration: T.() -> Unit): T {
    val kClass = T::class
    val constructor = kClass.primaryConstructor ?: error("Class ${T::class.simpleName} must have primary Constructor")
    val args = mutableMapOf<KParameter, Any?>()
    constructor.parameters.firstOrNull { it.name == "name" }?.let { args[it] = name }
    val task = constructor.callBy(args)
    task.configuration()
    return task
}
