import groovy.json.JsonOutput
import groovy.json.JsonSlurper

plugins {
    id("net.neoforged.moddev") version "2.0.141"
    id("me.modmuss50.mod-publish-plugin")
}

val minecraftTitle = mod.prop("mc_title")
val loader = stonecutter.current.project.substringAfterLast('-')
val javaVersion = mod.prop("java_version")

version = "${mod.version}+$minecraftTitle"
group = mod.group
base {
    archivesName.set("${mod.name}-$loader")
}

sourceSets {
    main {
        resources.srcDir(rootProject.file("src/common/src/main/resources"))
        resources.srcDir(rootProject.file("src/neoforge/src/main/resources"))
    }
}

versionedJavaSources(
    rootProject.file("src/common/src/main/java"),
    rootProject.file("src/neoforge/src/main/java")
)

repositories {
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.terraformersmc.com/")
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.blamejared.com/")
}

val projectName = project.name

dependencies {
    implementation("software.bernie.geckolib:geckolib-neoforge-1.21.1:${mod.dep("geckolib_version")}")
    compileOnly("mezz.jei:jei-1.21.1-neoforge-api:19.18.3.204")
    runtimeOnly("mezz.jei:jei-1.21.1-neoforge:19.18.3.204")
}

neoForge {
    version = mod.dep("neoforge_loader")

    mods {
        register(mod.id) {
            sourceSet(sourceSets.main.get())
        }
    }

    runs {
        register("client") {
            gameDirectory = rootProject.file("run/$projectName/client")
            client()
        }
        register("server") {
            gameDirectory = rootProject.file("run/$projectName/server")
            server()
        }
    }
}

val requiredJava = JavaVersion.toVersion(javaVersion)

java {
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava
}

tasks.jar {
    inputs.property("archivesName", base.archivesName)
}

val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.jar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}/$loader"))
    dependsOn("build")
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"
        description = "Builds and collects active subproject artifacts."
        dependsOn(buildAndCollect)
    }

    rootProject.tasks.register("testClient") {
        group = "project"
        description = "Launches the client for testing the active NeoForge version."
        dependsOn(tasks.named("runClient"))
    }

    rootProject.tasks.register("testServer") {
        group = "project"
        description = "Launches the server for testing the active NeoForge version."
        dependsOn(tasks.named("runServer"))
    }
}

tasks.processResources {
    properties(
        listOf("META-INF/neoforge.mods.toml"),
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "minecraft" to mod.prop("mc_targets"),
        "loader" to mod.dep("neoforge_loader_range"),
        "neoforge" to mod.dep("neoforge_version_range")
    )
    properties(
        listOf("*.mixins.json"),
        "java" to javaVersion
    )

    doLast {
        fileTree(outputs.files.singleFile).matching {
            include("**/*.json")
        }.forEach { file ->
            file.writeText(JsonOutput.toJson(JsonSlurper().parse(file)))
        }
    }
}

tasks.build {
    group = "versioned"
    description = "Stonecutter target build task."
}

stonecutter {
    constants {
        put("neoforge", true)
        put("forge", false)
    }
}

configureModPublishing()