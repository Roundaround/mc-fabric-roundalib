plugins {
  id("roundalib-gradle") version "2.0.0"
}

roundalib {
  configureForLibrary()
}

dependencies {
  implementation(project(":core"))
  implementation(project(":observables"))
}