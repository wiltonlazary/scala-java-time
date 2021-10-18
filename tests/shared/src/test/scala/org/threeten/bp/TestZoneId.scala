/*
 * Copyright (c) 2007-present, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.threeten.bp

import java.util.Locale
import java.util.SimpleTimeZone
import java.util.TimeZone

import org.scalatest.funsuite.AnyFunSuite
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.TemporalAccessor
import org.threeten.bp.zone.ZoneOffsetTransition
import org.threeten.bp.zone.ZoneRules
import org.threeten.bp.zone.ZoneRulesException

/** Test ZoneId. */
object TestZoneId {
  private val ZONE_PARIS: ZoneId = ZoneId.of("Europe/Paris")
  val LATEST_TZDB: String        = "2010i"
  private val OVERLAP: Int       = 2
  private val GAP: Int           = 0
}

class TestZoneId extends AnyFunSuite with AssertionsHelper {
  test("systemDefault_unableToConvert_badFormat") {
    assertThrows[DateTimeException] {
      val current: TimeZone = TimeZone.getDefault
      try {
        TimeZone.setDefault(new SimpleTimeZone(127, "Something Weird"))
        ZoneId.systemDefault
      } finally TimeZone.setDefault(current)
    }
  }

  test("systemDefault_unableToConvert_unknownId") {
    assertThrows[ZoneRulesException] {
      val current: TimeZone = TimeZone.getDefault
      try {
        TimeZone.setDefault(new SimpleTimeZone(127, "SomethingWeird"))
        ZoneId.systemDefault
      } finally TimeZone.setDefault(current)
    }
  }

  test("constant_UTC") {
    val test: ZoneId = ZoneOffset.UTC
    assertEquals(test.getId, "Z")
    assertEquals(test.getDisplayName(TextStyle.FULL, Locale.UK), "Z")
    assertEquals(test.getRules.isFixedOffset, true)
    assertEquals(test.getRules.getOffset(Instant.ofEpochSecond(0L)), ZoneOffset.UTC)
    checkOffset(test.getRules, createLDT(2008, 6, 30), ZoneOffset.UTC, 1)
  }

  test("constant_SHORT_IDS") {
    val ids: java.util.Map[String, String] = ZoneId.SHORT_IDS
    assertEquals(ids.get("EST"), "-05:00")
    assertEquals(ids.get("MST"), "-07:00")
    assertEquals(ids.get("HST"), "-10:00")
    assertEquals(ids.get("ACT"), "Australia/Darwin")
    assertEquals(ids.get("AET"), "Australia/Sydney")
    assertEquals(ids.get("AGT"), "America/Argentina/Buenos_Aires")
    assertEquals(ids.get("ART"), "Africa/Cairo")
    assertEquals(ids.get("AST"), "America/Anchorage")
    assertEquals(ids.get("BET"), "America/Sao_Paulo")
    assertEquals(ids.get("BST"), "Asia/Dhaka")
    assertEquals(ids.get("CAT"), "Africa/Harare")
    assertEquals(ids.get("CNT"), "America/St_Johns")
    assertEquals(ids.get("CST"), "America/Chicago")
    assertEquals(ids.get("CTT"), "Asia/Shanghai")
    assertEquals(ids.get("EAT"), "Africa/Addis_Ababa")
    assertEquals(ids.get("ECT"), "Europe/Paris")
    assertEquals(ids.get("IET"), "America/Indiana/Indianapolis")
    assertEquals(ids.get("IST"), "Asia/Kolkata")
    assertEquals(ids.get("JST"), "Asia/Tokyo")
    assertEquals(ids.get("MIT"), "Pacific/Apia")
    assertEquals(ids.get("NET"), "Asia/Yerevan")
    assertEquals(ids.get("NST"), "Pacific/Auckland")
    assertEquals(ids.get("PLT"), "Asia/Karachi")
    assertEquals(ids.get("PNT"), "America/Phoenix")
    assertEquals(ids.get("PRT"), "America/Puerto_Rico")
    assertEquals(ids.get("PST"), "America/Los_Angeles")
    assertEquals(ids.get("SST"), "Pacific/Guadalcanal")
    assertEquals(ids.get("VST"), "Asia/Ho_Chi_Minh")
  }

  test("constant_SHORT_IDS_immutable") {
    assertThrows[UnsupportedOperationException] {
      val ids: java.util.Map[String, String] = ZoneId.SHORT_IDS
      ids.clear()
    }
  }

  test("systemDefault") {
    val test: ZoneId = ZoneId.systemDefault
    assertEquals(test.getId, TimeZone.getDefault.getID)
  }

  def test_of_string_Map(): Unit = {
    val map: java.util.Map[String, String] = new java.util.HashMap[String, String]
    map.put("LONDON", "Europe/London")
    map.put("PARIS", "Europe/Paris")
    val test: ZoneId                       = ZoneId.of("LONDON", map)
    assertEquals(test.getId, "Europe/London")
  }

  def test_of_string_Map_lookThrough(): Unit = {
    val map: java.util.Map[String, String] = new java.util.HashMap[String, String]
    map.put("LONDON", "Europe/London")
    map.put("PARIS", "Europe/Paris")
    val test: ZoneId                       = ZoneId.of("Europe/Madrid", map)
    assertEquals(test.getId, "Europe/Madrid")
  }

  def test_of_string_Map_emptyMap(): Unit = {
    val map: java.util.Map[String, String] = new java.util.HashMap[String, String]
    val test: ZoneId                       = ZoneId.of("Europe/Madrid", map)
    assertEquals(test.getId, "Europe/Madrid")
  }

  test("of_string_Map_badFormat") {
    assertThrows[DateTimeException] {
      val map: java.util.Map[String, String] = new java.util.HashMap[String, String]
      ZoneId.of("Not known", map)
    }
  }

