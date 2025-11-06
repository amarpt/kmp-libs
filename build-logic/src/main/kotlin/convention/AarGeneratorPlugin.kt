package convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.bundling.Zip
import java.io.File

class AarGeneratorPlugin : Plugin<Project> {

    companion object {
        private const val TMP_DIR = "tmp"
        private const val EXTENSION_NAME = "aarGenerator"
    }

    override fun apply(project: Project) {
        val extension = project.extensions.create(
            EXTENSION_NAME,
            AarGeneratorExtension::class.java
        )

        project.afterEvaluate {
            registerTasks(project, extension)
        }
    }

    private fun registerTasks(project: Project, ext: AarGeneratorExtension) {
        val outputDir = project.layout.buildDirectory.dir(ext.outputDir).get().asFile
        val outputFile = File(outputDir, ext.outputAarName)

        registerMergeTask(project, ext)
        registerGenerateTask(project, ext, outputDir)
        registerBuildTask(project, ext, outputFile)
        registerCleanTask(project, ext)
    }

    private fun registerMergeTask(project: Project, ext: AarGeneratorExtension) {
        val mergedDir = project.layout.buildDirectory.dir("${ext.outputDir}/$TMP_DIR")
        val sourceFile = project.layout.buildDirectory.file(ext.sourceAarPath).get().asFile

        project.tasks.register("mergeAAR", Copy::class.java) {
            dependsOn("assemble")
            from(project.zipTree(sourceFile))
            into(mergedDir)
            doLast {
                println("AAR contents extracted to: ${mergedDir.get().asFile.absolutePath}")
            }
        }
    }

    private fun registerGenerateTask(project: Project, ext: AarGeneratorExtension, outputDir: File) {
        val mergedDir = project.layout.buildDirectory.dir("${ext.outputDir}/$TMP_DIR")

        project.tasks.register("generateAAR", Zip::class.java) {
            dependsOn("mergeAAR")
            from(mergedDir)
            archiveFileName.set(ext.outputAarName)
            destinationDirectory.set(outputDir)
            doLast {
                println("Generated AAR: ${File(outputDir, ext.outputAarName).absolutePath}")
            }
        }
    }

    private fun registerBuildTask(project: Project, ext: AarGeneratorExtension, outputFile: File) {
        project.tasks.register("buildGenAAR") {
            dependsOn("generateAAR")
            doLast {
                println("AAR build complete: ${outputFile.absolutePath}")
                if (outputFile.exists()) {
                    println("Size: ${outputFile.length() / 1024} KB")
                } else {
                    println("Error: AAR not found.")
                }
            }
        }
    }

    private fun registerCleanTask(project: Project, ext: AarGeneratorExtension) {
        project.tasks.register("cleanGenAAR", Delete::class.java) {
            delete(project.layout.buildDirectory.dir(ext.outputDir))
            doLast {
                println("Cleaned: ${ext.outputDir}")
            }
        }
    }
}