import net.fabricmc.loom.task.RemapJarTask

plugins {
  id("roundalib") version "0.2.2"
}

val testModId = project.properties.get("mod_id").toString()

val roundaLibConfig = configurations.create("roundaLibConfig")

dependencies {
  implementation(project(path = ":library", configuration = "namedElements"))
  roundaLibConfig(project(path = ":library", configuration = "namedElements"))

  implementation("com.electronwill.night-config:core:3.6.5")
  implementation("com.electronwill.night-config:toml:3.6.5")
}

tasks.mergeAssets {
  modId.set(testModId)
  roundaLibConfiguration.set(roundaLibConfig)

  from(project.sourceSets.main.get().resources.asFileTree)
  into(project.buildDir.resolve("roundalib"))
}

tasks.importMixins {
  roundaLibPackage.set("me.roundaround.roundalib.mixin")
  roundaLibConfiguration.set(roundaLibConfig)

  from(project.sourceSets.main.get().resources.asFileTree)
  into(project.buildDir.resolve("roundalib"))
}

tasks.processResources {
  dependsOn(tasks.mergeAssets, tasks.importMixins)
  from(tasks.mergeAssets.get().destinationDir, tasks.importMixins.get().destinationDir)
}