  test("of_string_Map_unknown") {
    assertThrows[ZoneRulesException] {
      val map: java.util.Map[String, String] = new java.util.HashMap[String, String]
      ZoneId.of("Unknown", map)
    }
  }

  val data_of_string_UTC: List[String] =
    List("",
         "+00",
         "+0000",
         "+00:00",
         "+000000",
         "+00:00:00",
         "-00",
         "-0000",
         "-00:00",
         "-000000",
         "-00:00:00"
    )

  test("of_string_UTC") {
    data_of_string_UTC.foreach { id =>
      val test: ZoneId = ZoneId.of("UTC" + id)
      assertEquals(test.getId, "UTC")
      assertEquals(test.normalized, ZoneOffset.UTC)
    }
  }

  test("of_string_GMT") {
    data_of_string_UTC.foreach { id =>
      val test: ZoneId = ZoneId.of("GMT" + id)
      assertEquals(test.getId, "GMT")
      assertEquals(test.normalized, ZoneOffset.UTC)
    }
  }

  test("of_string_UT") {
    data_of_string_UTC.foreach { id =>
      val test: ZoneId = ZoneId.of("UT" + id)
      assertEquals(test.getId, "UT")
      assertEquals(test.normalized, ZoneOffset.UTC)
    }
  }

  val data_of_string_Fixed: List[List[String]] =
    List(
      List("+0", ""),
      List("+5", "+05:00"),
      List("+01", "+01:00"),
      List("+0100", "+01:00"),
      List("+01:00", "+01:00"),
      List("+010000", "+01:00"),
      List("+01:00:00", "+01:00"),
      List("+12", "+12:00"),
      List("+1234", "+12:34"),
      List("+12:34", "+12:34"),
      List("+123456", "+12:34:56"),
      List("+12:34:56", "+12:34:56"),
      List("-02", "-02:00"),
      List("-5", "-05:00"),
      List("-0200", "-02:00"),
      List("-02:00", "-02:00"),
      List("-020000", "-02:00"),
      List("-02:00:00", "-02:00")
    )

  test("of_string_offset") {
    data_of_string_Fixed.foreach {
      case input :: id :: Nil =>
        val test: ZoneId       = ZoneId.of(input)
        val offset: ZoneOffset = ZoneOffset.of(if (id.isEmpty) "Z" else id)
        assertEquals(test, offset)
      case _                  => fail()
    }
  }

  test("of_string_FixedUTC") {
    data_of_string_Fixed.foreach {
      case input :: id :: Nil =>
        val test: ZoneId       = ZoneId.of("UTC" + input)
        assertEquals(test.getId, "UTC" + id)
        assertEquals(test.getDisplayName(TextStyle.FULL, Locale.UK), "UTC" + id)
        assertEquals(test.getRules.isFixedOffset, true)
        val offset: ZoneOffset = ZoneOffset.of(if (id.isEmpty) "Z" else id)
        assertEquals(test.getRules.getOffset(Instant.ofEpochSecond(0L)), offset)
        checkOffset(test.getRules, createLDT(2008, 6, 30), offset, 1)
      case _                  => fail()
    }
  }

  test("of_string_FixedGMT") {
    data_of_string_Fixed.foreach {
      case input :: id :: Nil =>
        val test: ZoneId       = ZoneId.of("GMT" + input)
        assertEquals(test.getId, "GMT" + id)
        assertEquals(test.getDisplayName(TextStyle.FULL, Locale.UK), "GMT" + id)
        assertEquals(test.getRules.isFixedOffset, true)
        val offset: ZoneOffset = ZoneOffset.of(if (id.isEmpty) "Z" else id)
        assertEquals(test.getRules.getOffset(Instant.ofEpochSecond(0L)), offset)
        checkOffset(test.getRules, createLDT(2008, 6, 30), offset, 1)
      case _                  => fail()
    }
  }

  test("of_string_FixedUT") {
    data_of_string_Fixed.foreach {
      case input :: id :: Nil =>
        val test: ZoneId       = ZoneId.of("UT" + input)
        assertEquals(test.getId, "UT" + id)
        assertEquals(test.getDisplayName(TextStyle.FULL, Locale.UK), "UT" + id)
        assertEquals(test.getRules.isFixedOffset, true)
        val offset: ZoneOffset = ZoneOffset.of(if (id.isEmpty) "Z" else id)
        assertEquals(test.getRules.getOffset(Instant.ofEpochSecond(0L)), offset)
        checkOffset(test.getRules, createLDT(2008, 6, 30), offset, 1)
      case _                  => fail()
    }
  }

  val data_of_string_UTC_invalid: List[String] =
    List(
      "A",
      "B",
      "C",
      "D",
      "E",
      "F",
      "G",
      "H",
      "I",
      "J",
      "K",
      "L",
      "M",
      "N",
      "O",
      "P",
      "Q",
      "R",
      "S",
      "T",
      "U",
      "V",
      "W",
      "X",
      "Y",
      "+0:00",
      "+00:0",
      "+0:0",
      "+000",
      "+00000",
      "+0:00:00",
      "+00:0:00",
      "+00:00:0",
      "+0:0:0",
      "+0:0:00",
      "+00:0:0",
      "+0:00:0",
      "+01_00",
      "+01;00",
      "+01@00",
      "+01:AA",
      "+19",
      "+19:00",
      "+18:01",
      "+18:00:01",
      "+1801",
      "+180001",
      "-0:00",
      "-00:0",
      "-0:0",
      "-000",
      "-00000",
      "-0:00:00",
      "-00:0:00",
      "-00:00:0",
      "-0:0:0",
      "-0:0:00",
      "-00:0:0",
      "-0:00:0",
      "-19",
      "-19:00",
      "-18:01",
      "-18:00:01",
      "-1801",
      "-180001",
      "-01_00",
      "-01;00",
      "-01@00",
      "-01:AA",
      "@01:00"
    )

