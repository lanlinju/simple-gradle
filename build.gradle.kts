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
        attributes["Main-Class"] = "com.example.HelloKt"
        attributes["Implementation-Version"] = project.version
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}