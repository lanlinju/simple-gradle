package com.example.gradle

import kotlin.system.exitProcess

class ExecTask(
    name: String,
    group: String? = "exec",
    description: String? = "Execute external process"
) : DefaultTask(name, group, description) {
    var executable: String = ""
    var commandLine: List<String> = emptyList()

    fun commandLine(vararg args: String) {
        commandLine = args.toList()
        executable = args.first()
        val processBuilder = ProcessBuilder(commandLine).apply {
            redirectErrorStream(true)
        }
        val process = processBuilder.start()
        process.inputStream.bufferedReader().use { reader ->
            reader.lineSequence().forEach(::println)
        }
        val exitCode = process.waitFor()
        println("Exit Code: $exitCode")
    }
}