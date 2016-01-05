package com.github.bootlog

import java.io.File
import java.nio.charset.Charset
import com.github.bootlog.models.Post
import com.github.bootlog.util.ConfigUtil
import com.typesafe.config.{Config, ConfigFactory}
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
    val assetResourceMapping = SettingKey[Seq[(String, String)]]("assetResourceMapping", "the user config that will be rendered in generated pages")
  }

  import autoImport._
  lazy val baseSettings: Seq[Def.Setting[_]] = Seq(
    bootlogConfigFile := baseDirectory.value / "conf/application.conf",
    generateDir := baseDirectory.value / "src/site",
    assetResourceMapping := Seq(
      // css
      "stylesheets/bootstrap.3.3.6.min.css" -> "/META-INF/resources/webjars/bootstrap/3.3.6/dist/css/bootstrap.min.css",
      "stylesheets/bootflat-2.0.4.min.css" -> "/META-INF/resources/webjars/Bootflat/2.0.4/bootflat/css/bootflat.min.css",
      // js jquery should be before bootstrap
      "javascripts/jquery-1.11.3.min.js" -> "/META-INF/resources/webjars/jquery/1.11.3/dist/jquery.min.js",
      "javascripts/bootstrap-3.3.6.min.js" -> "/META-INF/resources/webjars/bootstrap/3.3.6/dist/js/bootstrap.min.js",
      // customize
      "stylesheets/style.css" -> "/stylesheets/style.css"
    ),
    makeMD := process(
      Source.fromFile(bootlogConfigFile.value).getLines().mkString("\n"),
      generateDir.value,
      assetResourceMapping.value
    )
  )

  override val projectSettings =
    inConfig(Compile)(baseSettings)

  val charset = java.nio.charset.StandardCharsets.UTF_8

  def process(config : String, generate_dir : File, assets : Seq[(String, String)]) : File = {
    //println(config)

    val conf = ConfigFactory.parseString(config)
    ConfigUtil.conf = conf

    // io operations: delete site dir
    delete(generate_dir)
    generate_dir.mkdirs()

    //  copy assets in webjar
    assets.foreach { pair =>
      val (filePath, url) = pair
      // TODO do not need to create dir? createDirectory(generate_dir / "stylesheets")
      // TODO check file exist?
      writeLines(generate_dir / filePath, Source.fromURL(getClass.getResource(url)).getLines().toSeq)
    }
    ConfigUtil.assets = assets.map(_._1)

    // parse posts
    val posts: Array[Post] = Post.getPosts("_content/_posts")

    if(true) {
      processBootflatTheme(generate_dir, conf, posts)
    } else {
      processDefaultTheme(generate_dir, conf, posts)
    }

    generate_dir
  }
  def processBootflatTheme(generate_dir: sbt.File, conf: Config, posts: Array[Post]): Unit = {
    createDirectory(generate_dir / "post")
    posts.foreach { post =>
      write(generate_dir / "post" / post.name, views.html.flat.post(post).toString(), charset)
    }
  }


  def processDefaultTheme(generate_dir: sbt.File, conf: Config, posts: Array[Post]): Unit = {
    // generate pages
    // posts
    createDirectory(generate_dir / "post")
    posts.foreach { post =>
      write(generate_dir / "post" / post.name, views.html.boot.post(post).toString(), charset)
    }

    // archive page
    val archives = posts.groupBy(_.date.getYear)
      .mapValues(_.groupBy(_.date.getMonthOfYear)
      .mapValues(_.sortWith((a, b) => a.date isAfter b.date)))
    write(generate_dir / "archive.html", views.html.boot.pages.archive(archives).toString(), charset)

    // index
    val indexFile = new sbt.File("_content/index.md")
    if (indexFile.exists()) {
      write(generate_dir / "index.html", views.html.boot.post(Post.getPost(indexFile)).toString(), charset)
    } else {
      write(generate_dir / "index.html", views.html.boot.pages.archive(archives).toString(), charset)
    }

    // categories
    val categories = posts.groupBy(_.category)
    write(generate_dir / "categories.html", views.html.boot.pages.categories(categories).toString(), charset)

    // pages
    write(generate_dir / "pages.html", views.html.boot.pages.pages().toString(), charset)

    // tags
    val mm = new mutable.HashMap[String, mutable.Set[Post]] with mutable.MultiMap[String, Post]
    for (post <- posts) {
      post.tags.foreach { tag =>
        mm.addBinding(tag, post)
      }
    }
    write(generate_dir / "tags.html", views.html.boot.pages.tags(mm).toString(), charset)

    // atom
    write(generate_dir / "atom.xml", views.xml.boot.pages.atom(posts).toString().trim(), charset)

    // rss
    write(generate_dir / "rss.xml", views.xml.boot.pages.rss(posts).toString().trim(), charset)

    // sitemap
    val pages = "archive.html" :: "atom.xml" :: "categories.html" :: "index.html" :: "pages.html" :: "rss.xml" :: "sitemap.txt" :: "tags.html" :: Nil
    val ps = pages.map {
      conf.getString("production_url") + "/" + _ + "\n"
    }.mkString
    val pts = posts.map {
      conf.getString("production_url") + "/post/" + _.name
    }.mkString("\n")
    write(generate_dir / "sitemap.txt", ps + "\n" + pts, charset)
  }
}
