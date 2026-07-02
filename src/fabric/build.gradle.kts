plugins {
    id("dev.architectury.loom") version "1.17.487"
    id("me.modmuss50.mod-publish-plugin")
}

evaluationDependsOn(":common")

version = "${mod.version}+${mod.prop("mc_title")}"
group = mod.group

base {
    archivesName.set("${mod.name}-fabric")
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
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.terraformersmc.com/releases/")
}

dependencies {
    minecraft("com.mojang:minecraft:${mod.dep("minecraft.fabric")}")
    mappings(loom.officialMojangMappings())
    compileOnly(project(":common"))

    modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${mod.dep("fabric_api_version")}")
    modImplementation("software.bernie.geckolib:geckolib-fabric-1.21.1:${mod.dep("geckolib_version")}")
    
    modCompileOnly("com.terraformersmc:modmenu:${mod.dep("modmenu_version")}")
    modLocalRuntime("com.terraformersmc:modmenu:${mod.dep("modmenu_version")}")
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

loom {
    runs {
        named("client") {
            runDir = "../../run/fabric/client"
        }
        named("server") {
            runDir = "../../run/fabric/server"
        }
    }
}

tasks.processResources {
    properties(
        listOf("fabric.mod.json"),
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "minecraft" to mod.prop("mc_targets"),
        "java" to mod.prop("java_version"),
        "fabric_loader" to mod.dep("fabric_loader")
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