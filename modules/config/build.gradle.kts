plugins {
  id("roundalib-gradle") version "1.0.0"
}

roundalib {
  configureForLibrary()
  constants.enabled = false
}

dependencies {
  implementation(project(":core"))
}
