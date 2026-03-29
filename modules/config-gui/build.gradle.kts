plugins {
  id("roundalib-gradle") version "2.0.0"
}

roundalib {
  configureForLibrary()
}

dependencies {
  implementation(project(":config"))
  implementation(project(":gui"))
  implementation(project(":observables"))
}
