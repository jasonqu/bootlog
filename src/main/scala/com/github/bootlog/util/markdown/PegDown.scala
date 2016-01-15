package com.github.bootlog.util.markdown

import java.io.File
import org.pegdown.PegDownProcessor

/**
 * process file with metadata in the head
 */
object PegDown {

  /**
   * @param file
   * @return tuple, 1st is metadata; 2nd is PegDown Processed html
   */
  def processMdFile(file: File) = {
    val pro = new PegDownProcessor()
    val lines = scala.io.Source.fromFile(file, "utf8").getLines().toList

    val (metadata, md) = lines.drop(1).span { !_.startsWith("---") }
    
    val meta = metadata.map { line =>
      val (k, v) = line.span(_ != ':')
      (k.trim, v.drop(1).trim.replaceAll("\"", ""))
    }
    val mdContent = md.drop(1).mkString("\n")
    (meta.toMap, pro.markdownToHtml(mdContent), mdContent.substring(0, 200))
  }

  def processMdContent(md: String) = {
    new PegDownProcessor().markdownToHtml(md)
  }
}