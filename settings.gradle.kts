pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
//    maven("https://maven.rnda.dev/snapshots/")
    mavenLocal()
  }
}

include("core", "gui", "config", "config-gui", "network", "observables")

project(":core").projectDir = file("modules/core")
project(":gui").projectDir = file("modules/gui")
project(":config").projectDir = file("modules/config")
project(":config-gui").projectDir = file("modules/config-gui")
project(":network").projectDir = file("modules/network")
project(":observables").projectDir = file("modules/observables")