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
package org.threeten.bp.format

import org.scalatest.funsuite.AnyFunSuite
import org.threeten.bp.AssertionsHelper
import org.threeten.bp.Platform
import org.threeten.bp.temporal.ChronoField.OFFSET_SECONDS
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.internal.TTBPDateTimeFormatterBuilder
import org.threeten.bp.temporal.TemporalQueries

/** Test OffsetIdPrinterParser. */
class TestZoneOffsetParser extends AnyFunSuite with GenTestPrinterParser with AssertionsHelper {
  val data_error
    : List[(TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser, String, Int, Class[_])] = {
    List(
      (new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("Z", "+HH:MM:ss"),
       "hello",
       -1,
       classOf[Platform.DFE]),
      (new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("Z", "+HH:MM:ss"),
       "hello",
       6,
       classOf[Platform.DFE])
    )
  }

  test("test_parse_error") {
    data_error.foreach {
      case (pp, text, pos, expected) =>
        try {
          pp.parse(parseContext, text, pos)
        } catch {
          case ex: Throwable =>
            assertTrue(expected.isAssignableFrom(ex.getClass))
            assertEquals(parseContext.toParsed.query(TemporalQueries.chronology), null)
            assertEquals(parseContext.toParsed.query(TemporalQueries.zoneId), null)
        }
      case _ =>
        fail()
    }
  }

  test("test_parse_exactMatch_UTC") {
    val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
      new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("Z", "+HH:MM:ss")
    val result: Int = pp.parse(parseContext, "Z", 0)
    assertEquals(result, 1)
    assertParsed(ZoneOffset.UTC)
  }

  test("test_parse_startStringMatch_UTC") {
    val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
      new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("Z", "+HH:MM:ss")
    val result: Int = pp.parse(parseContext, "ZOTHER", 0)
    assertEquals(result, 1)
    assertParsed(ZoneOffset.UTC)
  }

  test("test_parse_midStringMatch_UTC") {
    val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
      new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("Z", "+HH:MM:ss")
    val result: Int = pp.parse(parseContext, "OTHERZOTHER", 5)
    assertEquals(result, 6)
    assertParsed(ZoneOffset.UTC)
  }

  test("test_parse_endStringMatch_UTC") {
    val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
      new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("Z", "+HH:MM:ss")
    val result: Int = pp.parse(parseContext, "OTHERZ", 5)
    assertEquals(result, 6)
    assertParsed(ZoneOffset.UTC)
  }

  test("test_parse_exactMatch_UTC_EmptyUTC") {
    val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
      new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("", "+HH:MM:ss")
    val result: Int = pp.parse(parseContext, "", 0)
    assertEquals(result, 0)
    assertParsed(ZoneOffset.UTC)
  }

  test("test_parse_startStringMatch_UTC_EmptyUTC") {
    val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
      new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("", "+HH:MM:ss")
    val result: Int = pp.parse(parseContext, "OTHER", 0)
    assertEquals(result, 0)
    assertParsed(ZoneOffset.UTC)
  }

  test("test_parse_midStringMatch_UTC_EmptyUTC") {
    val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
      new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("", "+HH:MM:ss")
    val result: Int = pp.parse(parseContext, "OTHEROTHER", 5)
    assertEquals(result, 5)
    assertParsed(ZoneOffset.UTC)
  }

  test("test_parse_endStringMatch_UTC_EmptyUTC") {
    val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
      new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("", "+HH:MM:ss")
    val result: Int = pp.parse(parseContext, "OTHER", 5)
    assertEquals(result, 5)
    assertParsed(ZoneOffset.UTC)
  }