  test("of_string_UTC_invalid") {
    data_of_string_UTC_invalid.foreach { id =>
      assertThrows[DateTimeException] {
        ZoneId.of("UTC" + id)
      }
    }
  }

  test("unknown id zone") {
    val caught = intercept[DateTimeException] {
      ZoneId.of("X")
    }
    assert(caught.getMessage == "Invalid ID for ZoneOffset, invalid format: X")
  }

  test("of_string_GMT_invalid") {
    data_of_string_UTC_invalid.foreach { id =>
      assertThrows[DateTimeException] {
        ZoneId.of("GMT" + id)
      }
    }
  }

  val data_of_string_invalid: List[String] =
    List(
      "",
      ":",
      "#",
      "\u00ef",
      "`",
      "!",
      "\"",
      "\u00ef",
      "$",
      "^",
      "&",
      "*",
      "(",
      ")",
      "=",
      "\\",
      "|",
      ",",
      "<",
      ">",
      "?",
      ";",
      "'",
      "[",
      "]",
      "{",
      "}",
      "\u00ef:A",
      "`:A",
      "!:A",
      "\":A",
      "\u00ef:A",
      "$:A",
      "^:A",
      "&:A",
      "*:A",
      "(:A",
      "):A",
      "=:A",
      "+:A",
      "\\:A",
      "|:A",
      ",:A",
      "<:A",
      ">:A",
      "?:A",
      ";:A",
      "::A",
      "':A",
      "@:A",
      "~:A",
      "[:A",
      "]:A",
      "{:A",
      "}:A",
      "A:B#\u00ef",
      "A:B#`",
      "A:B#!",
      "A:B#\"",
      "A:B#\u00ef",
      "A:B#$",
      "A:B#^",
      "A:B#&",
      "A:B#*",
      "A:B#(",
      "A:B#)",
      "A:B#=",
      "A:B#+",
      "A:B#\\",
      "A:B#|",
      "A:B#,",
      "A:B#<",
      "A:B#>",
      "A:B#?",
      "A:B#;",
      "A:B#:",
      "A:B#'",
      "A:B#@",
      "A:B#~",
      "A:B#[",
      "A:B#]",
      "A:B#{",
      "A:B#}"
    )

  test("of_string_invalid") {
    data_of_string_invalid.foreach { id =>
      assertThrows[DateTimeException] {
        ZoneId.of(id)
      }
    }
  }

  test("of_string_GMT0") {
    val test: ZoneId = ZoneId.of("GMT0")
    assertEquals(test.getId, "GMT0")
    assertEquals(test.getRules.isFixedOffset, true)
    assertEquals(test.normalized, ZoneOffset.UTC)
  }

  test("of_string_London") {
    val test: ZoneId = ZoneId.of("Europe/London")
    assertEquals(test.getId, "Europe/London")
    assertEquals(test.getRules.isFixedOffset, false)
  }

  test("of_string_null") {
    assertThrows[NullPointerException] {
      ZoneId.of(null.asInstanceOf[String])
    }
  }

  test("of_string_unknown_simple") {
    assertThrows[ZoneRulesException] {
      ZoneId.of("Unknown")
    }
  }

  test("factory_CalendricalObject") {
    assertEquals(ZoneId.from(createZDT(2007, 7, 15, 17, 30, 0, 0, TestZoneId.ZONE_PARIS)),
                 TestZoneId.ZONE_PARIS
    )
  }

  test("factory_CalendricalObject_invalid_noDerive") {
    assertThrows[DateTimeException] {
      ZoneId.from(LocalTime.of(12, 30))
    }
  }

  test("factory_CalendricalObject_null") {
    assertThrows[Platform.NPE] {
      ZoneId.from(null.asInstanceOf[TemporalAccessor])
    }
  }

  test("London") {
    val test: ZoneId = ZoneId.of("Europe/London")
    assertEquals(test.getId, "Europe/London")
    assertEquals(test.getRules.isFixedOffset, false)
  }

