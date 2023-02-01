lazy val scalaVersions = Seq("2.13.10", "2.12.17")
lazy val scalaVersionsJvm = Seq("2.11.12", "2.10.7")

lazy val commonSettings: SettingsDefinition = Def.settings(
  organization := "de.bitmarck.bms",
  version := {
    val Tag = "refs/tags/(.*)".r
    sys.env.get("CI_VERSION").collect { case Tag(tag) => tag }
      .getOrElse("0.0.1-SNAPSHOT")
  },

  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0")),

  homepage := Some(url("https://base32check.org")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/bitmarck-service/base32check-scala"),
      "scm:git@github.com:bitmarck-service/base32check-scala.git"
    )
  ),
  developers := List(
    Developer(id = "u016595", name = "Pierre Kisters", email = "pierre.kisters@bitmarck.de", url = url("https://github.com/LolHens/"))
  ),

  libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % "1.4.5" % Test,
    "org.scalatestplus" %%% "scalacheck-1-14" % "3.2.2.0" % Test,
    "org.scalatest" %%% "scalatest" % "3.2.15" % Test,
  ),

  Compile / doc / sources := Seq.empty,

  publishMavenStyle := true,

  publishTo := sonatypePublishToBundle.value,

  credentials ++= (for {
    username <- sys.env.get("SONATYPE_USERNAME")
    password <- sys.env.get("SONATYPE_PASSWORD")
  } yield Credentials(
    "Sonatype Nexus Repository Manager",
    "oss.sonatype.org",
    username,
    password
  )).toList
)

name := (core.projectRefs.head / name).value
ThisBuild / scalaVersion := scalaVersions.head
ThisBuild / versionScheme := Some("early-semver")

lazy val root: Project = project.in(file("."))
  .settings(commonSettings)
  .settings(
    publishArtifact := false,
    publish / skip := true
  )
  .aggregate(core.projectRefs: _*)

lazy val core = projectMatrix.in(file("core"))
  .settings(commonSettings)
  .settings(
    name := "base32check-scala",
  )
  .jvmPlatform(scalaVersions ++ scalaVersionsJvm)
  .jsPlatform(scalaVersions)
