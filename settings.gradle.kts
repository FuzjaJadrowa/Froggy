pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.neoforged.net/releases/")
    }
}

rootProject.name = "Froggy"

include("common")
project(":common").projectDir = file("src/common")

include("fabric")
project(":fabric").projectDir = file("src/fabric")

include("neoforge")
project(":neoforge").projectDir = file("src/neoforge")