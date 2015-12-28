lazy val bootlog = (project in file(".")).enablePlugins(SbtTwirl).
  settings(
    name := "bootlog",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.11.4",
    libraryDependencies ++= Seq(
      "com.github.scala-incubator.io" % "scala-io-file_2.10" % "0.4.2",
      "joda-time" % "joda-time" % "2.4",
      "org.pegdown" % "pegdown" % "1.4.2",
      "com.typesafe" % "config" % "1.2.1",
      "commons-io" % "commons-io" % "2.4"
    )
  )

TwirlKeys.templateImports ++= Seq(
"com.github.bootlog._",
"com.github.bootlog.models._",
"com.github.bootlog.util.Config._",
"views.html._",
"org.joda.time._")

site.settings