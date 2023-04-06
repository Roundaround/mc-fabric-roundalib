plugins {
  id("roundalib") version "0.1.3"
}

dependencies {
  implementation(project(path = ":library", configuration = "namedElements"))
}
