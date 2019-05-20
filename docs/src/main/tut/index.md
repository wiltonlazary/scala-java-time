---
layout: home
title:  "scala-java-time"
section: "home"
---

## Scala Java-Time

[![Build Status](https://travis-ci.org/cquiroz/scala-java-time.svg?branch=master)](https://travis-ci.org/cquiroz/scala-java-time)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.cquiroz/scala-java-time_2.12.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.cquiroz/scala-java-time_2.12)
[![Scala.js](http://scala-js.org/assets/badges/scalajs-0.6.8.svg)](http://scala-js.org)

This project provides an implementation of the `java.time` package, a date and time library that was added in Java 8.
The implementation is based on the original BSD-licensed reference implementation (before it was contributed to OpenJDK).

#### Example

```tut:book
import java.time._

// always returns 2009-02-13T23:31:30
val fixedClock = Clock.fixed(Instant.ofEpochSecond(1234567890L), ZoneOffset.ofHours(0))
val date = LocalDateTime.now(fixedClock)

date.getMonth      == Month.FEBRUARY
date.getDayOfWeek  == DayOfWeek.FRIDAY
date.getDayOfMonth == 13
date.getHour       == 23

val tomorrow = date.plusDays(1)

val duration = Duration.between(date, tomorrow)
duration.toMinutes == 1440L

val period = Period.between(date.toLocalDate, tomorrow.toLocalDate)
period.get(temporal.ChronoUnit.DAYS) == 1L

val date1 = LocalDate.of(2001, 1, 31)
date1.plusMonths(1) == LocalDate.of(2001, 2, 28)
val date2 = date1.`with`(temporal.TemporalAdjusters.next(DayOfWeek.SUNDAY))
date2 == LocalDate.of(2001, 2, 4)

val offsetTime = OffsetTime.of(date.toLocalTime, ZoneOffset.ofHours(1))
offsetTime.isBefore(OffsetTime.of(date.toLocalTime, ZoneOffset.ofHours(0))) == true

val format1 = format.DateTimeFormatter.ofPattern("MMMM MM d HH mm ss EE EEEE yyyy G", java.util.Locale.GERMAN)
date.format(format1) == "Februar 02 13 23 31 30 Fr. Freitag 2009 n. Chr."

chrono.JapaneseDate.now(fixedClock).toString     == "Japanese Heisei 21-02-13"
chrono.ThaiBuddhistDate.now(fixedClock).toString == "ThaiBuddhist BE 2552-02-13"
chrono.MinguoDate.now(fixedClock).toString       == "Minguo ROC 98-02-13"
```

### Usage

The _scala-java-time_ library is currently available for Scala (JVM, version 8 and later) and Scala.js (JavaScript).
Both Scala 2.11 and Scala 2.12 (2.0.0-M8 and later) are supported.

To get started with SBT, add one of these dependencies:

* `libraryDependencies += "io.github.cquiroz" %% "scala-java-time" % "2.0.0-RC2"` (for Scala)
* `libraryDependencies += "io.github.cquiroz" %%% "scala-java-time" % "2.0.0-RC2"` (for Scala.js, [Scala.js plugin](http://www.scala-js.org/tutorial/basic/#sbt-setup) required)

To get the latest snapshots add the repo

```
resolvers +=
  Resolver.sonatypeRepo("snapshots")
```

and either:

* `libraryDependencies += "io.github.cquiroz" %% "scala-java-time" % "2.0.0-RC3-SNAPSHOT"` (for Scala)
* `libraryDependencies += "io.github.cquiroz" %%% "scala-java-time" % "2.0.0-RC3-SNAPSHOT"` (for Scala.js, [Scala.js plugin](http://www.scala-js.org/tutorial/basic/#sbt-setup) required)

### Time zones

#### No timezones

If you don't need to use timezones in your application you can just stop here. By default `UTC` will always be available but otherwise the library will work fine and produce the smallest possible js file

#### Time-zone data (JS) alternative I (All timezones)

The timezone for js is provided in a separate bundle which contains all time zones available from
[IANA Time Zone Database](https://www.iana.org/time-zones). To use them you need to add the following dependency

* `libraryDependencies += "io.github.cquiroz" %%% "scala-java-time-tzdb" % "2.0.0-RC2_2019a"` (for Scala.js, [Scala.js plugin](http://www.scala-js.org/tutorial/basic/#sbt-setup) required)

Note that the db is fairly large and due to the characteristics of the API it's not very ammenable to optimization
This database is published every now and then so it maybe old. For current version see the following section.

#### Time-zone data (JS) alternative II (Selective timezones)

It is possible to build a custom tz db for your application using [sbt-tzdb](https://github.com/cquiroz/sbt-tzdb). This has two big benefits:

* You can select exactly what version of tzdb to use. Eventually you can you use the latest always.
* You can filter the time zones relevant to your application. This can dramatically reduce your js size

To do that you need to:

* Add `sbt-tzdb` to your list of plugins (Note you need sbt 1.x)

```scala
addSbtPlugin("io.github.cquiroz" % "sbt-tzdb" % "0.3.2")
```

* Enable the plugin for your `Scala.js` project:

```scala
  .enablePlugins(TzdbPlugin)
```

or for cross projects, enable it **only** one the js side

```scala
lazy val lib = crossProject(JVMPlatform, JSPlatform)
  ...

lazy val libJVM = lib.jvm
lazy val libJS  = lib.js.enablePlugins(TzdbPlugin)
```

This will build a tzdb exactly as on step 1 with all timezones. However you can filter the zones you want, e.g. add to your build:

```scala
zonesFilter := {(z: String) => z == "America/Santiago" || z == "Pacific/Honolulu"},
```

#### Time-zone data (JVM)

The time-zone database is stored as a pre-compiled dat file that is included in the built jar.
The version of the time-zone data used is stored within the dat file (near the start).
Updating the time-zone database involves using the `TzdbZoneRulesCompiler` class
and re-compiling the jar file.
Pull requests with later versions of the dat file will be accepted.

#### Building

This project builds using sbt.
Run `sbt scalajavatimeTestsJVM/test` to run the test suite on the JVM and
`sbt scalajavatimeTestsJS/test` to run the test suite in JavaScript.

#### Contributing

We welcome all contributions, including ideas, suggestions, bug reports, bug fixes and code!
We are especially interested in contributions that tackle the following issues:

* Find ways to reduce the size of the library
* Implement missing methods

Have a look at the [issues](https://github.com/cquiroz/scala-java-time/issues) or [issues](https://github.com/soc/scala-java-time/issues) to find something to work on! Let us know if you need help!

#### Plans

##### 2.0

A stable release of 2.0 will be published with only `java.time` on its binary after RC3 is published.

#### FAQs

##### What's with the packages? the code uses `org.threeten.bp` but I use `java.time`

The original code was in the `org.threeten.bp` and that has been maintained, among other reasons, because writing
code on the `java` namespace tends to produce issues with the security controls of the JVM

Thus the code is on `org.threeten.bp` but during packaging the code is moved to `java.time` and `org.threeten.bp` is removed.

Most applications would use the `java.time` package mirroring the JVM

##### Is this project derived from OpenJDK?

No. This project is derived from the Reference Implementation previously hosted on GitHub.
That project had a BSD license, which has been preserved here.
Thus, this project is a fork of the original code before entry to OpenJDK.

##### What is the relation to [this](https://github.com/soc/scala-java-time/) project

This is a fork from the original [project](https://github.com/soc/scala-java-time/) aim to complete the API to work on Scala.js

##### Are there are differences with the Java Time API?

The project aims to be an exact port of the java time API to scala.
The only differences are classes not on the official java API but still present as private, e.g. `DateTimeTextProvider`
in the format package that have been moved to the `internal` package to ease compatibility across versions
