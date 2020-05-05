organization := "de.bitmarck.bms"
name := "base32check-scala"
version := "0.0.1-SNAPSHOT"

scalaVersion := "2.13.2"
crossScalaVersions := Seq("2.11.12", "2.12.11", "2.13.2")

homepage := Some(url("https://base32check.org"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/bitmarck-service/base32check-scala"),
    "scm:git@github.com:bitmarck-service/base32check-scala.git"
  )
)

sonatypeProfileName := "de.bitmarck.bms"

libraryDependencies += "org.scalatestplus" %% "scalacheck-1-14" % "3.1.1.1" % Test


Compile / doc / sources := Seq.empty

version := {
  val tagPrefix = "refs/tags/"
  sys.env.get("CI_VERSION").filter(_.startsWith(tagPrefix)).map(_.drop(tagPrefix.length)).getOrElse(version.value)
}

publishMavenStyle := true

publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

credentials ++= (for {
  username <- sys.env.get("SONATYPE_USERNAME")
  password <- sys.env.get("SONATYPE_PASSWORD")
} yield Credentials(
  "Sonatype Nexus Repository Manager",
  "oss.sonatype.org",
  username,
  password
)).toList
