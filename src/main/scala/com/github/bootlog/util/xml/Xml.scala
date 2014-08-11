package com.github.bootlog.util.xml

import com.github.bootlog.models.Post
//import play.api.Play.current
//import play.api.i18n.Messages

object Xml {
//  def atomPost(post: Post) = {
//    <entry>
//      <title>{ post.title }</title>
//      <link href="/post/{post.name}"/>
//      <updated>{ post.date }</updated>
//      <id>{ Messages("production_url") }/post/{ post.name }</id>
//      <content type="html">{ post.html }</content>
//    </entry>
//  }
//
//  def atom(posts: Array[Post]) = {
//    <?xml version="1.0" encoding="utf-8"?>
//    <feed xmlns="http://www.w3.org/2005/Atom">
//      <title>{ Messages("site.title") }</title>
//      <link href={ Messages("production_url") + "/atom.xml" } rel="self"/>
//      <link href={ Messages("production_url") + ""}/>
//      <updated>site.time</updated>
//      <id>{ Messages("production_url") }</id>
//      <author>
//        <name>{ Messages("author.name") }</name>
//        <email>{ Messages("author.email") }</email>
//      </author>
//      {for (post <- posts) yield {atomPost(post)}}
//    </feed>
//  }
//
//  def rssPost(post: Post) = {
//    <item>
//      <title>{ post.title }</title>
//      <description>{ post.html }</description>
//      <link>{ Messages("production_url") + "/post/" + post.name }</link>
//      <guid>{ Messages("production_url") + "/post/" + post.name }</guid>
//      <pubDate>{ post.date }</pubDate>
//    </item>
//  }
//
//  def rss(posts: Array[models.Post]) = {
//    <rss version="2.0">
//      <channel>
//        <title>{ Messages("site.title") }</title>
//        <description>{ Messages("site.title") } - { Messages("author.name") }</description>
//        <link>{ Messages("production_url") + "" }/rss.xml</link>
//        <link>{ Messages("production_url") + "" }</link>
//        <lastBuildDate>site.time</lastBuildDate>
//        <pubDate>site.time</pubDate>
//        <ttl>1800</ttl>
//        { for (post <- posts) yield { rssPost(post) } }
//      </channel>
//    </rss>
//  }

}