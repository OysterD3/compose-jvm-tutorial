import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion = "1.6.2"

plugins {
  kotlin("jvm")
  kotlin("plugin.serialization") version "1.5.21"
}

tasks.withType<KotlinCompile>() {
  kotlinOptions.jvmTarget = "11"
}

group = rootProject.group
version = rootProject.version

repositories {
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib"))

  // Ktor
  implementation("io.ktor:ktor-client-core:$ktorVersion")
  implementation("io.ktor:ktor-client-cio:$ktorVersion")
  implementation("io.ktor:ktor-client-logging:$ktorVersion")
  implementation("io.ktor:ktor-client-serialization:$ktorVersion")

  // Serialization
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")

  // Datetime
  implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")
}
