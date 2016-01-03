package com.github.bootlog.models

import org.joda.time._
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
  html: String) {
}

object Post {
  def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  def getPost(file: File) = {
    val (metadata, content) = processMdFile(file)

    // TODO refine date
    val date = try {
      DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(file.getName.substring(0, 10))
    } catch {
      case _: Throwable => new DateTime(file.lastModified())
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
    content)
  }

  def getPosts(path: String) = {
    val postFiles = recursiveListFiles(new File(path)).filterNot(_.isDirectory)
    val posts = postFiles map getPost
    posts.sortWith((a, b) => a.date.isAfter(b.date))
  }
}