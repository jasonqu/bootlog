sbtPlugin := true

lazy val bootlog = (project in file("."))
  .enablePlugins(SbtTwirl)
  .settings(
    name := "bootlog",
    organization := "com.github.bootlog",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.10.4",
    unmanagedResourceDirectories in Compile += baseDirectory.value / "src/main/asset",
    libraryDependencies ++= Seq(
      "joda-time" % "joda-time" % "2.4",
      "org.joda" % "joda-convert" % "1.2",
      "org.pegdown" % "pegdown" % "1.4.2",
      "com.typesafe" % "config" % "1.2.1",
      "org.webjars.bower" % "bootstrap" % "3.3.6",
      "org.webjars.bower" % "Bootflat" % "2.0.4",
      "org.webjars.bower" % "jquery" % "1.11.3"
    )
  )

TwirlKeys.templateImports ++= Seq(
  "com.github.bootlog.models._",
  "com.github.bootlog.util._",
  "com.github.bootlog.util.ConfigUtil._",
  "views.html._",
  "org.joda.time._")

site.settings
