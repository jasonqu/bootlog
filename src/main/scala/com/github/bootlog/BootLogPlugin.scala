package com.github.bootlog

import java.io.File
import com.github.bootlog.models.Post
import com.github.bootlog.util.ConfigUtil
import com.typesafe.config.ConfigFactory
import sbt.IO._
import sbt._
import Keys._

import scala.collection.mutable
import scala.io.Source

object BootLogPlugin extends AutoPlugin {
  object autoImport {
    val makeMD = TaskKey[File]("make-md", "Generates a static markdown website for a project by using bootlog.")
    val generateDir = SettingKey[File]("generate-dir", "the output dir for bootlog.")
    val bootlogConfigFile = SettingKey[File]("bootlogConfigFile", "the user config that will be rendered in generated pages")
    val assetResourceMapping = SettingKey[Seq[(File, String)]]("assetResourceMapping", "the user config that will be rendered in generated pages")
  }

  import autoImport._
  lazy val baseSettings: Seq[Def.Setting[_]] = Seq(
    bootlogConfigFile := baseDirectory.value / "conf/application.conf",
    generateDir := baseDirectory.value / "src/site",
    assetResourceMapping := Seq(
      (generateDir.value / "stylesheets/bootstrap.2.2.2.min.css") -> "/META-INF/resources/webjars/bootstrap/2.2.2/css/bootstrap.min.css",
      (generateDir.value / "javascripts/jquery-1.9.0.min.js") -> "/META-INF/resources/webjars/jquery/1.9.0/jquery.min.js",
      (generateDir.value / "stylesheets/style.css") -> "/stylesheets/style.css"
    ),
    makeMD := process(
      Source.fromFile(bootlogConfigFile.value).getLines().mkString("\n"),
      generateDir.value,
      assetResourceMapping.value
    )
  )

  override val projectSettings =
    inConfig(Compile)(baseSettings)

  def process(config : String, generate_dir : File, assets : Seq[(File, String)]) : File = {
    val charset = java.nio.charset.StandardCharsets.UTF_8
    //println(config)

    val conf = ConfigFactory.parseString(config)
    ConfigUtil.conf = conf

    // io operations: delete site dir
    delete(generate_dir)
    generate_dir.mkdirs()

    //  copy assets in webjar
    createDirectory(generate_dir / "stylesheets")

    assets.foreach {pair =>
      val (file, url) = pair
      writeLines(file, Source.fromURL(getClass.getResource(url)).getLines().toSeq)
    }

    // parse posts
    val posts: Array[Post] = Post.getPosts("_content/_posts")

    // generate pages
    // index TODO refine index
    write(generate_dir / "index.html", views.html.index("")(posts).toString(), charset)

    // posts
    createDirectory(generate_dir / "post")
    posts.foreach { post =>
      write(generate_dir / "post" / post.name, views.html.post(post).toString(), charset)
    }

    // archive page
    val archives = posts.groupBy(_.date.getYear)
      .mapValues(_.groupBy(_.date.getMonthOfYear)
      .mapValues(_.sortWith((a, b) => a.date isAfter b.date)))
    write(generate_dir / "archive.html", views.html.pages.archive(archives).toString(), charset)

    // categories
    val categories = posts.groupBy(_.category)
    write(generate_dir / "categories.html", views.html.pages.categories(categories).toString(), charset)

    // pages
    write(generate_dir / "pages.html", views.html.pages.pages().toString(), charset)

    // tags
    val mm = new mutable.HashMap[String, mutable.Set[Post]] with collection.mutable.MultiMap[String, Post]
    for (post <- posts) {
      post.tags.foreach { tag =>
        mm.addBinding(tag, post)
      }
    }
    write(generate_dir / "tags.html", views.html.pages.tags(mm).toString(), charset)

    // atom
    write(generate_dir / "atom.xml", views.xml.pages.atom(posts).toString().trim(), charset)

    // rss
    write(generate_dir / "rss.xml", views.xml.pages.rss(posts).toString().trim(), charset)

    // sitemap
    val pages = "archive.html" :: "atom.xml" :: "categories.html" :: "index.html" :: "pages.html" :: "rss.xml" :: "sitemap.txt" :: "tags.html" :: Nil
    val ps = pages.map {
      conf.getString("production_url") + "/" + _ + "\n"
    }.mkString
    val pts = posts.map {
      conf.getString("production_url") + "/post/" + _.name
    }.mkString("\n")
    write(generate_dir / "sitemap.txt", ps + "\n" + pts, charset)

    generate_dir
  }
}