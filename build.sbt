import sbtcrossproject.CrossPlugin.autoImport.{ crossProject, CrossType }
import sbt._
import sbt.io.Using

val scalaVer = "2.12.8"
val tzdbVersion = "2019a"
val scalaJavaTimeVer = "2.0.0-RC3-SNAPSHOT"
val scalaJavaTimeVersion = s"$scalaJavaTimeVer"
val scalaTZDBVersion = s"${scalaJavaTimeVer}_$tzdbVersion"

lazy val downloadFromZip: TaskKey[Unit] =
  taskKey[Unit]("Download the tzdb tarball and extract it")

lazy val commonSettings = Seq(
  description  := "java.time API implementation in Scala and Scala.js",
  version      := scalaJavaTimeVersion,
  organization := "io.github.cquiroz",
  homepage     := Some(url("https://github.com/cquiroz/scala-java-time")),
  licenses     := Seq("BSD 3-Clause License" -> url("https://opensource.org/licenses/BSD-3-Clause")),

  scalaVersion       := scalaVer,
  crossScalaVersions := {
    if (scalaJSVersion.startsWith("0.6")) {
      Seq("2.10.7", "2.11.12", "2.12.8", "2.13.0")
    } else {
      Seq("2.11.12", "2.12.8", "2.13.0")
    }
  },
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-encoding", "UTF-8",
  ),
  // Don't include threeten on the binaries
  mappings in (Compile, packageBin) := (mappings in (Compile, packageBin)).value.filter { case (f, s) => !s.contains("threeten") },
  scalacOptions := {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, scalaMajor)) if scalaMajor >= 11 && scalaMajor <= 12 =>
        scalacOptions.value ++ Seq(
          "-deprecation:false",
          "-Xfatal-warnings",
          "-Yrangepos",
          "-unchecked",
          "-target:jvm-1.8")
      case Some((2, 13)) =>
        scalacOptions.value ++ Seq(
          "-deprecation:false",
          "-Xsource:2.13",
          "-Xfatal-warnings",
          "-Yrangepos",
          "-target:jvm-1.8")
      case Some((2, 10)) =>
        scalacOptions.value ++ Seq("-target:jvm-1.8")
    }
  },
  scalacOptions in (Compile, doc) := {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, scalaMajor)) if scalaMajor >= 11 =>
        Seq("-deprecation:false")
      case Some((2, 10)) =>
        Seq.empty
    }
  },
  javaOptions ++= Seq("-Dfile.encoding=UTF8"),
  autoAPIMappings := true,
  useGpg := true,

  publishArtifact in Test := false,
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra := pomData,
  pomIncludeRepository := { _ => false }
)

lazy val root = project.in(file("."))
  .settings(
    name                 := "scala-java-time-root",
    // No, SBT, we don't want any artifacts for root.
    // No, not even an empty jar.
    publish              := {},
    publishLocal         := {},
    publishArtifact      := false,
    Keys.`package`       := file(""))
  .settings(commonSettings: _*)
  .aggregate(scalajavatime.jvm, scalajavatime.js, scalajavatimeTZDBJVM, scalajavatimeTZDBJS, scalajavatimeTestsJVM, scalajavatimeTestsJVM)

/**
  * Copy source files and translate them to the java.time package
  */
def copyAndReplace(srcDirs: Seq[File], destinationDir: File): Seq[File] = {
  // Copy a directory and return the list of files
  def copyDirectory(source: File, target: File, overwrite: Boolean = false, preserveLastModified: Boolean = false): Set[File] =
    IO.copy(PathFinder(source).allPaths.pair(Path.rebase(source, target)).toTraversable, overwrite, preserveLastModified, false)

  val onlyScalaDirs = srcDirs.filter(_.getName.endsWith("scala"))
  // Copy the source files from the base project, exclude classes on java.util and dirs
  val generatedFiles: List[java.io.File] = onlyScalaDirs.foldLeft(Set.empty[File]) { (files, sourceDir) =>
    files ++ copyDirectory(sourceDir, destinationDir, overwrite = true)
  }.filterNot(_.isDirectory).filter(_.getName.endsWith(".scala")).filterNot(_.getParentFile.getName == "util").toList

  // These replacements will in practice rename all the classes from
  // org.threeten to java.time
  def replacements(line: String): String = {
    line
      .replaceAll("package org.threeten$", "package java")
      .replaceAll("package object bp", "package object time")
      .replaceAll("package org.threeten.bp", "package java.time")
      .replaceAll("""import org.threeten.bp(\..*)?(\.[A-Z_{][^\.]*)""", "import java.time$1$2")
      .replaceAll("import zonedb.threeten", "import zonedb.java")
      .replaceAll("private\\s*\\[bp\\]", "private[time]")
  }

  // Visit each file and read the content replacing key strings
  generatedFiles.foreach { f =>
    val replacedLines = IO.readLines(f).map(replacements)
    IO.writeLines(f, replacedLines)
  }
  generatedFiles
}

lazy val scalajavatime = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .in(file("core"))
  .settings(commonSettings: _*)
  .settings(
    name                 := "scala-java-time"
  )
  .jsSettings(
    scalacOptions ++= {
      val tagOrHash =
        if(isSnapshot.value) sys.process.Process("git rev-parse HEAD").lineStream_!.head
        else s"v${version.value}"
      (sourceDirectories in Compile).value.map { f =>
        val a = f.toURI.toString
        val g = "https://raw.githubusercontent.com/cquiroz/scala-java-time/" + tagOrHash + "/shared/src/main/scala/"
        s"-P:scalajs:mapSourceURI:$a->$g/"
      }
    },
    sourceGenerators in Compile += Def.task {
        val srcDirs = (sourceDirectories in Compile).value
        val destinationDir = (sourceManaged in Compile).value
        copyAndReplace(srcDirs, destinationDir)
      }.taskValue,
    libraryDependencies ++= Seq(
      "io.github.cquiroz" %%% "scala-java-locales" % "0.3.16-cldr35"
    )
  )

