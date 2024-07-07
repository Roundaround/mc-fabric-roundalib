plugins {
  id("io.github.goooler.shadow") version "8.1.7"
  id("roundalib") version "0.7.0-SNAPSHOT"
}

val nightConfig: Configuration by configurations.creating {
  isTransitive = false
  isCanBeResolved = true
  isCanBeConsumed = false
}

dependencies {
  implementation("com.electronwill.night-config:core:3.7.4")
  nightConfig("com.electronwill.night-config:core:3.7.4")
  implementation("com.electronwill.night-config:toml:3.7.4")
  nightConfig("com.electronwill.night-config:toml:3.7.4")
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

  relocate("com.electronwill.nightconfig", "me.roundaround.roundalib.nightconfig")
}

tasks.remapJar {
  dependsOn(tasks.shadowJar)
  inputFile.set(tasks.shadowJar.get().archiveFile)
}
