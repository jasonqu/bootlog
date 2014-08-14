package com.github.bootlog

import com.github.bootlog.models.Post
import scala.collection.mutable
import com.github.bootlog.util.Config._
import org.apache.commons.io.FileUtils._
import java.io._

object Application extends App {

  def writeFile(path: String, content: String) = {
    val p = new PrintWriter(path)
    try { p.write(content) } finally { p.close() }
  }

  // io operations: delete site dir and copy assets
  val generate_dir = message("generate_dir")
  val file_generate_dir = new File(generate_dir)
  file_generate_dir.mkdirs()
  cleanDirectory(file_generate_dir)
  copyDirectory(new File("src/main/asset"), file_generate_dir)


  val posts: Array[Post] = Post.getPosts("_content/_posts")

  writeFile(s"$generate_dir/index.html", views.html.index("")(posts).toString())

  // posts
  new File(s"$generate_dir/post").mkdir()
  posts.foreach { post =>
    writeFile(s"$generate_dir/post/" + post.name, views.html.post(post).toString())
  }

  // archive page
  val archives = posts.groupBy(_.date.getYear)
    .mapValues(_.groupBy(_.date.getMonthOfYear)
    .mapValues(_.sortWith((a, b) => a.date isAfter b.date)))
  writeFile(s"$generate_dir/archive.html", views.html.pages.archive(archives).toString())

  // categories
  val categories = posts.groupBy(_.category)
  writeFile(s"$generate_dir/categories.html", views.html.pages.categories(categories).toString())

  // pages
  writeFile(s"$generate_dir/pages.html", views.html.pages.pages().toString())

  // tags
  val mm = new mutable.HashMap[String, mutable.Set[Post]] with collection.mutable.MultiMap[String, Post]
  for(post <- posts) {
    post.tags.foreach { tag =>
      mm.addBinding(tag, post)
    }
  }
  writeFile(s"$generate_dir/tags.html", views.html.pages.tags(mm).toString())

  // atom
  writeFile(s"$generate_dir/atom.xml", views.xml.pages.atom(posts).toString().trim())

  // rss
  writeFile(s"$generate_dir/rss.xml", views.xml.pages.rss(posts).toString().trim())


  val pages = "archive.html" :: "atom.xml" :: "categories.html" :: "index.html" :: "pages.html" :: "rss.xml" :: "sitemap.txt" :: "tags.html" :: Nil
  val ps = pages.map { message("production_url") + "/" + _ +"\n" }.mkString
  val pts = posts.map { message("production_url") + "/post/" + _.name }.mkString("\n")
  writeFile(s"$generate_dir/sitemap.txt", ps + "\n" + pts)
}

