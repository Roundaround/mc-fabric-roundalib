plugins {
  id("fabric-loom") version "1.1-SNAPSHOT"
  id("distribution")
  id("maven-publish")
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

val targetJavaVersion = 17
val fullVersion = project.property("mod_version").toString() + "+" + project.property("minecraft_version").toString()

configurations.configureEach {
  version = project.property("mod_version").toString()
  group = project.property("maven_group").toString()
}

dependencies {
  minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
  mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
  modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")

  shadow("com.electronwill.night-config:core:3.6.5")
  shadow("com.electronwill.night-config:toml:3.6.5")
}

java {
  sourceCompatibility = JavaVersion.toVersion(targetJavaVersion)
  targetCompatibility = JavaVersion.toVersion(targetJavaVersion)

  withSourcesJar()
}

distributions {
  main {
    contents {
      from(tasks.processResources.get()) {
        into("")
      }
    }
  }
}

tasks.withType<JavaCompile> {
  options.encoding = "UTF-8"
  options.release.set(targetJavaVersion)
}

tasks.jar {
  from("LICENSE") {
    rename { "${it}_${project.property("archive_base_name")}" }
  }
}

tasks.shadowJar {
  configurations = listOf(project.configurations.shadow.get())

  archiveBaseName.set(project.property("archive_base_name").toString())
  archiveVersion.set(fullVersion)
  archiveClassifier.set("shaded")

  manifest {
    attributes["Fabric-Loom-Remap"] = "true"
  }

  relocate("com.electronwill.nightconfig", "me.roundaround.roundalib.shadow.nightconfig")
}

tasks.remapJar {
  dependsOn(tasks.shadowJar)
  inputFile.set(tasks.shadowJar.get().archiveFile)

  archiveBaseName.set(project.property("archive_base_name").toString())
  archiveVersion.set(fullVersion)
  archiveClassifier.set("")

  manifest {
    attributes["Fabric-Loom-Remap"] = "true"
  }
}

tasks.remapSourcesJar {
  archiveBaseName.set(project.property("archive_base_name").toString())
  archiveVersion.set(fullVersion)
  archiveClassifier.set("sources")

  manifest {
    attributes["Fabric-Loom-Remap"] = "true"
  }
}

tasks.distZip {
  eachFile {
    relativePath = RelativePath(true, *relativePath.segments.drop(3).toTypedArray())
  }
  includeEmptyDirs = false

  archiveBaseName.set(project.property("archive_base_name").toString())
  archiveVersion.set(fullVersion)
  archiveClassifier.set("resources")
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])
      artifact(tasks.distZip.get())

      artifactId = tasks.remapJar.get().archiveBaseName.get()
      version = tasks.remapJar.get().archiveVersion.get()

      repositories {
        maven {
          url = uri(property("selfHostedMavenUrl").toString() + "/releases")
          credentials(PasswordCredentials::class) {
            username = property("selfHostedMavenUser").toString()
            password = property("selfHostedMavenPass").toString()
          }
        }
      }
    }
  }
}
