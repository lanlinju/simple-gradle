package gradle

import com.example.gradle.DefaultProject
import com.example.gradle.ExecTask
import com.example.gradle.getByName
import com.example.gradle.register
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.test.Test
import kotlin.test.assertEquals


class TaskTest {
    @Test
    fun testExecTask() {
        val project = DefaultProject("test-project")
        project.tasks.register<ExecTask>("execTask") {
            commandLine("cmd", "/c", "dir")
        }

        project.executeTask("execTask")
        assertEquals("cmd /c dir", project.tasks.getByName<ExecTask>("execTask").commandLine.joinToString(" "))
    }

    @Test
    fun testProcessBuilder() {
        val processBuilder = ProcessBuilder("cmd", "/c", "dir").apply {
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