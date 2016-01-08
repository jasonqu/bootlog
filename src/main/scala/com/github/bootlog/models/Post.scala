package com.github.bootlog.models

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import com.github.bootlog.util.markdown.PegDown._
import java.io.File

case class Post(
  title: String,
  tagline: String,
  name: String, // filename
  category: String,
  tags: List[String],
  date: DateTime,
  excerpt: String,
  html: String) {
  lazy val getDate = Post.format.print(date)
  lazy val getDateWithWeek = Post.formatWithWeek.print(date)
  lazy val getYear =Post.yearFormat.print(date)
  lazy val getYearMonth =Post.yearMonthFormat.print(date)
  lazy val getMonth =Post.monthFormat.print(date)
}

object Post {
  val format = DateTimeFormat.forPattern("yyyy-MM-dd")
  val yearFormat = DateTimeFormat.forPattern("yyyy")
  val yearMonthFormat = DateTimeFormat.forPattern("yyyy MMM")
  val monthFormat = DateTimeFormat.forPattern("MMM")
  val formatWithWeek = DateTimeFormat.forPattern("yyyy-MM-dd EE")

  def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  def getPost(file: File) = {
    val (metadata, content, excerpt) = processMdFile(file)

    // TODO refine date
    val date = try {
      format.parseDateTime(file.getName.substring(0, 10))
    } catch {
      case _: Throwable =>
        println(file.getName + " -> " + new DateTime(file.lastModified()))
        new DateTime(file.lastModified())
    }

    Post(
    metadata.getOrElse("title", "no title"),
    metadata.getOrElse("tagline", ""),
    file.getName + ".html",
    metadata.getOrElse("category", ""), {
      val s = metadata.getOrElse("tags", "[]").trim()
      s.substring(1, s.length() - 1).split(",").map(_.trim()).toList
    },
    date,
    metadata.getOrElse("excerpt", excerpt),
    content)
  }

  def getPosts(path: String) = {
    val postFiles = recursiveListFiles(new File(path)).filterNot(_.isDirectory)
    val posts = postFiles map getPost
    posts.sortWith((a, b) => a.date.isAfter(b.date))
  }
}