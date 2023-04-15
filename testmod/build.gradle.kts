plugins {
  id("roundalib") version "0.1.3"
}

val testModId = project.properties.get("mod_id").toString()

val resourceConfig = configurations.create("roundaLibResources")

dependencies {
  implementation(project(path = ":library", configuration = "namedElements"))
  resourceConfig(project(path = ":library", configuration = "namedElements"))

  implementation("com.electronwill.night-config:core:3.6.5")
  implementation("com.electronwill.night-config:toml:3.6.5")
}

tasks.prepareResources {
  modId.set(testModId)
  roundaLibConfiguration.set(resourceConfig)

  from(project.sourceSets.main.get().resources.asFileTree)
  into(project.buildDir.resolve("roundalib"))
}

tasks.processResources {
  dependsOn(tasks.prepareResources)
  from(tasks.prepareResources.get().destinationDir)
}
