
addSbtPlugin("com.47deg"  % "sbt-microsites" % "1.0.2")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.7")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "2.0.1")

val scalaJSVersion =
  Option(System.getenv("SCALAJS_VERSION")).getOrElse("0.6.31")

addSbtPlugin("org.portable-scala" % "sbt-crossproject" % "0.6.1")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % scalaJSVersion)

addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.1")

addSbtPlugin("io.github.cquiroz" % "sbt-tzdb" % "0.3.2")
