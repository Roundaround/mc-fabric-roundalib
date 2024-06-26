plugins {
    id("io.github.goooler.shadow") version "8.1.7"
    id("roundalib")
}

val testGroupId = project.properties["group_id"].toString()
val testModId = project.properties["mod_id"].toString()

val roundaLibConfig: Configuration = configurations.create("roundaLibConfig")

dependencies {
    implementation(project(path = ":library", configuration = "namedElements"))
    roundaLibConfig(project(path = ":library", configuration = "namedElements"))

    implementation("com.electronwill.night-config:core:3.6.5")
    implementation("com.electronwill.night-config:toml:3.6.5")
}

val importLangFiles = tasks.register<me.roundaround.roundalib.gradle.tasks.ImportLangFilesTask>("importLangFiles") {
    modId.set(testModId)
    roundaLibSource.from(roundaLibConfig)
}

val importMixins = tasks.register<me.roundaround.roundalib.gradle.tasks.ImportMixinsTask>("importMixins") {
    modId.set(testModId)
    roundaLibSource.from(roundaLibConfig)
    roundaLibPackage.set("$testGroupId.$testModId.roundalib.mixin")
}

val importTextures = tasks.register<me.roundaround.roundalib.gradle.tasks.ImportTexturesTask>("importTextures") {
    modId.set(testModId)
    roundaLibSource.from(roundaLibConfig)
}

tasks.processResources {
    dependsOn(importLangFiles, importMixins, importTextures)
    from(importLangFiles.get().outputDir, importMixins.get().outputDir, importTextures.get().outputDir)
}
