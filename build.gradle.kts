plugins {
  id("org.jetbrains.kotlin.jvm").version("2.0.0-RC1")
  id("com.apollographql.apollo3").version("4.0.0-beta.6")
}

dependencies {
  implementation("com.apollographql.apollo3:apollo-runtime")
  implementation("com.apollographql.apollo3:apollo-normalized-cache")
  testImplementation(kotlin("test"))
  testImplementation("app.cash.turbine:turbine:1.1.0")
}

apollo {
  service("service") {
    packageName.set("com.example")
  }
}