  val provider_offsets: List[(String, String, ZoneOffset)] = {
    List(
      ("+HH", "-00", ZoneOffset.UTC),
      ("+HH", "+01", ZoneOffset.ofHours(1)),
      ("+HH", "-01", ZoneOffset.ofHours(-1)),
      ("+HHMM", "+0000", ZoneOffset.UTC),
      ("+HHMM", "-0000", ZoneOffset.UTC),
      ("+HHMM", "+0102", ZoneOffset.ofHoursMinutes(1, 2)),
      ("+HHMM", "-0102", ZoneOffset.ofHoursMinutes(-1, -2)),
      ("+HH:MM", "+00:00", ZoneOffset.UTC),
      ("+HH:MM", "-00:00", ZoneOffset.UTC),
      ("+HH:MM", "+01:02", ZoneOffset.ofHoursMinutes(1, 2)),
      ("+HH:MM", "-01:02", ZoneOffset.ofHoursMinutes(-1, -2)),
      ("+HHMMss", "+0000", ZoneOffset.UTC),
      ("+HHMMss", "-0000", ZoneOffset.UTC),
      ("+HHMMss", "+0100", ZoneOffset.ofHoursMinutesSeconds(1, 0, 0)),
      ("+HHMMss", "+0159", ZoneOffset.ofHoursMinutesSeconds(1, 59, 0)),
      ("+HHMMss", "+0200", ZoneOffset.ofHoursMinutesSeconds(2, 0, 0)),
      ("+HHMMss", "+1800", ZoneOffset.ofHoursMinutesSeconds(18, 0, 0)),
      ("+HHMMss", "+010215", ZoneOffset.ofHoursMinutesSeconds(1, 2, 15)),
      ("+HHMMss", "-0100", ZoneOffset.ofHoursMinutesSeconds(-1, 0, 0)),
      ("+HHMMss", "-0200", ZoneOffset.ofHoursMinutesSeconds(-2, 0, 0)),
      ("+HHMMss", "-1800", ZoneOffset.ofHoursMinutesSeconds(-18, 0, 0)),
      ("+HHMMss", "+000000", ZoneOffset.UTC),
      ("+HHMMss", "-000000", ZoneOffset.UTC),
      ("+HHMMss", "+010000", ZoneOffset.ofHoursMinutesSeconds(1, 0, 0)),
      ("+HHMMss", "+010203", ZoneOffset.ofHoursMinutesSeconds(1, 2, 3)),
      ("+HHMMss", "+015959", ZoneOffset.ofHoursMinutesSeconds(1, 59, 59)),
      ("+HHMMss", "+020000", ZoneOffset.ofHoursMinutesSeconds(2, 0, 0)),
      ("+HHMMss", "+180000", ZoneOffset.ofHoursMinutesSeconds(18, 0, 0)),
      ("+HHMMss", "-010000", ZoneOffset.ofHoursMinutesSeconds(-1, 0, 0)),
      ("+HHMMss", "-020000", ZoneOffset.ofHoursMinutesSeconds(-2, 0, 0)),
      ("+HHMMss", "-180000", ZoneOffset.ofHoursMinutesSeconds(-18, 0, 0)),
      ("+HH:MM:ss", "+00:00", ZoneOffset.UTC),
      ("+HH:MM:ss", "-00:00", ZoneOffset.UTC),
      ("+HH:MM:ss", "+01:00", ZoneOffset.ofHoursMinutesSeconds(1, 0, 0)),
      ("+HH:MM:ss", "+01:02", ZoneOffset.ofHoursMinutesSeconds(1, 2, 0)),
      ("+HH:MM:ss", "+01:59", ZoneOffset.ofHoursMinutesSeconds(1, 59, 0)),
      ("+HH:MM:ss", "+02:00", ZoneOffset.ofHoursMinutesSeconds(2, 0, 0)),
      ("+HH:MM:ss", "+18:00", ZoneOffset.ofHoursMinutesSeconds(18, 0, 0)),
      ("+HH:MM:ss", "+01:02:15", ZoneOffset.ofHoursMinutesSeconds(1, 2, 15)),
      ("+HH:MM:ss", "-01:00", ZoneOffset.ofHoursMinutesSeconds(-1, 0, 0)),
      ("+HH:MM:ss", "-02:00", ZoneOffset.ofHoursMinutesSeconds(-2, 0, 0)),
      ("+HH:MM:ss", "-18:00", ZoneOffset.ofHoursMinutesSeconds(-18, 0, 0)),
      ("+HH:MM:ss", "+00:00:00", ZoneOffset.UTC),
      ("+HH:MM:ss", "-00:00:00", ZoneOffset.UTC),
      ("+HH:MM:ss", "+01:00:00", ZoneOffset.ofHoursMinutesSeconds(1, 0, 0)),
      ("+HH:MM:ss", "+01:02:03", ZoneOffset.ofHoursMinutesSeconds(1, 2, 3)),
      ("+HH:MM:ss", "+01:59:59", ZoneOffset.ofHoursMinutesSeconds(1, 59, 59)),
      ("+HH:MM:ss", "+02:00:00", ZoneOffset.ofHoursMinutesSeconds(2, 0, 0)),
      ("+HH:MM:ss", "+18:00:00", ZoneOffset.ofHoursMinutesSeconds(18, 0, 0)),
      ("+HH:MM:ss", "-01:00:00", ZoneOffset.ofHoursMinutesSeconds(-1, 0, 0)),
      ("+HH:MM:ss", "-02:00:00", ZoneOffset.ofHoursMinutesSeconds(-2, 0, 0)),
      ("+HH:MM:ss", "-18:00:00", ZoneOffset.ofHoursMinutesSeconds(-18, 0, 0)),
      ("+HHMMSS", "+000000", ZoneOffset.UTC),
      ("+HHMMSS", "-000000", ZoneOffset.UTC),
      ("+HHMMSS", "+010203", ZoneOffset.ofHoursMinutesSeconds(1, 2, 3)),
      ("+HHMMSS", "-010203", ZoneOffset.ofHoursMinutesSeconds(-1, -2, -3)),
      ("+HH:MM:SS", "+00:00:00", ZoneOffset.UTC),
      ("+HH:MM:SS", "-00:00:00", ZoneOffset.UTC),
      ("+HH:MM:SS", "+01:02:03", ZoneOffset.ofHoursMinutesSeconds(1, 2, 3)),
      ("+HH:MM:SS", "-01:02:03", ZoneOffset.ofHoursMinutesSeconds(-1, -2, -3))
    )
  }

