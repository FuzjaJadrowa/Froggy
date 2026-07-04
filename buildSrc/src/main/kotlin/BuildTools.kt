import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.maven
import org.gradle.language.jvm.tasks.ProcessResources
import java.io.File

val Project.mod: ModData get() = ModData(this)

fun Project.prop(key: String): String? = findProperty(key)?.toString()

fun String.upperCaseFirst() = replaceFirstChar { if (it.isLowerCase()) it.uppercaseChar() else it }

fun RepositoryHandler.strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
    forRepository { maven(url) { name = alias } }
    filter { groups.forEach(::includeGroup) }
}

fun ProcessResources.properties(files: Iterable<String>, vararg properties: Pair<String, Any>) {
    for ((name, value) in properties) inputs.property(name, value)
    filesMatching(files) {
        expand(properties.toMap())
    }
}

fun Project.versionedJavaSources(vararg roots: File) {
    val generatedSources = layout.buildDirectory.dir("generated/preprocessed/main")
    
    val prepareSources = tasks.register("prepareVersionedJavaSources") {
        inputs.files(roots)
        outputs.dir(generatedSources)
        dependsOn(tasks.matching { it.name == "stonecutterGenerate" })

        doLast {
            val outputRoot = generatedSources.get().asFile
            outputRoot.deleteRecursively()

            for (root in roots) {
                if (!root.exists()) {
                    continue
                }

                root.walkTopDown()
                    .filter { it.isFile && it.extension == "java" }
                    .forEach { file ->
                        val relative = root.toPath().relativize(file.toPath())
                        val output = outputRoot.toPath().resolve(relative).toFile()
                        output.parentFile.mkdirs()
                        val version = project.name.substringBeforeLast('-')
                        output.writeText(Preprocessor.transform(file.readLines(), version))
                    }
            }
        }
    }

    val active = isStonecutterProjectActive()
    extensions.getByType<SourceSetContainer>().named("main") {
        if (System.getProperty("idea.sync.active") == "true" && active) {
            java.setSrcDirs(roots.toList())
        } else {
            java.setSrcDirs(listOf(generatedSources))
        }
    }
    
    tasks.named("compileJava") {
        dependsOn(prepareSources)
        dependsOn(tasks.matching { it.name == "stonecutterGenerate" })
    }
}

@JvmInline
value class ModData(private val project: Project) {
    val id: String get() = requireNotNull(project.prop("mod.id")) { "Missing 'mod.id' in gradle.properties" }
    val name: String get() = requireNotNull(project.prop("mod.name")) { "Missing 'mod.name' in gradle.properties" }
    val version: String get() = requireNotNull(project.prop("mod.version")) { "Missing 'mod.version' in gradle.properties" }
    val group: String get() = requireNotNull(project.prop("mod.group")) { "Missing 'mod.group' in gradle.properties" }

    fun prop(key: String) = requireNotNull(project.prop("mod.$key")) { "Missing 'mod.$key' in gradle.properties" }
    fun dep(key: String) = requireNotNull(project.prop("dep.$key")) { "Missing 'dep.$key' in gradle.properties" }
}

private fun Project.isStonecutterProjectActive(): Boolean {
    val stonecutter = extensions.findByName("stonecutter") ?: return false
    return try {
        val current = stonecutter.javaClass.getMethod("getCurrent").invoke(stonecutter)
        current.javaClass.getMethod("isActive").invoke(current) as? Boolean ?: false
    } catch (e: Exception) {
        false
    }
}