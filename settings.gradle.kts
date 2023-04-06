pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenLocal()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.rnda.dev/releases/")
  }
}

include("library")
include("testmod")
