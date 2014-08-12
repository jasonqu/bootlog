package com.github.bootlog

import com.github.bootlog.models.Post
import scala.collection.mutable

object Application extends App {

  def writeFile(path: String, content: String) = {
    val p = new java.io.PrintWriter(path)
    try { p.write(content) } finally { p.close() }
  }

  // TODO add io operations: delete site dir and copy assets

  val posts: Array[Post] = Post.getPosts

  writeFile("site/index.html", views.html.index("")(posts).toString())

  // posts
  new java.io.File("site/post").mkdir()
  posts.foreach { post =>
//    case post if post.name.endsWith(".md") =>
//      writeFile("site/post/" + post.name.substring(0, post.name.length - 3) + "html", views.html.post(post).toString())
//    case post
    writeFile("site/post/" + post.name, views.html.post(post).toString())
  }

  // archive page
  val archives = posts.groupBy(_.date.getYear)
    .mapValues(_.groupBy(_.date.getMonthOfYear)
    .mapValues(_.sortWith((a, b) => a.date isAfter b.date)))
  writeFile("site/archive.html", views.html.pages.archive(archives).toString())

  // categories
  val categories = posts.groupBy(_.category)
  writeFile("site/categories.html", views.html.pages.categories(categories).toString())

  // pages
  writeFile("site/pages.html", views.html.pages.pages().toString())

  // tags
  val mm = new mutable.HashMap[String, mutable.Set[Post]] with collection.mutable.MultiMap[String, Post]
  for(post <- posts) {
    post.tags.foreach { tag =>
      mm.addBinding(tag, post)
    }
  }
  writeFile("site/tags.html", views.html.pages.tags(mm).toString())

  // atom
  writeFile("site/atom.xml", views.xml.pages.atom(Post.getPosts).toString().trim())

  // rss
  writeFile("site/rss.xml", views.xml.pages.rss(Post.getPosts).toString().trim())

  import com.github.bootlog.util.Config._
  val pages = "archive.html" :: "atom.xml" :: "categories.html" :: "index.html" :: "pages.html" :: "rss.xml" :: "sitemap.txt" :: "tags.html" :: Nil
  val ps = pages.map { message("production_url") + "/" + _ +"\n" }.mkString
  val pts = posts.map { message("production_url") + "/post/" + _.name }.mkString("\n")
  writeFile("site/sitemap.txt", ps + "\n" + pts)
}

