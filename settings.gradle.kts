pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.kikugie.dev/snapshots")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9"
}

stonecutter {
    kotlinController = true
    shared {
        fun mc(loader: String, vararg versions: String) {
            for (version in versions) {
                val targetDir = file("versions/$version-$loader")
                if (!targetDir.exists()) {
                    targetDir.mkdirs()
                }

                val sourceProps = file("gradle/targets/$version.properties")
                val targetProps = file("versions/$version-$loader/gradle.properties")
                if (sourceProps.exists()) {
                    sourceProps.copyTo(targetProps, overwrite = true)
                }

                val buildscript = when (loader) {
                    "neoforge" -> "build-neoforge.gradle.kts"
                    "forge" -> "build-forge.gradle.kts"
                    else -> error("Unsupported loader: $loader")
                }
                
                version("$version-$loader", version).buildscript(buildscript)
            }
        }

        mc("neoforge", "1.21.1")
        mc("forge", "1.20.1")
    }
    create(rootProject)
}

rootProject.name = "Froggy"