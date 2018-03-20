resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += "heroku-sbt-plugin-releases" at "https://dl.bintray.com/heroku/sbt-plugins/"

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.11")

addSbtPlugin("com.typesafe.sbt" % "sbt-play-ebean" % "4.1.0")