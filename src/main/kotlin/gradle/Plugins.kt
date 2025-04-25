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

class JavaPlugin : Plugin {
    override fun apply(project: Project) {
        project.tasks.register("compileJava") {
            group = "compile"
            description = "Compiles Java source code"
        }

        project.tasks.register("compileTestJava") {
            group = "compile"
            dependsOn("compileJava")
        }

        project.tasks.register("processResources") {
            dependsOn("compileJava")
        }

        project.tasks.register("classes") {
            dependsOn("compileJava", "processResources")
        }

        project.tasks.register("jar") { dependsOn("classes") }

        project.tasks.register("clean") {
            group = "build"
            description = "Cleans the build directory"
            doLast {
                println("Cleaning the build directory...")
            }
        }
    }
}

@DependsOn(plugins = [JavaPlugin::class])
class ApplicationPlugin : Plugin {
    override fun apply(project: Project) {
        // 创建扩展配置（如主类）
        project.extensions.create("application", ApplicationExtension::class.java) {
            mainClass = "com.example.Main" // 默认主类
        }

        // 注册运行任务
        project.tasks.register<ExecTask>("run") {
            group = "application"
            description = "Runs the application"
            dependsOn("jar") // 依赖 Java 插件的打包任务

            doLast {
                val extension = project.getExtension<ApplicationExtension>("application")
                commandLine("java", "-cp", extension.applicationClassPath, extension.mainClass)
            }
        }
    }
}

// 应用扩展配置类
class ApplicationExtension {
    var mainClass: String = "com.example.Main"
    var applicationName = ""
    var applicationClassPath = "build/classes/kotlin/main"
}

fun  Project.application(config: ApplicationExtension.() -> Unit) {
    getExtension<ApplicationExtension>("application").apply(config)
}
