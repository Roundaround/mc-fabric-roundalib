pluginManagement {
  repositories {
    gradlePluginPortal()
    maven {
      name = "Fabric"
      url = uri("https://maven.fabricmc.net/")
    }
    maven {
      name = "Roundaround"
      url = uri("https://maven.rnda.dev/snapshots/")
      metadataSources {
        mavenPom()
        artifact()
      }
    }
  }
}

include("library")
include("testmod")
