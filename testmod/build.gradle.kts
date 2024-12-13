plugins {
  id("fabric-loom") version "1.9-SNAPSHOT"
  id("com.gradleup.shadow") version "9.0.0-beta2"
  id("roundalib") version "0.9.0-SNAPSHOT"
}

val testGroupId = project.properties["group_id"].toString()
val testModId = project.properties["mod_id"].toString()

val roundaLibConfig: Configuration = configurations.create("roundaLibConfig")

dependencies {
  implementation(project(path = ":library", configuration = "namedElements"))
  roundaLibConfig(project(path = ":library", configuration = "namedElements"))

  implementation("com.electronwill.night-config:core:3.7.4")
  implementation("com.electronwill.night-config:toml:3.7.4")
}

val importLangFiles = tasks.register<me.roundaround.roundalib.gradle.tasks.ImportLangFilesTask>("importLangFiles") {
  modId.set(testModId)
  roundaLibSource.from(roundaLibConfig)
}

val importMixins = tasks.register<me.roundaround.roundalib.gradle.tasks.ImportMixinsTask>("importMixins") {
  modId.set(testModId)
  roundaLibSource.from(roundaLibConfig)
  roundaLibPackage.set("$testGroupId.$testModId.roundalib")
}

val importTextures = tasks.register<me.roundaround.roundalib.gradle.tasks.ImportTexturesTask>("importTextures") {
  modId.set(testModId)
  roundaLibSource.from(roundaLibConfig)
}

val registerEntrypoint =
  tasks.register<me.roundaround.roundalib.gradle.tasks.RegisterEntrypointTask>("registerEntrypoint") {
    modId.set(testModId)
    roundaLibPackage.set("$testGroupId.$testModId.roundalib")
    mustRunAfter(importMixins)
  }

tasks.processResources {
  dependsOn(importLangFiles, importMixins, importTextures, registerEntrypoint)
  from(
    importLangFiles.get().outputDir,
    importMixins.get().outputDir,
    importTextures.get().outputDir,
    registerEntrypoint.get().outputDir
  )
}