  test("test_parse_exactMatch") {
    provider_offsets.foreach {
      case (pattern, parse, expected) =>
        super.beforeEach()
        val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
          new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("Z", pattern)
        val result: Int = pp.parse(parseContext, parse, 0)
        assertEquals(result, parse.length)
        assertParsed(expected)
      case _ =>
        fail()
    }
  }

  test("test_parse_startStringMatch") {
    provider_offsets.foreach {
      case (pattern, parse, expected) =>
        super.beforeEach()
        val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
          new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("Z", pattern)
        val result: Int = pp.parse(parseContext, parse + ":OTHER", 0)
        assertEquals(result, parse.length)
        assertParsed(expected)
      case _ =>
        fail()
    }
  }

  test("test_parse_midStringMatch") {
    provider_offsets.foreach {
      case (pattern, parse, expected) =>
        super.beforeEach()
        val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
          new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("Z", pattern)
        val result: Int = pp.parse(parseContext, "OTHER" + parse + ":OTHER", 5)
        assertEquals(result, parse.length + 5)
        assertParsed(expected)
      case _ =>
        fail()
    }
  }

  test("test_parse_endStringMatch") {
    provider_offsets.foreach {
      case (pattern, parse, expected) =>
        super.beforeEach()
        val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
          new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("Z", pattern)
        val result: Int = pp.parse(parseContext, "OTHER" + parse, 5)
        assertEquals(result, parse.length + 5)
        assertParsed(expected)
      case _ =>
        fail()
    }
  }

  test("test_parse_exactMatch_EmptyUTC") {
    provider_offsets.foreach {
      case (pattern, parse, expected) =>
        super.beforeEach()
        val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
          new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("", pattern)
        val result: Int = pp.parse(parseContext, parse, 0)
        assertEquals(result, parse.length)
        assertParsed(expected)
      case _ =>
        fail()
    }
  }

