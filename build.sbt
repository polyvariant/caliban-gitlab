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
      ),
      Developer(
        "majk-p",
        "Michał Pawlik",
        "admin@michalp.net",
        url("https://michalp.net")
      )
    ),
    Compile / doc / sources := Seq()
  )
)

val Scala213 = "2.13.6"
val Scala3 = "3.0.1"

def crossPlugin(x: sbt.librarymanagement.ModuleID) = compilerPlugin(x.cross(CrossVersion.full))

val compilerPlugins = List(
  crossPlugin("org.typelevel" % "kind-projector" % "0.11.0"),
  crossPlugin("com.github.cb372" % "scala-typed-holes" % "0.1.5"),
  crossPlugin("com.kubukoz" % "better-tostring" % "0.2.4"),
  compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
)

val GraalVM11 = "graalvm-ce-java11@20.3.0"

ThisBuild / scalaVersion := Scala213
ThisBuild / crossScalaVersions := Seq(Scala213, Scala3)
ThisBuild / githubWorkflowJavaVersions := Seq(GraalVM11)
//sbt-ci-release settings
ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches := Seq(RefPredicate.StartsWith(Ref.Branch("master")), RefPredicate.StartsWith(Ref.Tag("v")))
ThisBuild / githubWorkflowPublishPreamble := Seq(WorkflowStep.Use(UseRef.Public("olafurpg", "setup-gpg", "v3")))
ThisBuild / githubWorkflowPublish := Seq(WorkflowStep.Sbt(List("ci-release")))
ThisBuild / githubWorkflowEnv ++= List("PGP_PASSPHRASE", "PGP_SECRET", "SONATYPE_PASSWORD", "SONATYPE_USERNAME").map { envKey =>
  envKey -> s"$${{ secrets.$envKey }}"
}.toMap

val core = project
  .settings(
    name := "caliban-gitlab",
    libraryDependencies ++= List("com.github.ghostdogpr" %% "caliban-client" % "1.1.0")
  )

val root = project
  .in(file("."))
  .settings(
    addCommandAlias(
      "codegen",
      "calibanGenClient https://gitlab.com/api/graphql core/src/main/scala/io/pg/gitlab/graphql.scala --packageName io.pg.gitlab"
    ),
    addCommandAlias(
      "release",
      "+publishSigned;sonatypeBundleRelease"
    ),
    publish / skip := true
  )
  .aggregate(core)
  .enablePlugins(CalibanPlugin)
