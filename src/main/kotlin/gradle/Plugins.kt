package com.example.gradle

class HelloPlugin() : Plugin {
    override fun apply(project: Project) {
        project.tasks.register("sayHello") {
            group = "greeting"
            description = "Prints a hello message from plugin"
            doLast {
                println("Hello from HelloPlugin!")
            }
        }
    }
}

// 依赖管理插件（带配置扩展）
class DependencyManagementPlugin : Plugin {
    override fun apply(project: Project) {
        val config = DependencyConfig().apply {
            libVersion = "1.0.0"
            testVersion = "4.13.2"
        }
        project.extensions("dependencyConfig", config)

        // 注册检查任务
        project.tasks.register("checkDependencies") {
            group = "verification"
            doLast {
                val cfg = project.getExtension<DependencyConfig>("dependencyConfig")
                println("Checking dependencies with config:")
                println(" - Library version: ${cfg.libVersion}")
                println(" - Test version: ${cfg.testVersion}")
            }
        }
    }
}

// 插件配置扩展类
class DependencyConfig {
    var libVersion: String = ""
    var testVersion: String = ""
}