plugins {
  id("roundalib") version "0.2.65"
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

tasks.mergeLanguageFiles {
  modId.set(testModId)
  roundaLibSource.from(roundaLibConfig)
}

tasks.importMixins {
  modId.set(testModId)
  roundaLibSource.from(roundaLibConfig)
  roundaLibPackage.set("$testGroupId.$testModId.roundalib.mixin")
}

tasks.copyTextures {
  modId.set(testModId)
  roundaLibSource.from(roundaLibConfig)
}

tasks.processResources {
  dependsOn(tasks.mergeLanguageFiles, tasks.importMixins, tasks.copyTextures)
  from(tasks.mergeLanguageFiles.get().outputDir, tasks.importMixins.get().outputDir, tasks.copyTextures.get().outputDir)
}
