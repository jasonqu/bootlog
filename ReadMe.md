## bootlog

a [twirl](https://github.com/playframework/twirl) static site generator inspired by [jekyllbootstrap](http://jekyllbootstrap.com/)

use `sbt run` to build the site and check it out in `src/site`

## TODO

* [x] sbt plugin
  * [x] 1st version done
* [x] sub domain eg. blog
* [ ] todo google prettify refactor
  * [ ] or prism
* [x] webjar
  * [x] https://github.com/sbt/sbt-web/issues/95
  * [x] done:just use Source.fromURL
  * [ ] optional: could use bower instead
* [ ] blog demo
  * [x] simple demo
  * [x] simple demo change to boot3
  * [ ] my blog
* [ ] configurable theme
  * [x] configurable twirl template -> not acceptable, use configurable assets instead
* [ ] try markdown-js instead of pegDown
  * [ ] https://github.com/evilstreak/markdown-js
  * [ ] https://github.com/chjj/marked
* [ ] comment/analytics support
  * [ ] https://github.com/plusjade/jekyll-bootstrap/blob/master/_config.yml
* [ ] chain generateDir and site's siteSourceDirectory
* [ ] deploy to web
* [x] index.md
  * [x] if it exist, generate index.html
  * [x] else if `rootpath == ""`, use archive.html as index.html
  * [ ] else use prod_url as index.html
    * [ ] useless if blog has the same domain with personal website
* [ ] bootflat https://github.com/fabienwang/Ghost-Flat
  * [x] head-image
  * [ ] pagination
  * [ ] default-wide
  * [ ] post
  * [x] tags -> reuse archive