lazy val scalajavatimeTZDB = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .in(file("tzdb"))
  .settings(commonSettings)
  .settings(
    name    := "scala-java-time-tzdb",
    version := scalaTZDBVersion
  )
  .jsSettings(
    dbVersion := TzdbPlugin.Version(tzdbVersion),
    includeTTBP := true,
    sourceGenerators in Compile += Def.task {
      val srcDirs = (sourceManaged in Compile).value
      val destinationDir = (sourceManaged in Compile).value
      copyAndReplace(Seq(srcDirs), destinationDir)
    }.taskValue
  ).dependsOn(scalajavatime)

lazy val scalajavatimeTZDBJVM = scalajavatimeTZDB.jvm
lazy val scalajavatimeTZDBJS  = scalajavatimeTZDB.js.enablePlugins(TzdbPlugin)

lazy val scalajavatimeTests = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .in(file("tests"))
  .settings(commonSettings: _*)
  .settings(
    name                 := "scala-java-time-tests",
    // No, SBT, we don't want any artifacts for root.
    // No, not even an empty jar.
    publish              := {},
    publishLocal         := {},
    publishArtifact      := false,
    Keys.`package`       := file(""),
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, scalaMajor)) if scalaMajor == 13 =>
          Seq("org.scalatest" %%% "scalatest" % "3.1.0-SNAP12" % "test")
        case Some((2, scalaMajor)) if scalaMajor <= 12 && (scalaJSVersion.startsWith("0.6.")) =>
          Seq("org.scalatest" %%% "scalatest" % "3.0.7" % "test")
        case _ => Seq.empty
      }
    }
  )
  .jvmSettings(
    // Fork the JVM test to ensure that the custom flags are set
    fork in Test := true,
    baseDirectory in Test := baseDirectory.value.getParentFile,
    // Use CLDR provider for locales
    // https://docs.oracle.com/javase/8/docs/technotes/guides/intl/enhancements.8.html#cldr
    javaOptions in Test ++= Seq("-Duser.language=en", "-Duser.country=US", "-Djava.locale.providers=CLDR")
  ).jsSettings(
    parallelExecution in Test := false,
    sourceGenerators in Test += Def.task {
      val srcDirs = (sourceDirectories in Test).value
      val destinationDir = (sourceManaged in Test).value
      copyAndReplace(srcDirs, destinationDir)
    }.taskValue
  ).dependsOn(scalajavatime, scalajavatimeTZDB)

lazy val scalajavatimeTestsJVM = scalajavatimeTests.jvm
lazy val scalajavatimeTestsJS  = scalajavatimeTests.js

lazy val docs = project.in(file("docs")).dependsOn(scalajavatime.jvm, scalajavatime.js)
  .settings(commonSettings)
  .settings(name := "docs")
  .enablePlugins(MicrositesPlugin)
  .settings(
    micrositeName             := "scala-java-time",
    micrositeAuthor           := "Carlos Quiroz",
    micrositeGithubOwner      := "cquiroz",
    micrositeGithubRepo       := "scala-java-time",
    micrositeBaseUrl          := "/scala-java-time",
    //micrositeDocumentationUrl := "/scala-java-time/docs/",
    micrositeHighlightTheme   := "color-brewer"
  )

lazy val pomData =
  <developers>
    <developer>
      <id>cquiroz</id>
      <name>Carlos Quiroz</name>
      <url>https://github.com/cquiroz</url>
      <roles>
        <role>Project Lead (current Scala version)</role>
      </roles>
    </developer>
    <developer>
      <id>soc</id>
      <name>Simon Ochsenreither</name>
      <url>https://github.com/soc</url>
      <roles>
        <role>Project Lead (original Scala version)</role>
      </roles>
    </developer>
    <developer>
      <id>jodastephen</id>
      <name>Stephen Colebourne</name>
      <url>https://github.com/jodastephen</url>
      <roles>
        <role>Project Lead (original Java implementation)</role>
      </roles>
   </developer>
  </developers>
  <contributors>
    <contributor>
      <name>Javier Fernandez-Ivern</name>
      <url>https://github.com/ivern</url>
    </contributor>
    <contributor>
      <name>Martin Baker</name>
      <url>https://github.com/kemokid</url>
    </contributor>
    <contributor>
      <name>Keith Harris</name>
      <url>https://github.com/keithharris</url>
    </contributor>
    <contributor>
      <name>Ludovic Hochet</name>
      <url>https://github.com/lhochet</url>
    </contributor>
    <contributor>
      <name>Matias Irland</name>
      <url>https://github.com/matir91</url>
    </contributor>
    <contributor>
      <name>Pap Lorinc</name>
      <url>https://github.com/paplorinc</url>
    </contributor>
    <contributor>
      <name>Philippe Marschall</name>
      <url>https://github.com/marschall</url>
    </contributor>
    <contributor>
      <name>Michael Nascimento Santos</name>
      <url>https://github.com/sjmisterm</url>
    </contributor>
    <contributor>
      <name>Roger Riggs</name>
      <url>https://github.com/RogerRiggs</url>
    </contributor>
    <contributor>
      <name>Siebe Schaap</name>
      <url>https://github.com/sschaap</url>
    </contributor>
    <contributor>
      <name>Sherman Shen</name>
    </contributor>
    <contributor>
      <name>Pavel Malyshev</name>
      <url>https://github.com/pamalyshev</url>
    </contributor>
    <contributor>
      <name>Philipp Thuerwaechter</name>
      <url>https://github.com/pithu</url>
    </contributor>
  </contributors>
