import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.21"
    id("org.jetbrains.compose") version "1.0.0-alpha3"
}

group = "me.oysterlee"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)

    implementation(project(":data"))

    // MVI
    implementation("com.arkivanov.mvikotlin:mvikotlin:2.0.4")
    implementation("com.arkivanov.mvikotlin:mvikotlin-main:2.0.4")
    implementation("com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines:2.0.4")
    implementation("com.arkivanov.mvikotlin:rx:2.0.4")

    // Dependency Injection
    implementation("org.kodein.di:kodein-di:7.6.0")
    implementation("org.kodein.di:kodein-di-framework-compose:7.6.0")

    // Decompose
    implementation("com.arkivanov.decompose:decompose-jvm:0.3.1")
    implementation("com.arkivanov.decompose:extensions-compose-jetbrains-jvm:0.3.1")

    // Kotlinx datetime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "compose-jvm-tutorial"
            packageVersion = "1.0.0"

            val iconsRoot = project.file("src/main/resources/drawables")

            linux {
                iconFile.set(iconsRoot.resolve("launcher_icons/linux.png"))
            }

            windows {
                iconFile.set(iconsRoot.resolve("launcher_icons/windows.ico"))
            }

            macOS {
                iconFile.set(iconsRoot.resolve("launcher_icons/macos.icns"))
            }
        }
    }
}
