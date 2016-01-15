lazy val bootlog = (project in file("."))
  .enablePlugins(SbtTwirl)
  .settings(
    sbtPlugin := true,
    name := "bootlog",
    organization := "com.github.bootlog",
    version := "0.1.0",
    scalaVersion := "2.10.4",
    unmanagedResourceDirectories in Compile += baseDirectory.value / "src/main/asset",
    libraryDependencies ++= Seq(
      "joda-time" % "joda-time" % "2.4",
      "org.joda" % "joda-convert" % "1.2",
      "org.pegdown" % "pegdown" % "1.6.0",
      "com.typesafe" % "config" % "1.2.1",
      // assets
      "org.webjars.bower" % "bootstrap" % "3.3.6",
      "org.webjars.bower" % "Bootflat" % "2.0.4",
      "org.webjars.bower" % "jquery" % "1.11.3",
      "org.webjars.bower" % "octicons" % "3.1.0"
    )
  )
  .settings(
    description := "An static blog generator powered by sbt, twirl, pegdown, bootstrap, bootflat.",
    startYear := Some(2015),
    homepage := Some(url("https://github.com/jasonqu/bootlog")),
    organizationHomepage := None,
    licenses in GlobalScope += "Apache License 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"),
    publishMavenStyle := false,
    bintrayRepository := "bootlog",
    bintrayOrganization := None
  )

TwirlKeys.templateImports ++= Seq(
  "com.github.bootlog.models._",
  "com.github.bootlog.util._",
  "com.github.bootlog.util.ConfigUtil._",
  "views.html._",
  "org.joda.time._")

site.settings