  test("London_getOffset") {
    val test: ZoneId = ZoneId.of("Europe/London")
    assertEquals(test.getRules.getOffset(createInstant(2008, 1, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(0)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 2, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(0)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(0)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 4, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 5, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 6, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 7, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 8, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 9, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 10, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 11, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(0)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 12, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(0)
    )
  }

  test("London_getOffset_toDST") {
    val test: ZoneId = ZoneId.of("Europe/London")
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 24, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(0)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 25, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(0)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 26, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(0)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 27, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(0)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 28, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(0)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 29, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(0)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 30, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(0)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 31, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(
      test.getRules.getOffset(createInstant(2008, 3, 30, 0, 59, 59, 999999999, ZoneOffset.UTC)),
      ZoneOffset.ofHours(0)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 30, 1, 0, 0, 0, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
  }

  test("London_getOffset_fromDST") {
    val test: ZoneId = ZoneId.of("Europe/London")
    assertEquals(test.getRules.getOffset(createInstant(2008, 10, 24, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 10, 25, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 10, 26, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 10, 27, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(0)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 10, 28, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(0)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 10, 29, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(0)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 10, 30, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(0)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 10, 31, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(0)
    )
    assertEquals(
      test.getRules.getOffset(createInstant(2008, 10, 26, 0, 59, 59, 999999999, ZoneOffset.UTC)),
      ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 10, 26, 1, 0, 0, 0, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(0)
    )
  }

  test("London_getOffsetInfo") {
    val test: ZoneId = ZoneId.of("Europe/London")
    checkOffset(test.getRules, createLDT(2008, 1, 1), ZoneOffset.ofHours(0), 1)
    checkOffset(test.getRules, createLDT(2008, 2, 1), ZoneOffset.ofHours(0), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 1), ZoneOffset.ofHours(0), 1)
    checkOffset(test.getRules, createLDT(2008, 4, 1), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 5, 1), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 6, 1), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 7, 1), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 8, 1), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 9, 1), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 10, 1), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 11, 1), ZoneOffset.ofHours(0), 1)
    checkOffset(test.getRules, createLDT(2008, 12, 1), ZoneOffset.ofHours(0), 1)
  }

  test("London_getOffsetInfo_toDST") {
    val test: ZoneId = ZoneId.of("Europe/London")
    checkOffset(test.getRules, createLDT(2008, 3, 24), ZoneOffset.ofHours(0), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 25), ZoneOffset.ofHours(0), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 26), ZoneOffset.ofHours(0), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 27), ZoneOffset.ofHours(0), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 28), ZoneOffset.ofHours(0), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 29), ZoneOffset.ofHours(0), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 30), ZoneOffset.ofHours(0), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 31), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules,
                LocalDateTime.of(2008, 3, 30, 0, 59, 59, 999999999),
                ZoneOffset.ofHours(0),
                1
    )
    checkOffset(test.getRules,
                LocalDateTime.of(2008, 3, 30, 1, 30, 0, 0),
                ZoneOffset.ofHours(0),
                TestZoneId.GAP
    )
    checkOffset(test.getRules, LocalDateTime.of(2008, 3, 30, 2, 0, 0, 0), ZoneOffset.ofHours(1), 1)
  }

  test("London_getOffsetInfo_fromDST") {
    val test: ZoneId = ZoneId.of("Europe/London")
    checkOffset(test.getRules, createLDT(2008, 10, 24), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 10, 25), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 10, 26), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 10, 27), ZoneOffset.ofHours(0), 1)
    checkOffset(test.getRules, createLDT(2008, 10, 28), ZoneOffset.ofHours(0), 1)
    checkOffset(test.getRules, createLDT(2008, 10, 29), ZoneOffset.ofHours(0), 1)
    checkOffset(test.getRules, createLDT(2008, 10, 30), ZoneOffset.ofHours(0), 1)
    checkOffset(test.getRules, createLDT(2008, 10, 31), ZoneOffset.ofHours(0), 1)
    checkOffset(test.getRules,
                LocalDateTime.of(2008, 10, 26, 0, 59, 59, 999999999),
                ZoneOffset.ofHours(1),
                1
    )
    checkOffset(test.getRules,
                LocalDateTime.of(2008, 10, 26, 1, 30, 0, 0),
                ZoneOffset.ofHours(1),
                TestZoneId.OVERLAP
    )
    checkOffset(test.getRules, LocalDateTime.of(2008, 10, 26, 2, 0, 0, 0), ZoneOffset.ofHours(0), 1)
  }

  test("London_getOffsetInfo_gap") {
    val test: ZoneId                     = ZoneId.of("Europe/London")
    val dateTime: LocalDateTime          = LocalDateTime.of(2008, 3, 30, 1, 0, 0, 0)
    val trans: ZoneOffsetTransition      =
      checkOffset(test.getRules, dateTime, ZoneOffset.ofHours(0), TestZoneId.GAP)
    assertEquals(trans.isGap, true)
    assertEquals(trans.isOverlap, false)
    assertEquals(trans.getOffsetBefore, ZoneOffset.ofHours(0))
    assertEquals(trans.getOffsetAfter, ZoneOffset.ofHours(1))
    assertEquals(trans.getInstant, dateTime.toInstant(ZoneOffset.UTC))
    assertEquals(trans.getDateTimeBefore, LocalDateTime.of(2008, 3, 30, 1, 0))
    assertEquals(trans.getDateTimeAfter, LocalDateTime.of(2008, 3, 30, 2, 0))
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(-1)), false)
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(0)), false)
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(1)), false)
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(2)), false)
    assertEquals(trans.toString, "Transition[Gap at 2008-03-30T01:00Z to +01:00]")
    assertFalse(trans == null)
    assertNotEquals(trans, ZoneOffset.ofHours(0))
    assertTrue(trans == trans)
    val otherTrans: ZoneOffsetTransition = test.getRules.getTransition(dateTime)
    assertTrue(trans == otherTrans)
    assertEquals(trans.hashCode, otherTrans.hashCode)
  }

  test("London_getOffsetInfo_overlap") {
    val test: ZoneId                     = ZoneId.of("Europe/London")
    val dateTime: LocalDateTime          = LocalDateTime.of(2008, 10, 26, 1, 0, 0, 0)
    val trans: ZoneOffsetTransition      =
      checkOffset(test.getRules, dateTime, ZoneOffset.ofHours(1), TestZoneId.OVERLAP)
    assertEquals(trans.isGap, false)
    assertEquals(trans.isOverlap, true)
    assertEquals(trans.getOffsetBefore, ZoneOffset.ofHours(1))
    assertEquals(trans.getOffsetAfter, ZoneOffset.ofHours(0))
    assertEquals(trans.getInstant, dateTime.toInstant(ZoneOffset.UTC))
    assertEquals(trans.getDateTimeBefore, LocalDateTime.of(2008, 10, 26, 2, 0))
    assertEquals(trans.getDateTimeAfter, LocalDateTime.of(2008, 10, 26, 1, 0))
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(-1)), false)
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(0)), true)
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(1)), true)
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(2)), false)
    assertEquals(trans.toString, "Transition[Overlap at 2008-10-26T02:00+01:00 to Z]")
    assertFalse(trans == null)
    assertNotEquals(trans, ZoneOffset.ofHours(1))
    assertTrue(trans == trans)
    val otherTrans: ZoneOffsetTransition = test.getRules.getTransition(dateTime)
    assertTrue(trans == otherTrans)
    assertEquals(trans.hashCode, otherTrans.hashCode)
  }

  test("Paris") {
    val test: ZoneId = ZoneId.of("Europe/Paris")
    assertEquals(test.getId, "Europe/Paris")
    assertEquals(test.getRules.isFixedOffset, false)
  }

  test("Paris_getOffset") {
    val test: ZoneId = ZoneId.of("Europe/Paris")
    assertEquals(test.getRules.getOffset(createInstant(2008, 1, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 2, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 4, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(2)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 5, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(2)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 6, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(2)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 7, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(2)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 8, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(2)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 9, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(2)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 10, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(2)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 11, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 12, 1, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
  }

  test("Paris_getOffset_toDST") {
    val test: ZoneId = ZoneId.of("Europe/Paris")
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 24, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 25, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 26, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 27, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 28, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 29, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 30, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 31, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(2)
    )
    assertEquals(
      test.getRules.getOffset(createInstant(2008, 3, 30, 0, 59, 59, 999999999, ZoneOffset.UTC)),
      ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 30, 1, 0, 0, 0, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(2)
    )
  }

  test("Paris_getOffset_fromDST") {
    val test: ZoneId = ZoneId.of("Europe/Paris")
    assertEquals(test.getRules.getOffset(createInstant(2008, 10, 24, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(2)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 10, 25, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(2)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 10, 26, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(2)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 10, 27, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 10, 28, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 10, 29, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 10, 30, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 10, 31, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
    assertEquals(
      test.getRules.getOffset(createInstant(2008, 10, 26, 0, 59, 59, 999999999, ZoneOffset.UTC)),
      ZoneOffset.ofHours(2)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 10, 26, 1, 0, 0, 0, ZoneOffset.UTC)),
                 ZoneOffset.ofHours(1)
    )
  }

  test("Paris_getOffsetInfo") {
    val test: ZoneId = ZoneId.of("Europe/Paris")
    checkOffset(test.getRules, createLDT(2008, 1, 1), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 2, 1), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 1), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 4, 1), ZoneOffset.ofHours(2), 1)
    checkOffset(test.getRules, createLDT(2008, 5, 1), ZoneOffset.ofHours(2), 1)
    checkOffset(test.getRules, createLDT(2008, 6, 1), ZoneOffset.ofHours(2), 1)
    checkOffset(test.getRules, createLDT(2008, 7, 1), ZoneOffset.ofHours(2), 1)
    checkOffset(test.getRules, createLDT(2008, 8, 1), ZoneOffset.ofHours(2), 1)
    checkOffset(test.getRules, createLDT(2008, 9, 1), ZoneOffset.ofHours(2), 1)
    checkOffset(test.getRules, createLDT(2008, 10, 1), ZoneOffset.ofHours(2), 1)
    checkOffset(test.getRules, createLDT(2008, 11, 1), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 12, 1), ZoneOffset.ofHours(1), 1)
  }

  test("Paris_getOffsetInfo_toDST") {
    val test: ZoneId = ZoneId.of("Europe/Paris")
    checkOffset(test.getRules, createLDT(2008, 3, 24), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 25), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 26), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 27), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 28), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 29), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 30), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 31), ZoneOffset.ofHours(2), 1)
    checkOffset(test.getRules,
                LocalDateTime.of(2008, 3, 30, 1, 59, 59, 999999999),
                ZoneOffset.ofHours(1),
                1
    )
    checkOffset(test.getRules,
                LocalDateTime.of(2008, 3, 30, 2, 30, 0, 0),
                ZoneOffset.ofHours(1),
                TestZoneId.GAP
    )
    checkOffset(test.getRules, LocalDateTime.of(2008, 3, 30, 3, 0, 0, 0), ZoneOffset.ofHours(2), 1)
  }

  test("Paris_getOffsetInfo_fromDST") {
    val test: ZoneId = ZoneId.of("Europe/Paris")
    checkOffset(test.getRules, createLDT(2008, 10, 24), ZoneOffset.ofHours(2), 1)
    checkOffset(test.getRules, createLDT(2008, 10, 25), ZoneOffset.ofHours(2), 1)
    checkOffset(test.getRules, createLDT(2008, 10, 26), ZoneOffset.ofHours(2), 1)
    checkOffset(test.getRules, createLDT(2008, 10, 27), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 10, 28), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 10, 29), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 10, 30), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules, createLDT(2008, 10, 31), ZoneOffset.ofHours(1), 1)
    checkOffset(test.getRules,
                LocalDateTime.of(2008, 10, 26, 1, 59, 59, 999999999),
                ZoneOffset.ofHours(2),
                1
    )
    checkOffset(test.getRules,
                LocalDateTime.of(2008, 10, 26, 2, 30, 0, 0),
                ZoneOffset.ofHours(2),
                TestZoneId.OVERLAP
    )
    checkOffset(test.getRules, LocalDateTime.of(2008, 10, 26, 3, 0, 0, 0), ZoneOffset.ofHours(1), 1)
  }

  test("Paris_getOffsetInfo_gap") {
    val test: ZoneId                   = ZoneId.of("Europe/Paris")
    val dateTime: LocalDateTime        = LocalDateTime.of(2008, 3, 30, 2, 0, 0, 0)
    val trans: ZoneOffsetTransition    =
      checkOffset(test.getRules, dateTime, ZoneOffset.ofHours(1), TestZoneId.GAP)
    assertEquals(trans.isGap, true)
    assertEquals(trans.isOverlap, false)
    assertEquals(trans.getOffsetBefore, ZoneOffset.ofHours(1))
    assertEquals(trans.getOffsetAfter, ZoneOffset.ofHours(2))
    assertEquals(trans.getInstant, createInstant(2008, 3, 30, 1, 0, 0, 0, ZoneOffset.UTC))
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(0)), false)
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(1)), false)
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(2)), false)
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(3)), false)
    assertEquals(trans.toString, "Transition[Gap at 2008-03-30T02:00+01:00 to +02:00]")
    assertFalse(trans == null)
    assertNotEquals(trans, ZoneOffset.ofHours(1))
    assertTrue(trans == trans)
    val otherDis: ZoneOffsetTransition = test.getRules.getTransition(dateTime)
    assertTrue(trans == otherDis)
    assertEquals(trans.hashCode, otherDis.hashCode)
  }

  test("Paris_getOffsetInfo_overlap") {
    val test: ZoneId                   = ZoneId.of("Europe/Paris")
    val dateTime: LocalDateTime        = LocalDateTime.of(2008, 10, 26, 2, 0, 0, 0)
    val trans: ZoneOffsetTransition    =
      checkOffset(test.getRules, dateTime, ZoneOffset.ofHours(2), TestZoneId.OVERLAP)
    assertEquals(trans.isGap, false)
    assertEquals(trans.isOverlap, true)
    assertEquals(trans.getOffsetBefore, ZoneOffset.ofHours(2))
    assertEquals(trans.getOffsetAfter, ZoneOffset.ofHours(1))
    assertEquals(trans.getInstant, createInstant(2008, 10, 26, 1, 0, 0, 0, ZoneOffset.UTC))
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(0)), false)
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(1)), true)
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(2)), true)
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(3)), false)
    assertEquals(trans.toString, "Transition[Overlap at 2008-10-26T03:00+02:00 to +01:00]")
    assertFalse(trans == null)
    assertNotEquals(trans, ZoneOffset.ofHours(2))
    assertTrue(trans == trans)
    val otherDis: ZoneOffsetTransition = test.getRules.getTransition(dateTime)
    assertTrue(trans == otherDis)
    assertEquals(trans.hashCode, otherDis.hashCode)
  }

  test("NewYork") {
    val test: ZoneId = ZoneId.of("America/New_York")
    assertEquals(test.getId, "America/New_York")
    assertEquals(test.getRules.isFixedOffset, false)
  }

  test("NewYork_getOffset") {
    val test: ZoneId       = ZoneId.of("America/New_York")
    val offset: ZoneOffset = ZoneOffset.ofHours(-5)
    assertEquals(test.getRules.getOffset(createInstant(2008, 1, 1, offset)), ZoneOffset.ofHours(-5))
    assertEquals(test.getRules.getOffset(createInstant(2008, 2, 1, offset)), ZoneOffset.ofHours(-5))
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 1, offset)), ZoneOffset.ofHours(-5))
    assertEquals(test.getRules.getOffset(createInstant(2008, 4, 1, offset)), ZoneOffset.ofHours(-4))
    assertEquals(test.getRules.getOffset(createInstant(2008, 5, 1, offset)), ZoneOffset.ofHours(-4))
    assertEquals(test.getRules.getOffset(createInstant(2008, 6, 1, offset)), ZoneOffset.ofHours(-4))
    assertEquals(test.getRules.getOffset(createInstant(2008, 7, 1, offset)), ZoneOffset.ofHours(-4))
    assertEquals(test.getRules.getOffset(createInstant(2008, 8, 1, offset)), ZoneOffset.ofHours(-4))
    assertEquals(test.getRules.getOffset(createInstant(2008, 9, 1, offset)), ZoneOffset.ofHours(-4))
    assertEquals(test.getRules.getOffset(createInstant(2008, 10, 1, offset)),
                 ZoneOffset.ofHours(-4)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 11, 1, offset)),
                 ZoneOffset.ofHours(-4)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 12, 1, offset)),
                 ZoneOffset.ofHours(-5)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 1, 28, offset)),
                 ZoneOffset.ofHours(-5)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 2, 28, offset)),
                 ZoneOffset.ofHours(-5)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 28, offset)),
                 ZoneOffset.ofHours(-4)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 4, 28, offset)),
                 ZoneOffset.ofHours(-4)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 5, 28, offset)),
                 ZoneOffset.ofHours(-4)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 6, 28, offset)),
                 ZoneOffset.ofHours(-4)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 7, 28, offset)),
                 ZoneOffset.ofHours(-4)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 8, 28, offset)),
                 ZoneOffset.ofHours(-4)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 9, 28, offset)),
                 ZoneOffset.ofHours(-4)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 10, 28, offset)),
                 ZoneOffset.ofHours(-4)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 11, 28, offset)),
                 ZoneOffset.ofHours(-5)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 12, 28, offset)),
                 ZoneOffset.ofHours(-5)
    )
  }

  test("NewYork_getOffset_toDST") {
    val test: ZoneId       = ZoneId.of("America/New_York")
    val offset: ZoneOffset = ZoneOffset.ofHours(-5)
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 8, offset)), ZoneOffset.ofHours(-5))
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 9, offset)), ZoneOffset.ofHours(-5))
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 10, offset)),
                 ZoneOffset.ofHours(-4)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 11, offset)),
                 ZoneOffset.ofHours(-4)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 12, offset)),
                 ZoneOffset.ofHours(-4)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 13, offset)),
                 ZoneOffset.ofHours(-4)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 14, offset)),
                 ZoneOffset.ofHours(-4)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 9, 1, 59, 59, 999999999, offset)),
                 ZoneOffset.ofHours(-5)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 3, 9, 2, 0, 0, 0, offset)),
                 ZoneOffset.ofHours(-4)
    )
  }

  test("NewYork_getOffset_fromDST") {
    val test: ZoneId       = ZoneId.of("America/New_York")
    val offset: ZoneOffset = ZoneOffset.ofHours(-4)
    assertEquals(test.getRules.getOffset(createInstant(2008, 11, 1, offset)),
                 ZoneOffset.ofHours(-4)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 11, 2, offset)),
                 ZoneOffset.ofHours(-4)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 11, 3, offset)),
                 ZoneOffset.ofHours(-5)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 11, 4, offset)),
                 ZoneOffset.ofHours(-5)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 11, 5, offset)),
                 ZoneOffset.ofHours(-5)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 11, 6, offset)),
                 ZoneOffset.ofHours(-5)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 11, 7, offset)),
                 ZoneOffset.ofHours(-5)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 11, 2, 1, 59, 59, 999999999, offset)),
                 ZoneOffset.ofHours(-4)
    )
    assertEquals(test.getRules.getOffset(createInstant(2008, 11, 2, 2, 0, 0, 0, offset)),
                 ZoneOffset.ofHours(-5)
    )
  }

  test("NewYork_getOffsetInfo") {
    val test: ZoneId = ZoneId.of("America/New_York")
    checkOffset(test.getRules, createLDT(2008, 1, 1), ZoneOffset.ofHours(-5), 1)
    checkOffset(test.getRules, createLDT(2008, 2, 1), ZoneOffset.ofHours(-5), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 1), ZoneOffset.ofHours(-5), 1)
    checkOffset(test.getRules, createLDT(2008, 4, 1), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules, createLDT(2008, 5, 1), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules, createLDT(2008, 6, 1), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules, createLDT(2008, 7, 1), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules, createLDT(2008, 8, 1), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules, createLDT(2008, 9, 1), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules, createLDT(2008, 10, 1), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules, createLDT(2008, 11, 1), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules, createLDT(2008, 12, 1), ZoneOffset.ofHours(-5), 1)
    checkOffset(test.getRules, createLDT(2008, 1, 28), ZoneOffset.ofHours(-5), 1)
    checkOffset(test.getRules, createLDT(2008, 2, 28), ZoneOffset.ofHours(-5), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 28), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules, createLDT(2008, 4, 28), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules, createLDT(2008, 5, 28), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules, createLDT(2008, 6, 28), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules, createLDT(2008, 7, 28), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules, createLDT(2008, 8, 28), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules, createLDT(2008, 9, 28), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules, createLDT(2008, 10, 28), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules, createLDT(2008, 11, 28), ZoneOffset.ofHours(-5), 1)
    checkOffset(test.getRules, createLDT(2008, 12, 28), ZoneOffset.ofHours(-5), 1)
  }

  test("NewYork_getOffsetInfo_toDST") {
    val test: ZoneId = ZoneId.of("America/New_York")
    checkOffset(test.getRules, createLDT(2008, 3, 8), ZoneOffset.ofHours(-5), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 9), ZoneOffset.ofHours(-5), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 10), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 11), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 12), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 13), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules, createLDT(2008, 3, 14), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules,
                LocalDateTime.of(2008, 3, 9, 1, 59, 59, 999999999),
                ZoneOffset.ofHours(-5),
                1
    )
    checkOffset(test.getRules,
                LocalDateTime.of(2008, 3, 9, 2, 30, 0, 0),
                ZoneOffset.ofHours(-5),
                TestZoneId.GAP
    )
    checkOffset(test.getRules, LocalDateTime.of(2008, 3, 9, 3, 0, 0, 0), ZoneOffset.ofHours(-4), 1)
  }

  test("NewYork_getOffsetInfo_fromDST") {
    val test: ZoneId = ZoneId.of("America/New_York")
    checkOffset(test.getRules, createLDT(2008, 11, 1), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules, createLDT(2008, 11, 2), ZoneOffset.ofHours(-4), 1)
    checkOffset(test.getRules, createLDT(2008, 11, 3), ZoneOffset.ofHours(-5), 1)
    checkOffset(test.getRules, createLDT(2008, 11, 4), ZoneOffset.ofHours(-5), 1)
    checkOffset(test.getRules, createLDT(2008, 11, 5), ZoneOffset.ofHours(-5), 1)
    checkOffset(test.getRules, createLDT(2008, 11, 6), ZoneOffset.ofHours(-5), 1)
    checkOffset(test.getRules, createLDT(2008, 11, 7), ZoneOffset.ofHours(-5), 1)
    checkOffset(test.getRules,
                LocalDateTime.of(2008, 11, 2, 0, 59, 59, 999999999),
                ZoneOffset.ofHours(-4),
                1
    )
    checkOffset(test.getRules,
                LocalDateTime.of(2008, 11, 2, 1, 30, 0, 0),
                ZoneOffset.ofHours(-4),
                TestZoneId.OVERLAP
    )
    checkOffset(test.getRules, LocalDateTime.of(2008, 11, 2, 2, 0, 0, 0), ZoneOffset.ofHours(-5), 1)
  }

  test("NewYork_getOffsetInfo_gap") {
    val test: ZoneId                     = ZoneId.of("America/New_York")
    val dateTime: LocalDateTime          = LocalDateTime.of(2008, 3, 9, 2, 0, 0, 0)
    val trans: ZoneOffsetTransition      =
      checkOffset(test.getRules, dateTime, ZoneOffset.ofHours(-5), TestZoneId.GAP)
    assertEquals(trans.getOffsetBefore, ZoneOffset.ofHours(-5))
    assertEquals(trans.getOffsetAfter, ZoneOffset.ofHours(-4))
    assertEquals(trans.getInstant, createInstant(2008, 3, 9, 2, 0, 0, 0, ZoneOffset.ofHours(-5)))
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(-6)), false)
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(-5)), false)
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(-4)), false)
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(-3)), false)
    assertEquals(trans.toString, "Transition[Gap at 2008-03-09T02:00-05:00 to -04:00]")
    assertFalse(trans == null)
    assertNotEquals(trans, ZoneOffset.ofHours(-5))
    assertTrue(trans == trans)
    val otherTrans: ZoneOffsetTransition = test.getRules.getTransition(dateTime)
    assertTrue(trans == otherTrans)
    assertEquals(trans.hashCode, otherTrans.hashCode)
  }

  test("NewYork_getOffsetInfo_overlap") {
    val test: ZoneId                     = ZoneId.of("America/New_York")
    val dateTime: LocalDateTime          = LocalDateTime.of(2008, 11, 2, 1, 0, 0, 0)
    val trans: ZoneOffsetTransition      =
      checkOffset(test.getRules, dateTime, ZoneOffset.ofHours(-4), TestZoneId.OVERLAP)
    assertEquals(trans.getOffsetBefore, ZoneOffset.ofHours(-4))
    assertEquals(trans.getOffsetAfter, ZoneOffset.ofHours(-5))
    assertEquals(trans.getInstant, createInstant(2008, 11, 2, 2, 0, 0, 0, ZoneOffset.ofHours(-4)))
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(-1)), false)
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(-5)), true)
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(-4)), true)
    assertEquals(trans.isValidOffset(ZoneOffset.ofHours(2)), false)
    assertEquals(trans.toString, "Transition[Overlap at 2008-11-02T02:00-04:00 to -05:00]")
    assertFalse(trans == null)
    assertNotEquals(trans, ZoneOffset.ofHours(-4))
    assertTrue(trans == trans)
    val otherTrans: ZoneOffsetTransition = test.getRules.getTransition(dateTime)
    assertTrue(trans == otherTrans)
    assertEquals(trans.hashCode, otherTrans.hashCode)
  }

  test("get_Tzdb") {
    val test: ZoneId = ZoneId.of("Europe/London")
    assertEquals(test.getId, "Europe/London")
    assertEquals(test.getRules.isFixedOffset, false)
  }

  test("get_TzdbFixed") {
    val test: ZoneId = ZoneId.of("+01:30")
    assertEquals(test.getId, "+01:30")
    assertEquals(test.getRules.isFixedOffset, true)
  }

  test("equals") {
    val test1: ZoneId  = ZoneId.of("Europe/London")
    val test2: ZoneId  = ZoneId.of("Europe/Paris")
    val test2b: ZoneId = ZoneId.of("Europe/Paris")
    assertEquals(test1 == test2, false)
    assertEquals(test2 == test1, false)
    assertEquals(test1 == test1, true)
    assertEquals(test2 == test2, true)
    assertEquals(test2 == test2b, true)
    assertEquals(test1.hashCode == test1.hashCode, true)
    assertEquals(test2.hashCode == test2.hashCode, true)
    assertEquals(test2.hashCode == test2b.hashCode, true)
  }

  test("equals_null") {
    assertEquals(ZoneId.of("Europe/London") == null, false)
  }

  test("equals_notTimeZone") {
    assertNotEquals(ZoneId.of("Europe/London"), "Europe/London")
  }

  val data_toString: List[(String, String)] =
    List(
      ("Europe/London", "Europe/London"),
      ("Europe/Paris", "Europe/Paris"),
      ("Europe/Berlin", "Europe/Berlin"),
      ("Z", "Z"),
      ("UTC", "UTC"),
      ("UTC+01:00", "UTC+01:00"),
      ("GMT+01:00", "GMT+01:00"),
      ("UT+01:00", "UT+01:00")
    )

  test("toString") {
    data_toString.foreach {
      case (id: String, expected: String) =>
        val test: ZoneId = ZoneId.of(id)
        assertEquals(test.toString, expected)
      case _                              =>
        fail()
    }
  }

  private def createInstant(year: Int, month: Int, day: Int, offset: ZoneOffset): Instant =
    LocalDateTime.of(year, month, day, 0, 0).toInstant(offset)

  private def createInstant(
    year:   Int,
    month:  Int,
    day:    Int,
    hour:   Int,
    min:    Int,
    sec:    Int,
    nano:   Int,
    offset: ZoneOffset
  ): Instant =
    LocalDateTime.of(year, month, day, hour, min, sec, nano).toInstant(offset)

  private def createZDT(
    year:  Int,
    month: Int,
    day:   Int,
    hour:  Int,
    min:   Int,
    sec:   Int,
    nano:  Int,
    zone:  ZoneId
  ): ZonedDateTime =
    LocalDateTime.of(year, month, day, hour, min, sec, nano).atZone(zone)

  private def createLDT(year: Int, month: Int, day: Int): LocalDateTime                   =
    LocalDateTime.of(year, month, day, 0, 0)

  private def checkOffset(
    rules:    ZoneRules,
    dateTime: LocalDateTime,
    offset:   ZoneOffset,
    `type`:   Int
  ): ZoneOffsetTransition = {
    val validOffsets: java.util.List[ZoneOffset] = rules.getValidOffsets(dateTime)
    assertEquals(validOffsets.size, `type`)
    assertEquals(rules.getOffset(dateTime), offset)
    if (`type` == 1) {
      assertEquals(validOffsets.get(0), offset)
      null
    } else {
      val zot: ZoneOffsetTransition = rules.getTransition(dateTime)
      assertNotNull(zot)
      assertEquals(zot.isOverlap, `type` == 2)
      assertEquals(zot.isGap, `type` == 0)
      assertEquals(zot.isValidOffset(offset), `type` == 2)
      zot
    }
  }
}
