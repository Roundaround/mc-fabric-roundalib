plugins {
  id("roundalib-gradle") version "1.0.0"
}

roundalib {
  configureForLibrary()
}

dependencies {
  implementation(project(":core"))
}
