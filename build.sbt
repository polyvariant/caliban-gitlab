inThisBuild(
  List(
    organization := "com.kubukoz",
    homepage := Some(url("https://github.com/pitgull/caliban-gitlab")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "kubukoz",
        "Jakub Kozłowski",
        "kubukoz@gmail.com",
        url("https://blog.kubukoz.com")
      )
    ),
    sources in (Compile, doc) := Seq()
  )
)

val Scala212 = "2.12.12"
val Scala213 = "2.13.3"

def crossPlugin(x: sbt.librarymanagement.ModuleID) = compilerPlugin(x.cross(CrossVersion.full))

val compilerPlugins = List(
  crossPlugin("org.typelevel" % "kind-projector" % "0.11.0"),
  crossPlugin("com.github.cb372" % "scala-typed-holes" % "0.1.5"),
  crossPlugin("com.kubukoz" % "better-tostring" % "0.2.4"),
  compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
)

val GraalVM11 = "graalvm11@20.1.0"

ThisBuild / scalaVersion := Scala213
// ThisBuild / crossScalaVersions := Seq(Scala212, Scala213)
ThisBuild / crossScalaVersions := Seq(Scala213)
ThisBuild / githubWorkflowJavaVersions := Seq(GraalVM11)
//sbt-ci-release settings
ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches := Seq(RefPredicate.StartsWith(Ref.Tag("v")))
ThisBuild / githubWorkflowPublishPreamble := Seq(WorkflowStep.Use("olafurpg", "setup-gpg", "v2"))
ThisBuild / githubWorkflowPublish := Seq(WorkflowStep.Sbt(List("ci-release")))
ThisBuild / githubWorkflowEnv ++= List("PGP_PASSPHRASE", "PGP_SECRET", "SONATYPE_PASSWORD", "SONATYPE_USERNAME").map { envKey =>
  envKey -> s"$${{ secrets.$envKey }}"
}.toMap

val core = project
  .settings(
    name := "caliban-gitlab",
    libraryDependencies ++= List("com.github.ghostdogpr" %% "caliban-client" % "0.9.0")
  )

val root = project
  .in(file("."))
  .settings(
    addCommandAlias(
      "codegen",
      "calibanGenClient https://gitlab.com/api/graphql core/src/main/scala/io/pg/gitlab/graphql.scala --packageName io.pg.gitlab"
    ),
    skip in publish := true
  )
  .aggregate(core)
  .enablePlugins(CodegenPlugin)
