plugins {
    id("dev.architectury.loom") version "1.17.487"
    id("me.modmuss50.mod-publish-plugin")
}

evaluationDependsOn(":common")

version = "${mod.version}+${mod.prop("mc_title")}"
group = mod.group

base {
    archivesName.set("${mod.name}-neoforge")
}

sourceSets {
    main {
        val commonSourceSets = project(":common").extensions.getByType<SourceSetContainer>()
        val commonMain = commonSourceSets.named("main").get()
        java.srcDirs(commonMain.java.srcDirs)
        resources.srcDirs(commonMain.resources.srcDirs)
    }
}

repositories {
    maven("https://maven.neoforged.net/releases/")
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
}

dependencies {
    minecraft("com.mojang:minecraft:${mod.dep("minecraft.neoforge")}")
    mappings(loom.officialMojangMappings())
    neoForge("net.neoforged:neoforge:${mod.dep("neoforge_loader")}")
    compileOnly(project(":common")) {
        isTransitive = false
    }
    implementation("software.bernie.geckolib:geckolib-neoforge-1.21.1:${mod.dep("geckolib_version")}")
}

loom {
    runs {
        named("client") {
            runDir = "../../run/neoforge/client"
        }
        named("server") {
            runDir = "../../run/neoforge/server"
        }
    }
}

val requiredJava = JavaVersion.toVersion(mod.prop("java_version"))
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
        "java" to mod.prop("java_version")
    )
}

tasks.remapJar {
    archiveClassifier.set("")
}

tasks.jar {
    archiveClassifier.set("dev")
}

configurePublishing()