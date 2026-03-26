plugins {
  id("java")
  id("maven-publish")
  id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT"
}

version = providers.gradleProperty("mod_version").get()
group = providers.gradleProperty("group_id").get()

base {
  archivesName = extra["archives_base_name"].toString()
}

repositories {
  mavenLocal()
}

dependencies {
  minecraft("com.mojang:minecraft:${providers.gradleProperty("minecraft_version").get()}")
  implementation("net.fabricmc:fabric-loader:${providers.gradleProperty("loader_version").get()}")

  api("me.roundaround:roundalib-gradle:1.1.0:api")
}

tasks.withType<JavaCompile>().configureEach {
  options.release = 25
}

java {
  withSourcesJar()

  sourceCompatibility = JavaVersion.VERSION_25
  targetCompatibility = JavaVersion.VERSION_25
}

tasks.jar {
  inputs.property("archivesName", base.archivesName)

  from("LICENSE") {
    rename { "${it}_${base.archivesName.get()}" }
  }
}

publishing {
  publications {
    register<MavenPublication>("mavenJava") {
      artifactId = base.archivesName.get()
      from(components["java"])
    }
  }

  repositories {
    maven {
      name = "RoundaroundMaven"
      url = uri("https://maven.rnda.dev/releases/")
      credentials(PasswordCredentials::class) {
        username = property("selfHostedMavenUser").toString()
        password = property("selfHostedMavenPass").toString()
      }
    }
  }
}
