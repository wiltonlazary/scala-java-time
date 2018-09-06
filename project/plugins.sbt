import sbt.Keys._

resolvers += Resolver.sonatypeRepo("public")

addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-RC13")

addSbtPlugin("com.47deg"  % "sbt-microsites" % "0.7.15")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.0")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")

val scalaJSVersion =
  Option(System.getenv("SCALAJS_VERSION")).getOrElse("0.6.25")

addSbtPlugin("org.portable-scala" % "sbt-crossproject" % "0.6.0")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % scalaJSVersion)

addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.0")

addSbtPlugin("io.github.cquiroz" % "sbt-tzdb" % "0.1.2")

// Incompatible with 2.12.0-M5
// addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.5")
// addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.1.0")
