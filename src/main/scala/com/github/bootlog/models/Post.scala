package com.github.bootlog.models

import org.joda.time._

case class Post(
  title: String,
  tagline: String,
  name: String, // filename
  category: String,
  tags: List[String],
  date: DateTime,
  html: String) {
}