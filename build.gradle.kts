plugins {
    application
    kotlin("jvm") version "2.1.20"
}

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("com.example.MainKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.example.MainKt"
        attributes["Implementation-Version"] = project.version
    }
}


/* The Windows platform requires the installation of `WiX Toolset` for exe and msi. */
tasks.register<Exec>("jpackage") {
    group = "distribution"
    dependsOn("installDist")
    val type = "app-image" // ✅ 或者 exe（仅 Windows）
    commandLine(
        "jpackage",
        "--name", "MyApp",
        "--input", "build/install/simple-gradle/lib",
        "--main-jar", "simple-gradle-${version}.jar",
        "--main-class", "com.example.HelloDialogKt",
        "--type", type, // 或 "dmg", "pkg", "deb", "rpm", app-image等
        "--dest", "build/dist",
        "--app-version", version,
        "--vendor", "company",
    )
}

// ./gradlew jpackage

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}