  test("test_parse_startStringMatch_EmptyUTC") {
    provider_offsets.foreach {
      case (pattern, parse, expected) =>
        super.beforeEach()
        val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
          new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("", pattern)
        val result: Int = pp.parse(parseContext, parse + ":OTHER", 0)
        assertEquals(result, parse.length)
        assertParsed(expected)
      case _ =>
        fail()
    }
  }

  test("test_parse_midStringMatch_EmptyUTC") {
    provider_offsets.foreach {
      case (pattern, parse, expected) =>
        super.beforeEach()
        val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
          new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("", pattern)
        val result: Int = pp.parse(parseContext, "OTHER" + parse + ":OTHER", 5)
        assertEquals(result, parse.length + 5)
        assertParsed(expected)
      case _ =>
        fail()
    }
  }

  test("test_parse_endStringMatch_EmptyUTC") {
    provider_offsets.foreach {
      case (pattern, parse, expected) =>
        super.beforeEach()
        val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
          new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("", pattern)
        val result: Int = pp.parse(parseContext, "OTHER" + parse, 5)
        assertEquals(result, parse.length + 5)
        assertParsed(expected)
      case _ =>
        fail()
    }
  }

  val provider_bigOffsets: List[(String, String, Long)] = {
    List(
      ("+HH", "+59", 59 * 3600),
      ("+HH", "-19", -(19 * 3600)),
      ("+HHMM", "+1801", 18 * 3600 + 1 * 60),
      ("+HHMM", "-1801", -(18 * 3600 + 1 * 60)),
      ("+HH:MM", "+18:01", 18 * 3600 + 1 * 60),
      ("+HH:MM", "-18:01", -(18 * 3600 + 1 * 60)),
      ("+HHMMss", "+180103", 18 * 3600 + 1 * 60 + 3),
      ("+HHMMss", "-180103", -(18 * 3600 + 1 * 60 + 3)),
      ("+HH:MM:ss", "+18:01:03", 18 * 3600 + 1 * 60 + 3),
      ("+HH:MM:ss", "-18:01:03", -(18 * 3600 + 1 * 60 + 3)),
      ("+HHMMSS", "+180103", 18 * 3600 + 1 * 60 + 3),
      ("+HHMMSS", "-180103", -(18 * 3600 + 1 * 60 + 3)),
      ("+HH:MM:SS", "+18:01:03", 18 * 3600 + 1 * 60 + 3),
      ("+HH:MM:SS", "-18:01:03", -(18 * 3600 + 1 * 60 + 3))
    )
  }

  test("test_parse_bigOffsets") {
    provider_bigOffsets.foreach {
      case (pattern, parse, offsetSecs) =>
        super.beforeEach()
        val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
          new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("Z", pattern)
        val result: Int = pp.parse(parseContext, parse, 0)
        assertEquals(result, parse.length)
        assertEquals(parseContext.getParsed(OFFSET_SECONDS), offsetSecs.asInstanceOf[Long])
      case _ =>
        fail()
    }
  }

