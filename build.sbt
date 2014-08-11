name := "bootlog"

version := "0.1"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.2",
  "joda-time" % "joda-time" % "2.4",
  "org.pegdown" % "pegdown" % "1.4.2",
  "com.typesafe" % "config" % "1.2.1"
)

lazy val root = (project in file(".")).enablePlugins(SbtTwirl)

TwirlKeys.templateImports ++= Seq(
"com.github.bootlog._",
"com.github.bootlog.models._",
"com.github.bootlog.util.Config._",
"views.html._",
"org.joda.time._")