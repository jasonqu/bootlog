package com.github.bootlog

import java.io.File

import com.github.bootlog.models.Post
import com.github.bootlog.util.ConfigUtil
import com.typesafe.config.{Config, ConfigFactory}
import sbt.IO._
import sbt.Keys._
import sbt._

import scala.collection.mutable

object BootLogPlugin extends AutoPlugin {
  object autoImport {
    val makeMD = taskKey[File]("Generates a static markdown website for a project by using bootlog.")
    val generateDir = settingKey[File]("the output dir for bootlog.")
    val bootlogConfigFile = settingKey[File]("the user config that will be rendered in generated pages")
    val assetResourceMapping = settingKey[Seq[(String, String)]]("the user config that will be rendered in generated pages")
    val previewDrafts = settingKey[Boolean]("if this is true, then generated site will include the posts in _drafts")
  }

  import autoImport._
  lazy val baseSettings: Seq[Def.Setting[_]] = Seq(
    bootlogConfigFile := baseDirectory.value / "conf/application.conf",
    generateDir := baseDirectory.value / "src/site",
    previewDrafts := false,
    assetResourceMapping := Seq(
      // css
      "stylesheets/bootstrap.3.3.6.min.css" -> "/META-INF/resources/webjars/bootstrap/3.3.6/dist/css/bootstrap.min.css",
      "stylesheets/bootflat-2.0.4.min.css" -> "/META-INF/resources/webjars/Bootflat/2.0.4/bootflat/css/bootflat.min.css",
      // fonts
      "fonts/glyphicons-halflings-regular.eot" -> "/META-INF/resources/webjars/bootstrap/3.3.6/fonts/glyphicons-halflings-regular.eot",
      "fonts/glyphicons-halflings-regular.woff" -> "/META-INF/resources/webjars/bootstrap/3.3.6/fonts/glyphicons-halflings-regular.woff",
      "fonts/glyphicons-halflings-regular.ttf" -> "/META-INF/resources/webjars/bootstrap/3.3.6/fonts/glyphicons-halflings-regular.ttf",
      "fonts/glyphicons-halflings-regular.woff2" -> "/META-INF/resources/webjars/bootstrap/3.3.6/fonts/glyphicons-halflings-regular.woff2",
      "stylesheets/octicons.css" -> "/META-INF/resources/webjars/octicons/3.1.0/octicons/octicons.css",
      "stylesheets/octicons.eot" -> "/META-INF/resources/webjars/octicons/3.1.0/octicons/octicons.eot",
      "stylesheets/octicons.svg" -> "/META-INF/resources/webjars/octicons/3.1.0/octicons/octicons.svg",
      "stylesheets/octicons.ttf" -> "/META-INF/resources/webjars/octicons/3.1.0/octicons/octicons.ttf",
      "stylesheets/octicons.woff" -> "/META-INF/resources/webjars/octicons/3.1.0/octicons/octicons.woff",
      // image
      "images/blog.png" -> "/images/blog.png",
      "images/cover.jpg" -> "/images/cover.jpg",
      // js jquery should be before bootstrap
      "javascripts/jquery-1.11.3.min.js" -> "/META-INF/resources/webjars/jquery/1.11.3/dist/jquery.min.js",
      "javascripts/bootstrap-3.3.6.min.js" -> "/META-INF/resources/webjars/bootstrap/3.3.6/dist/js/bootstrap.min.js"
      // customize
      //"stylesheets/app.css" -> "/stylesheets/app.css"
      //"stylesheets/style.css" -> "/stylesheets/style.css"
    ),
    makeMD := process(
      bootlogConfigFile.value,
      generateDir.value,
      assetResourceMapping.value,
      previewDrafts.value,
      streams.value.log
    )
  )

  override val projectSettings = baseSettings

  val charset = java.nio.charset.StandardCharsets.UTF_8

