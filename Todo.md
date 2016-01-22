## TODO

* [x] sbt plugin
  * [x] 1st version done
* [x] sub domain eg. blog
* [x] webjar
  * [x] https://github.com/sbt/sbt-web/issues/95
  * [x] done:just use Source.fromURL
  * [x] optional: could use bower instead
* [x] blog demo
  * [x] simple demo
  * [x] simple demo change to boot3
  * [x] bootflat blog
* [x] chain generateDir and site's siteSourceDirectory
* [x] deploy to web : bintray
* [x] index.md
  * [x] if it exist, generate index.html
  * [x] else if `rootpath == ""`, use archive.html as index.html
  * [ ] else use prod_url as index.html
    * [ ] useless if blog has the same domain with personal website
* [x] bootflat https://github.com/fabienwang/Ghost-Flat
  * [x] head image
  * [x] blog image
  * [x] pagination
  * [x] default-wide
  * [x] post
  * [x] tags -> reuse archive
  * [x] tags index
    * [x] http://design.altervista.org/css-tags-how-to-style-post-tags/
      * [ ] random color
    * [x] http://www.cssflow.com/snippets/sliding-tags
    * [x] http://cssglobe.com/pure-css3-post-tags/
  * [ ] category
* [x] support drafts
* [ ] one image per blog
* [x] image in blog
  * [x] have to be in the relative `img` dir
* [x] todo google prettify refactor
  * [x] or prism
* [x] configurable theme
  * [x] configurable twirl template -> not acceptable, use configurable assets instead
  * [x] http://bootflat.github.io/documentation.html background
  * [x] theme example
  * [x] configurable navbar
  * [x] configurable excerpt
* [ ] try markdown-js instead of pegDown
  * [ ] https://github.com/evilstreak/markdown-js
  * [ ] https://github.com/chjj/marked
* [ ] comment/analytics support
  * [ ] https://github.com/plusjade/jekyll-bootstrap/blob/master/_config.yml
* [x] charset issue : only support utf8 and system's default one
* [ ] support incremental makeMD
  * [ ] support [Triggered Execution](http://www.scala-sbt.org/0.13/docs/Triggered-Execution.html)