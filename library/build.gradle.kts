plugins {
  id("roundalib") version "0.2.7"
}

dependencies {
  shadow("com.electronwill.night-config:core:3.6.5")
  shadow("com.electronwill.night-config:toml:3.6.5")
}

tasks.shadowJar {
  dependsOn(tasks.jar)
  enabled = true

  configurations = listOf(project.configurations.shadow.get())

  manifest {
    attributes["Fabric-Loom-Remap"] = "true"
  }

  relocate("com.electronwill.nightconfig", "me.roundaround.roundalib.shadow.nightconfig")
}

tasks.remapJar {
  dependsOn(tasks.shadowJar)
  inputFile.set(tasks.shadowJar.get().archiveFile)

  manifest {
    attributes["Fabric-Loom-Remap"] = "true"
  }
}

tasks.remapSourcesJar {
  manifest {
    attributes["Fabric-Loom-Remap"] = "true"
  }
}