  def process(configFile : File, generate_dir : File, assets : Seq[(String, String)], previewDrafts: Boolean, log: Logger) : File = {
    val conf = ConfigFactory.parseString(read(configFile))//.withFallback(ConfigFactory.load()) TODO load config in reference
    ConfigUtil.conf = conf

    // io operations: delete site dir
    delete(generate_dir)
    generate_dir.mkdirs()

    // parse posts
    val posts: Array[Post] =
      if (previewDrafts) {
        Post.getPosts("_content/_posts") ++ Post.getPosts("_content/_drafts")
      } else {
        Post.getPosts("_content/_posts")
      }

    val theme: String = conf.getString("theme")
    var assetsAfterCustomize = assets
    if(theme.equals("bootflat")) {
      assetsAfterCustomize = ("stylesheets/app.css" -> "/stylesheets/app.css") +: assetsAfterCustomize
      processAssets(generate_dir, assetsAfterCustomize, log)
      processBootflatTheme(generate_dir, conf, posts)
    } else {
      if(!theme.equals("default"))
        log.warn(s"unknown theme '$theme', change to default theme.")
      assetsAfterCustomize = ("stylesheets/style.css" -> "/stylesheets/style.css") +: assetsAfterCustomize
      processAssets(generate_dir, assetsAfterCustomize, log)
      processDefaultTheme(generate_dir, conf, posts)
    }

    // common part
    // atom
    write(generate_dir / "atom.xml", views.xml.boot.pages.atom(posts).toString().trim(), charset)
    // rss
    write(generate_dir / "rss.xml", views.xml.boot.pages.rss(posts).toString().trim(), charset)

    generate_dir
  }

  def processAssets(generate_dir: sbt.File, assets: Seq[(String, String)], log: Logger): Unit = {
    assets.foreach { case (filePath, url) =>
      try {
        if(url.startsWith("/")) {
          // copy assets in webjar
          transfer(getClass.getResource(url).openStream(), generate_dir / filePath)
        } else {
          // copy assets in file
          copyFile(new File(url), generate_dir / filePath)
        }
      } catch {
        case e: Throwable =>
          log.error(s"catch Exception when copy $url")
          e.printStackTrace()
      }
    }
    ConfigUtil.assets = assets.map(_._1)
    log.info("copied assets : " + ConfigUtil.assets)
  }

  def processBootflatTheme(generate_dir: sbt.File, conf: Config, allPosts: Array[Post]): Unit = {
    val posts_per_page = if(conf.getInt("posts_per_page") < 10) 10 else conf.getInt("posts_per_page")

    val postPage : List[Array[Post]] = allPosts.grouped(posts_per_page).toList
    val totalPage = postPage.size

    allPosts.foreach { post =>
      write(generate_dir / "posts" / post.name, views.html.flat.post(post).toString(), charset)
    }

    // indexes
    postPage.zipWithIndex.foreach { case (posts, pageIndex) =>
      val postGroup: List[(String, Array[(Post, Int)])] = posts.toArray.zipWithIndex
        .groupBy(p => p._1.date.getYear * 100 + p._1.date.getMonthOfYear)
        .toList.sortBy(- _._1).map(p => (p._2(0)._1.getYearMonth, p._2))
      if (pageIndex == 0) {
        write(generate_dir / "index.html", views.html.flat.archive(postGroup)("", pageIndex, totalPage).toString(), charset)
      } else {
        write(generate_dir / s"index$pageIndex.html", views.html.flat.archive(postGroup)("", pageIndex, totalPage).toString(), charset)
      }
    }

    // tags
    val mm = new mutable.HashMap[String, mutable.Set[Post]] with mutable.MultiMap[String, Post]
    for (post <- allPosts) {
      post.tags.foreach { tag =>
        mm.addBinding(tag, post)
      }
    }

    mm.foreach { case (tag, posts) =>
      val postGroup: List[(String, Array[(Post, Int)])] = posts.toArray.sorted(Ordering.by((_: Post).date.getMillis).reverse).zipWithIndex.
        groupBy(p => p._1.date.getYear * 100 + p._1.date.getMonthOfYear)
        .toList.sortBy(- _._1).map(p => (p._2(0)._1.getYearMonth, p._2))
      write(generate_dir / "tag" / s"$tag.html", views.html.flat.archive(postGroup)(tag, 0, 1).toString(), charset)
    }
  }

  def processDefaultTheme(generate_dir: sbt.File, conf: Config, posts: Array[Post]): Unit = {
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