  val provider_badOffsets: List[(String, String, Int)] = {
    List(
      ("+HH", "+1", ~0),
      ("+HH", "-1", ~0),
      ("+HH", "01", ~0),
      ("+HH", "01", ~0),
      ("+HH", "+AA", ~0),
      ("+HHMM", "+1", ~0),
      ("+HHMM", "+01", ~0),
      ("+HHMM", "+001", ~0),
      ("+HHMM", "0102", ~0),
      ("+HHMM", "+01:02", ~0),
      ("+HHMM", "+AAAA", ~0),
      ("+HH:MM", "+1", ~0),
      ("+HH:MM", "+01", ~0),
      ("+HH:MM", "+0:01", ~0),
      ("+HH:MM", "+00:1", ~0),
      ("+HH:MM", "+0:1", ~0),
      ("+HH:MM", "+:", ~0),
      ("+HH:MM", "01:02", ~0),
      ("+HH:MM", "+0102", ~0),
      ("+HH:MM", "+AA:AA", ~0),
      ("+HHMMss", "+1", ~0),
      ("+HHMMss", "+01", ~0),
      ("+HHMMss", "+001", ~0),
      ("+HHMMss", "0102", ~0),
      ("+HHMMss", "+01:02", ~0),
      ("+HHMMss", "+AAAA", ~0),
      ("+HH:MM:ss", "+1", ~0),
      ("+HH:MM:ss", "+01", ~0),
      ("+HH:MM:ss", "+0:01", ~0),
      ("+HH:MM:ss", "+00:1", ~0),
      ("+HH:MM:ss", "+0:1", ~0),
      ("+HH:MM:ss", "+:", ~0),
      ("+HH:MM:ss", "01:02", ~0),
      ("+HH:MM:ss", "+0102", ~0),
      ("+HH:MM:ss", "+AA:AA", ~0),
      ("+HHMMSS", "+1", ~0),
      ("+HHMMSS", "+01", ~0),
      ("+HHMMSS", "+001", ~0),
      ("+HHMMSS", "0102", ~0),
      ("+HHMMSS", "+01:02", ~0),
      ("+HHMMSS", "+AAAA", ~0),
      ("+HH:MM:SS", "+1", ~0),
      ("+HH:MM:SS", "+01", ~0),
      ("+HH:MM:SS", "+0:01", ~0),
      ("+HH:MM:SS", "+00:1", ~0),
      ("+HH:MM:SS", "+0:1", ~0),
      ("+HH:MM:SS", "+:", ~0),
      ("+HH:MM:SS", "01:02", ~0),
      ("+HH:MM:SS", "+0102", ~0),
      ("+HH:MM:SS", "+AA:AA", ~0)
    )
  }

  test("test_parse_invalid") {
    provider_badOffsets.foreach {
      case (pattern, parse, expectedPosition) =>
        val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
          new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("Z", pattern)
        val result: Int = pp.parse(parseContext, parse, 0)
        assertEquals(result, expectedPosition)
      case _ =>
        fail()
    }
  }

  test("test_parse_caseSensitiveUTC_matchedCase") {
    parseContext.setCaseSensitive(true)
    val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
      new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("Z", "+HH:MM:ss")
    val result: Int = pp.parse(parseContext, "Z", 0)
    assertEquals(result, 1)
    assertParsed(ZoneOffset.UTC)
  }

  test("test_parse_caseSensitiveUTC_unmatchedCase") {
    parseContext.setCaseSensitive(true)
    val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
      new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("Z", "+HH:MM:ss")
    val result: Int = pp.parse(parseContext, "z", 0)
    assertEquals(result, ~0)
    assertParsed(null)
  }

  test("test_parse_caseInsensitiveUTC_matchedCase") {
    parseContext.setCaseSensitive(false)
    val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
      new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("Z", "+HH:MM:ss")
    val result: Int = pp.parse(parseContext, "Z", 0)
    assertEquals(result, 1)
    assertParsed(ZoneOffset.UTC)
  }

  test("test_parse_caseInsensitiveUTC_unmatchedCase") {
    parseContext.setCaseSensitive(false)
    val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
      new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("Z", "+HH:MM:ss")
    val result: Int = pp.parse(parseContext, "z", 0)
    assertEquals(result, 1)
    assertParsed(ZoneOffset.UTC)
  }

  private def assertParsed(expectedOffset: ZoneOffset): Unit = {
    assertEquals(parseContext.toParsed.query(TemporalQueries.chronology), null)
    assertEquals(parseContext.toParsed.query(TemporalQueries.zoneId), null)
    if (expectedOffset == null) {
      assertEquals(parseContext.getParsed(OFFSET_SECONDS), null)
    } else {
      assertEquals(parseContext.getParsed(OFFSET_SECONDS), expectedOffset.getTotalSeconds.toLong)
    }
  }
}
