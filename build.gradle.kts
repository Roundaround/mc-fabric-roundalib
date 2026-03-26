subprojects {
  val propsFile = projectDir.resolve("gradle.properties")
  if (propsFile.exists()) {
    val props = java.util.Properties()
    propsFile.inputStream().use { props.load(it) }
    props.stringPropertyNames().forEach { key ->
      extra.set(key, props.getProperty(key))
    }
  }
}
