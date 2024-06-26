pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenLocal()
    maven("https://maven.fabricmc.net/")
//    maven("https://maven.rnda.dev/releases/")
    includeBuild("../mc-roundalib-gradle")
  }
}

include("library")
include("testmod")
