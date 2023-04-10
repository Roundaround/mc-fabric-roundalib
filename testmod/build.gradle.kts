plugins {
  id("roundalib") version "0.1.3"
}

dependencies {
  implementation(project(path = ":library", configuration = "namedElements"))

  implementation("com.electronwill.night-config:core:3.6.5")
  implementation("com.electronwill.night-config:toml:3.6.5")
}
