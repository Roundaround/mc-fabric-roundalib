plugins {
  id("roundalib-gradle") version "1.1.0"
}

roundalib {
  configureForLibrary()
}

dependencies {
  implementation(project(":core"))
  implementation(project(":observables"))
}
