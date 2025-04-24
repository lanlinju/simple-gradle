package gradle

import com.example.gradle.DefaultProject
import com.example.gradle.DefaultTask
import com.example.gradle.DefaultTaskContainer
import com.example.gradle.DependencyConfig
import com.example.gradle.configure
import com.example.gradle.extensions
import com.example.gradle.getByType
import com.example.gradle.getExtension
import com.example.gradle.register
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GradleTest {

    @Test
    fun testExecuteTask() {
        val project = DefaultProject("test-project")
        project.tasks.register("testTask") {
            doLast {
                println("Executing testTask")
            }
        }

        // 执行任务并验证输出
        project.executeTask("testTask")

        // 验证任务是否被正确执行
        val task = project.tasks.getByName("testTask")
        assertEquals("testTask", task?.name)
    }

    @Test
    fun testExecuteNonExistentTask() {
        val project = DefaultProject("test-project")

        // 执行不存在的任务并验证输出
        project.executeTask("nonExistentTask")

        // 验证任务不存在时的处理
        val task = project.tasks.getByName("nonExistentTask")
        assertEquals(null, task)
    }

    @Test
    fun testConfigureExtension() {
        val project = DefaultProject("test-project")
        project.configure<DependencyConfig> {
            libVersion = "1.0.0"
            testVersion = "2.0.0"
        }

        val config = project.extensions.getByType<DependencyConfig>()
        assertEquals("1.0.0", config.libVersion)
        assertEquals("2.0.0", config.testVersion)
    }

    @Test
    fun testAddAndGetExtension() {
        val project = DefaultProject("test-project")
        val config = DependencyConfig().apply {
            libVersion = "1.0.0"
            testVersion = "2.0.0"
        }

        project.extensions("dependencyConfig", config)

        val retrievedConfig = project.getExtension<DependencyConfig>("dependencyConfig")
        assertEquals("1.0.0", retrievedConfig.libVersion)
        assertEquals("2.0.0", retrievedConfig.testVersion)
    }

    @Test
    fun testGetNonExistentExtension() {
        val project = DefaultProject("test-project")

        try {
            project.getExtension<DependencyConfig>("nonExistentConfig")
        } catch (e: IllegalStateException) {
            assertEquals("Extension 'nonExistentConfig' not found", e.message)
        }
    }

    @Test
    fun testExtensionContainerCreate() {
        val project = DefaultProject("test-project")
        project.extensions.create("dependencyConfig", DependencyConfig::class.java)

        val config = project.getExtension<DependencyConfig>("dependencyConfig")
        assertNotNull(config)
    }

    @Test
    fun testExtensionContainerGetByName() {
        val project = DefaultProject("test-project")
        val config = DependencyConfig().apply {
            libVersion = "1.0.0"
            testVersion = "2.0.0"
        }

        project.extensions("dependencyConfig", config)

        val retrievedConfig = project.extensions.getByName<DependencyConfig>("dependencyConfig")
        assertNotNull(retrievedConfig)
        assertEquals("1.0.0", retrievedConfig.libVersion)
    }

    @Test
    fun testExtensionContainerGetByType() {
        val project = DefaultProject("test-project")
        project.configure<DependencyConfig>() {
            libVersion = "1.0.0"
            testVersion = "2.0.0"
        }
        val retrievedConfig = project.extensions.getByType<DependencyConfig>()
        assertNotNull(retrievedConfig)
        assertEquals("1.0.0", retrievedConfig.libVersion)
    }

    // MyTask.kt
    class MyTask(
        group: String? = null,
        name: String,
        description: String? = null
    ) : DefaultTask(name, group, description) {
//        override var name: String = "UNSET"
    }

    @Test
    fun testRegister() {
        val container = DefaultTaskContainer()

        val task = container.register<MyTask>("hello-world") {
            group = "build"
            description = "just a test"
        }

        // 1) 打印验证
        println("task.name = ${task.name}")             // 应该输出 "hello-world"
        println("task.group = ${task.group}")           // "build"
        println("task.description = ${task.description}") // "just a test"

        // 2) 简单断言（如果你用的是测试框架也可以这样写）
        check(task.name == "hello-world") { "Name was not set!" }
    }
}