val Scala213 = "2.13.3"

val root = project
  .in(file("core"))
  .settings(
    name := "caliban-gitlab",
    scalaVersion := Scala213,
    libraryDependencies ++= List("com.github.ghostdogpr" %% "caliban-client" % "0.9.0")
  )
