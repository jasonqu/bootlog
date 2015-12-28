package com.github.bootlog

import sbt._
import Keys._


object BootLogPlugin extends AutoPlugin {
  object autoImport {
    val makeMD = TaskKey[File]("make-md", "Generates a static markdown website for a project by using bootlog.")

    val bootlogConfig = SettingKey[String]("bootlogConfig", "the bootlog config that used by the user")
  }

  import autoImport._
  lazy val baseSettings: Seq[Def.Setting[_]] = Seq(
    bootlogConfig :=
      """
        |# site parameters
        |site.title = bootstrap blog with twirl
        |site.tagline = a twirl example for jekyll-bootstrap
        |site.description = hello world
        |
        |author.name = jasonqu
        |author.email = "qiuguo0205@gmail.com"
        |author.github = jasonqu
        |author.twitter = username
        |author.feedburner = feedname
        |
        |rootPath = ""
        |production_url = "http://username.github.io"
        |
        |# generator parameters
        |generate_dir = src/site
      """.stripMargin,

    makeMD := Application.process(bootlogConfig.value)
  )

  def copySite(dir: File, cacheDir: File, maps: Seq[(File, String)]): File = {
    val concrete = maps map { case (file, dest) => (file, dir / dest) }
    Sync(cacheDir / "make-site")(concrete)
    dir
  }

  override val projectSettings =
    inConfig(Compile)(baseSettings)
  override val buildSettings =
    (baseSettings)

//  override def trigger = allRequirements
//  override lazy val buildSettings = Seq(
//  //override lazy val globalSettings = Seq(
//
//    commands += helloCommand)
//  lazy val helloCommand =
//    Command.command("hello") { (state: State) =>
//      //println(greeting.value)
//      println("Hi!")
//      state
//    }
}