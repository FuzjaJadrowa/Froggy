plugins {
    id("dev.architectury.loom") version "1.17.487"
}

repositories {
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("dep.minecraft.fabric")}")
    mappings(loom.officialMojangMappings())
    modCompileOnly("net.fabricmc:fabric-loader:${project.property("dep.fabric_loader")}")
    modCompileOnly("software.bernie.geckolib:geckolib-fabric-1.21.1:${project.property("dep.geckolib_version")}")
}

tasks.compileJava {
    enabled = false
}

tasks.jar {
    enabled = false
}

tasks.named("remapJar") {
    enabled = false
}