plugins {
  id("roundalib") version "0.1.2"
}

dependencies {
  // Include the libray itself so its accessible from the testmod
  modImplementation(project(mapOf("path" to ":")))
}
