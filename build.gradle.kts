plugins {
  id("fabric-loom") version "1.1-SNAPSHOT"
  id("maven-publish")
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

val targetJavaVersion = 17
val fullVersion = project.property("mod_version").toString() + "+" + project.property("minecraft_version").toString()

group = project.property("maven_group").toString()
version = project.property("mod_version").toString()

sourceSets {
  create("testmod") {
    compileClasspath += sourceSets.named("main").get().compileClasspath
    runtimeClasspath += sourceSets.named("main").get().runtimeClasspath
  }
}

repositories {
  maven("https://maven.terraformersmc.com/releases/")
}

dependencies {
  minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
  mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
  modApi("net.fabricmc:fabric-loader:${project.property("loader_version")}")

  modCompileOnly("com.terraformersmc:modmenu:${project.property("mod_menu_version")}")
  modRuntimeOnly("com.terraformersmc:modmenu:${project.property("mod_menu_version")}")

  shadow("com.electronwill.night-config:core:3.6.5")
  shadow("com.electronwill.night-config:toml:3.6.5")
}

java {
  sourceCompatibility = JavaVersion.toVersion(targetJavaVersion)
  targetCompatibility = JavaVersion.toVersion(targetJavaVersion)

  withSourcesJar()
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

loom {
  runs {
    create("testmodClient") {
      client()
      source(sourceSets.named("testmod").get())
    }
    create("testmodServer") {
      server()
      source(sourceSets.named("testmod").get())
    }
  }
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])

      artifactId = tasks.remapJar.get().archiveBaseName.get()
      version = tasks.remapJar.get().archiveVersion.get()

      repositories {
        maven {
          url = uri("https://maven.rnda.dev/releases/")
          credentials(PasswordCredentials::class) {
            username = property("selfHostedMavenUser").toString()
            password = property("selfHostedMavenPass").toString()
          }
        }
      }
    }
  }
}
