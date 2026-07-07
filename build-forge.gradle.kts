plugins {
    id("dev.architectury.loom") version "1.17.487"
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
        resources.srcDir(rootProject.file("src/forge/src/main/resources"))
    }
}

versionedJavaSources(
    rootProject.file("src/common/src/main/java"),
    rootProject.file("src/forge/src/main/java")
)

repositories {
    mavenCentral()
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.blamejared.com/")
}

loom {
    forge {
    }
    runs {
        named("client") {
            runDir(project.projectDir.toPath()
                .relativize(rootProject.file("run/${project.name}/client").toPath())
                .toString())
        }
        named("server") {
            runDir(project.projectDir.toPath()
                .relativize(rootProject.file("run/${project.name}/server").toPath())
                .toString())
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${mod.dep("minecraft")}")
    mappings(loom.officialMojangMappings())
    "forge"("net.minecraftforge:forge:${mod.dep("minecraft")}-${mod.dep("forge")}")
    modImplementation("software.bernie.geckolib:geckolib-forge-1.20.1:${mod.dep("geckolib_version")}")
    implementation("com.eliotlash.mclib:mclib:20")
    "forgeRuntimeLibrary"("com.eliotlash.mclib:mclib:20")
    compileOnly("mezz.jei:jei-1.20.1-forge-api:15.3.0.4")
    modLocalRuntime("mezz.jei:jei-1.20.1-forge:15.3.0.4")
}

val requiredJava = JavaVersion.toVersion(javaVersion)
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(requiredJava.majorVersion))
    }
}

tasks.withType<JavaExec>().configureEach {
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(requiredJava.majorVersion))
    })
}

tasks.remapJar {
    inputs.file(tasks.jar.get().archiveFile)
    archiveClassifier = null
    dependsOn(tasks.jar)
}

tasks.jar {
    archiveClassifier = "dev"
}

val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
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
        description = "Launches the client for testing the active Forge version."
        dependsOn(tasks.named("runClient"))
    }

    rootProject.tasks.register("testServer") {
        group = "project"
        description = "Launches the server for testing the active Forge version."
        dependsOn(tasks.named("runServer"))
    }
}

tasks.processResources {
    inputs.property("mod_id", mod.id)
    inputs.property("mod_name", mod.name)
    inputs.property("mod_version", mod.version)
    inputs.property("loader_version_range", "[47,)")
    inputs.property("forge_version_range", "[47.3.0,)")
    inputs.property("minecraft_version_range", "[1.20.1,1.21)")

    filesMatching("META-INF/mods.toml") {
        expand(
            "mod_id" to mod.id,
            "mod_name" to mod.name,
            "mod_version" to mod.version,
            "loader_version_range" to "[47,)",
            "forge_version_range" to "[47.3.0,)",
            "minecraft_version_range" to "[1.20.1,1.21)"
        )
    }
}

tasks.build {
    group = "versioned"
    description = "Stonecutter target build task."
}

stonecutter {
    constants {
        put("neoforge", false)
        put("forge", true)
    }
}

configureModPublishing()