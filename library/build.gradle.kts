plugins {
  id("roundalib") version "0.5.3"
}

val nightConfig: Configuration by configurations.creating {
  isTransitive = false
  isCanBeResolved = true
  isCanBeConsumed = false
}

dependencies {
  implementation("com.electronwill.night-config:core:3.7.2")
  nightConfig("com.electronwill.night-config:core:3.7.2")
  implementation("com.electronwill.night-config:toml:3.7.2")
  nightConfig("com.electronwill.night-config:toml:3.7.2")
}

tasks.jar {
  manifest {
    attributes["Fabric-Loom-Remap"] = "true"
  }
}

tasks.shadowJar {
  dependsOn(tasks.jar)
  enabled = true

  configurations = listOf(nightConfig)

  relocate("com.electronwill.nightconfig", "me.roundaround.roundalib.shadow.nightconfig")
}

tasks.remapJar {
  dependsOn(tasks.shadowJar)
  inputFile.set(tasks.shadowJar.get().archiveFile)
}
