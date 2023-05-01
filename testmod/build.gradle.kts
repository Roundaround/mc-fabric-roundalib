plugins {
  id("roundalib") version "0.2.26"
}

val testModId = project.properties["mod_id"].toString()

val roundaLibConfig: Configuration = configurations.create("roundaLibConfig")

dependencies {
  implementation(project(path = ":library", configuration = "namedElements"))
  roundaLibConfig(project(path = ":library", configuration = "namedElements"))

  implementation("com.electronwill.night-config:core:3.6.5")
  implementation("com.electronwill.night-config:toml:3.6.5")
}

tasks.mergeAssets {
  dependsOn(tasks.compileJava)

  modId.set(testModId)
  roundaLibConfiguration.set(project.files(roundaLibConfig))

  from(tasks.processResources.get().destinationDir)
  into(project.buildDir.resolve("roundalib"))
}

tasks.importMixins {
  dependsOn(tasks.compileJava)

  roundaLibPackage.set("me.roundaround.testmod.roundalib.mixin")
  roundaLibConfiguration.set(project.files(roundaLibConfig))

  from(tasks.processResources.get().destinationDir)
  into(project.buildDir.resolve("roundalib"))
}

tasks.processResources {
  dependsOn(tasks.mergeAssets)
  from(tasks.mergeAssets.get().destinationDir)
}
