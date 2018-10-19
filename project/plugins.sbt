
addSbtPlugin("com.47deg"  % "sbt-microsites" % "0.7.15")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.0")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")

val scalaJSVersion =
  Option(System.getenv("SCALAJS_VERSION")).getOrElse("0.6.25")

addSbtPlugin("org.portable-scala" % "sbt-crossproject" % "0.6.0")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % scalaJSVersion)

addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.0")

addSbtPlugin("io.github.cquiroz" % "sbt-tzdb" % "0.3.0")
