# bootlog

bootlog是基于sbt、twirl、pegdown、bootstrap和bootflat创建的一个静态博客生成器，目标用户是scala、java以及github使用者。

bootlog提供了两个示例，分别展示了[bootflat的主题](http://jasonqu.github.io/bootlogFlatDemo/)和[bootstrap主题](http://jasonqu.github.io/bootlogDemo/)。
如果想快速创建自己的博客，可以参考这两种博客中的介绍。

## 使用方法

在你的`project/plugins.sbt`中添加：

    addSbtPlugin("com.github.bootlog" % "bootlog" % "0.1.0")
    
    addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.8.1")
    
    resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"
    
    addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.5.4")

> *注意*：这里支持的sbt版本是0.13

然后在你的`build.sbt`文件中启动BootLog的插件，类似这样：

    lazy val bootlogDemo = (project in file("."))
      .enablePlugins(BootLogPlugin)

然后加上`sbt-site`和`sbt-ghpages`的配置就可以使用了

    site.settings
    
    // KEEP This, or else fonts file will not be copied by sbt-ghpages
    includeFilter in SiteKeys.makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.swf" | "*.ttf" | "*.woff" | "*.woff2"
    
    ghpages.settings
    
    git.remoteRepo := "https://github.com/{my github account}/{my blog repo}.git"

在`conf\application.conf`中写入自己的参数，在`_content\_posts`中编辑自己的文章。
完成后进入`sbt`，运行`makeMD`生成网站，使用`previewSite`预览自己的网站，最后使用`ghpagesPushSite`将网站发布到github。

## 配置参数

### sbt Setting

bootlog提供的sbt setting如下，可以通过修改`build.sbt`来修改这些配置：

    val bootlogConfigFile = settingKey[File]("the user config that will be rendered in generated pages")
    val generateDir = settingKey[File]("the output dir for bootlog.")
    val assetResourceMapping = settingKey[Seq[(String, String)]]("the user config that will be rendered in generated pages")
    val previewDrafts = settingKey[Boolean]("if this is true, then generated site will include the posts in _drafts")

#### `bootlogConfigFile`

`bootlog`读取的配置文件，默认是`conf\application.conf`。

要设置为根目录下的`blog.conf`，可以这样修改：`bootlogConfigFile := baseDirectory.value / "blog.conf"`

#### `generateDir`

`makeMD`输出的目录，默认是`src/site`，这是为了能无缝衔接`sbt-site`。

如果要修改`generateDir`，则需要使其输出可以配`sbt-site`读取。
可以使用`generateDir := SiteKeys.siteSourceDirectory.value`将两个配置连接起来。

#### `assetResourceMapping`

`assetResourceMapping`用来保存网站需要的静态文件，如js、css、字体等。

这个映射的key将要被拷贝到的文件；而value是资源的位置，如果以`/`开头，则将在类路径中查找，否则将在工程目录中查找。

为了减少用户的配置，bootlog把默认的资源都打包在插件或插件依赖的webjar中。

      "org.webjars.bower" % "bootstrap" % "3.3.6",
      "org.webjars.bower" % "Bootflat" % "2.0.4",
      "org.webjars.bower" % "jquery" % "1.11.3",
      "org.webjars.bower" % "octicons" % "3.1.0"

要使用打包在webjar中的资源，可以像下面这样：

    assetResourceMapping += ("stylesheets/bootstrap.3.3.6.min.css" -> "/META-INF/resources/webjars/bootstrap/3.3.6/dist/css/bootstrap.min.css")

如果用户有自定义的资源文件（比如使用bower下载的资源文件），可以像下面这样使用：

    assetResourceMapping += ("stylesheets/style.css" -> "assets/css/style.css"),

此时将把工程目录的`assets/css/style.css`文件，拷贝到`generateDir / "stylesheets/style.css"`。

#### `previewDrafts`

如果需要预览`_content/_drafts`目录下的markdown文件，则需要修改该配置：`previewDrafts := true`

### conf 文件介绍

bootlog使用[typesafe config](https://github.com/typesafehub/config)作为配置文件库，其格式很既灵活有易读，参见[HOCON](https://github.com/typesafehub/config#using-hocon-the-json-superset)。

bootlog支持的配置详见[源代码](https://github.com/jasonqu/bootlog/blob/master/src/main/resources/reference.conf)，需要重点关注的是：

* rootPath：该静态网站将被放在主站的哪个目录下。
* production_url：该静态网站的主站链接

具体使用示例，可以参考[bootlogDemo](http://jasonqu.github.io/bootlogDemo/)和[bootlogFlatDemo](http://jasonqu.github.io/bootlogFlatDemo/)。

### 许可证

[Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
