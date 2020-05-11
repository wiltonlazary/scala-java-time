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
import org.threeten.bp.temporal.ChronoField.DAY_OF_MONTH
import org.threeten.bp.temporal.ChronoField.DAY_OF_WEEK
import org.threeten.bp.temporal.ChronoField.MONTH_OF_YEAR
import java.util.Locale

import org.threeten.bp.temporal.TemporalField
import org.threeten.bp.temporal.TemporalQueries
import org.threeten.bp.format.internal.{ TTBPDateTimeFormatterBuilder, TTBPDateTimeParseContext }
import org.threeten.bp.format.internal.TTBPDateTimeTextProvider

/** Test TextPrinterParser. */
object TestTextParser {
  private val PROVIDER: TTBPDateTimeTextProvider = TTBPDateTimeTextProvider.Provider
}

class TestTextParser extends AnyFunSuite with GenTestPrinterParser with AssertionsHelper {
  val data_error: List[(TTBPDateTimeFormatterBuilder.TextPrinterParser, String, Int, Class[_])] =
    List(
      (new TTBPDateTimeFormatterBuilder.TextPrinterParser(DAY_OF_WEEK,
                                                          TextStyle.FULL,
                                                          TestTextParser.PROVIDER
       ),
       "Monday",
       -1,
       classOf[IndexOutOfBoundsException]
      ),
      (new TTBPDateTimeFormatterBuilder.TextPrinterParser(DAY_OF_WEEK,
                                                          TextStyle.FULL,
                                                          TestTextParser.PROVIDER
       ),
       "Monday",
       7,
       classOf[IndexOutOfBoundsException]
      )
    )

  test("test_parse_error") {
    data_error.foreach {
      case (pp, text, pos, expected) =>
        try pp.parse(parseContext, text, pos)
        catch {
          case ex: RuntimeException =>
            assertTrue(expected.isInstance(ex))
            assertEquals(parseContext.toParsed.query(TemporalQueries.chronology), null)
            assertEquals(parseContext.toParsed.query(TemporalQueries.zoneId), null)
        }
      case _                         =>
        fail()
    }
  }

  test("test_parse_midStr") {
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(DAY_OF_WEEK,
                                                         TextStyle.FULL,
                                                         TestTextParser.PROVIDER
      )
    val newPos: Int                                        = pp.parse(parseContext, "XxxMondayXxx", 3)
    assertEquals(newPos, 9)
    assertParsed(parseContext, DAY_OF_WEEK, 1L)
  }

  test("test_parse_remainderIgnored") {
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(DAY_OF_WEEK,
                                                         TextStyle.SHORT,
                                                         TestTextParser.PROVIDER
      )
    val newPos: Int                                        = pp.parse(parseContext, "Wednesday", 0)
    assertEquals(newPos, 3)
    assertParsed(parseContext, DAY_OF_WEEK, 3L)
  }

  test("test_parse_noMatch1") {
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(DAY_OF_WEEK,
                                                         TextStyle.FULL,
                                                         TestTextParser.PROVIDER
      )
    val newPos: Int                                        = pp.parse(parseContext, "Munday", 0)
    assertEquals(newPos, ~0)
    assertEquals(parseContext.toParsed.query(TemporalQueries.chronology), null)
    assertEquals(parseContext.toParsed.query(TemporalQueries.zoneId), null)
  }

  test("test_parse_noMatch2") {
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(DAY_OF_WEEK,
                                                         TextStyle.FULL,
                                                         TestTextParser.PROVIDER
      )
    val newPos: Int                                        = pp.parse(parseContext, "Monday", 3)
    assertEquals(newPos, ~3)
    assertEquals(parseContext.toParsed.query(TemporalQueries.chronology), null)
    assertEquals(parseContext.toParsed.query(TemporalQueries.zoneId), null)
  }

  test("test_parse_noMatch_atEnd") {
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(DAY_OF_WEEK,
                                                         TextStyle.FULL,
                                                         TestTextParser.PROVIDER
      )
    val newPos: Int                                        = pp.parse(parseContext, "Monday", 6)
    assertEquals(newPos, ~6)
    assertEquals(parseContext.toParsed.query(TemporalQueries.chronology), null)
    assertEquals(parseContext.toParsed.query(TemporalQueries.zoneId), null)
  }

  val provider_text: List[(TemporalField, TextStyle, Int, String)] =
    List(
      (DAY_OF_WEEK, TextStyle.FULL, 1, "Monday"),
      (DAY_OF_WEEK, TextStyle.FULL, 2, "Tuesday"),
      (DAY_OF_WEEK, TextStyle.FULL, 3, "Wednesday"),
      (DAY_OF_WEEK, TextStyle.FULL, 4, "Thursday"),
      (DAY_OF_WEEK, TextStyle.FULL, 5, "Friday"),
      (DAY_OF_WEEK, TextStyle.FULL, 6, "Saturday"),
      (DAY_OF_WEEK, TextStyle.FULL, 7, "Sunday"),
      (DAY_OF_WEEK, TextStyle.SHORT, 1, "Mon"),
      (DAY_OF_WEEK, TextStyle.SHORT, 2, "Tue"),
      (DAY_OF_WEEK, TextStyle.SHORT, 3, "Wed"),
      (DAY_OF_WEEK, TextStyle.SHORT, 4, "Thu"),
      (DAY_OF_WEEK, TextStyle.SHORT, 5, "Fri"),
      (DAY_OF_WEEK, TextStyle.SHORT, 6, "Sat"),
      (DAY_OF_WEEK, TextStyle.SHORT, 7, "Sun"),
      (MONTH_OF_YEAR, TextStyle.FULL, 1, "January"),
      (MONTH_OF_YEAR, TextStyle.FULL, 12, "December"),
      (MONTH_OF_YEAR, TextStyle.SHORT, 1, "Jan"),
      (MONTH_OF_YEAR, TextStyle.SHORT, 12, "Dec")
    )

  val provider_number: List[(TemporalField, TextStyle, Int, String)] =
    List(
      (DAY_OF_MONTH, TextStyle.FULL, 1, "1"),
      (DAY_OF_MONTH, TextStyle.FULL, 2, "2"),
      (DAY_OF_MONTH, TextStyle.FULL, 30, "30"),
      (DAY_OF_MONTH, TextStyle.FULL, 31, "31"),
      (DAY_OF_MONTH, TextStyle.SHORT, 1, "1"),
      (DAY_OF_MONTH, TextStyle.SHORT, 2, "2"),
      (DAY_OF_MONTH, TextStyle.SHORT, 30, "30"),
      (DAY_OF_MONTH, TextStyle.SHORT, 31, "31")
    )

  test("test_parseText") {
    provider_text.foreach {
      case (field, style, value, input) =>
        super.beforeEach()
        val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
          new TTBPDateTimeFormatterBuilder.TextPrinterParser(field, style, TestTextParser.PROVIDER)
        val newPos: Int                                        = pp.parse(parseContext, input, 0)
        assertEquals(newPos, input.length)
        assertParsed(parseContext, field, value.toLong)
      case _                            =>
        fail()
    }
  }

  test("test_parseNumber") {
    provider_number.foreach {
      case (field, style, value, input) =>
        super.beforeEach()
        val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
          new TTBPDateTimeFormatterBuilder.TextPrinterParser(field, style, TestTextParser.PROVIDER)
        val newPos: Int                                        = pp.parse(parseContext, input, 0)
        assertEquals(newPos, input.length)
        assertParsed(parseContext, field, value.toLong)
      case _                            =>
        fail()
    }
  }

  test("test_parse_strict_caseSensitive_parseUpper") {
    provider_text.foreach {
      case (field, style, _, input) =>
        super.beforeEach()
        parseContext.setCaseSensitive(true)
        val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
          new TTBPDateTimeFormatterBuilder.TextPrinterParser(field, style, TestTextParser.PROVIDER)
        val newPos: Int                                        = pp.parse(parseContext, input.toUpperCase, 0)
        assertEquals(newPos, ~0)
        assertEquals(parseContext.toParsed.query(TemporalQueries.chronology), null)
        assertEquals(parseContext.toParsed.query(TemporalQueries.zoneId), null)
      case _                        =>
        fail()
    }
  }

  test("test_parse_strict_caseInsensitive_parseUpper") {
    provider_text.foreach {
      case (field, style, value, input) =>
        super.beforeEach()
        parseContext.setCaseSensitive(false)
        val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
          new TTBPDateTimeFormatterBuilder.TextPrinterParser(field, style, TestTextParser.PROVIDER)
        val newPos: Int                                        = pp.parse(parseContext, input.toUpperCase, 0)
        assertEquals(newPos, input.length)
        assertParsed(parseContext, field, value.toLong)
      case _                            =>
        fail()
    }
  }

  test("test_parse_strict_caseSensitive_parseLower") {
    provider_text.foreach {
      case (field, style, _, input) =>
        super.beforeEach()
        parseContext.setCaseSensitive(true)
        val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
          new TTBPDateTimeFormatterBuilder.TextPrinterParser(field, style, TestTextParser.PROVIDER)
        val newPos: Int                                        = pp.parse(parseContext, input.toLowerCase, 0)
        assertEquals(newPos, ~0)
        assertEquals(parseContext.toParsed.query(TemporalQueries.chronology), null)
        assertEquals(parseContext.toParsed.query(TemporalQueries.zoneId), null)
      case _                        =>
        fail()
    }
  }

  test("test_parse_strict_caseInsensitive_parseLower") {
    provider_text.foreach {
      case (field, style, value, input) =>
        super.beforeEach()
        parseContext.setCaseSensitive(false)
        val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
          new TTBPDateTimeFormatterBuilder.TextPrinterParser(field, style, TestTextParser.PROVIDER)
        val newPos: Int                                        = pp.parse(parseContext, input.toLowerCase, 0)
        assertEquals(newPos, input.length)
        assertParsed(parseContext, field, value.toLong)
      case _                            =>
        fail()
    }
  }

  test("test_parse_full_strict_full_match") {
    parseContext.setStrict(true)
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(MONTH_OF_YEAR,
                                                         TextStyle.FULL,
                                                         TestTextParser.PROVIDER
      )
    val newPos: Int                                        = pp.parse(parseContext, "January", 0)
    assertEquals(newPos, 7)
    assertParsed(parseContext, MONTH_OF_YEAR, 1L)
  }

  test("test_parse_full_strict_short_noMatch") {
    parseContext.setStrict(true)
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(MONTH_OF_YEAR,
                                                         TextStyle.FULL,
                                                         TestTextParser.PROVIDER
      )
    val newPos: Int                                        = pp.parse(parseContext, "Janua", 0)
    assertEquals(newPos, ~0)
    assertEquals(parseContext.toParsed.query(TemporalQueries.chronology), null)
    assertEquals(parseContext.toParsed.query(TemporalQueries.zoneId), null)
  }

  test("test_parse_full_strict_number_noMatch") {
    parseContext.setStrict(true)
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(MONTH_OF_YEAR,
                                                         TextStyle.FULL,
                                                         TestTextParser.PROVIDER
      )
    val newPos: Int                                        = pp.parse(parseContext, "1", 0)
    assertEquals(newPos, ~0)
    assertEquals(parseContext.toParsed.query(TemporalQueries.chronology), null)
    assertEquals(parseContext.toParsed.query(TemporalQueries.zoneId), null)
  }

  test("test_parse_short_strict_full_match") {
    parseContext.setStrict(true)
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(MONTH_OF_YEAR,
                                                         TextStyle.SHORT,
                                                         TestTextParser.PROVIDER
      )
    val newPos: Int                                        = pp.parse(parseContext, "January", 0)
    assertEquals(newPos, 3)
    assertParsed(parseContext, MONTH_OF_YEAR, 1L)
  }

  test("test_parse_short_strict_short_match") {
    parseContext.setStrict(true)
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(MONTH_OF_YEAR,
                                                         TextStyle.SHORT,
                                                         TestTextParser.PROVIDER
      )
    val newPos: Int                                        = pp.parse(parseContext, "Janua", 0)
    assertEquals(newPos, 3)
    assertParsed(parseContext, MONTH_OF_YEAR, 1L)
  }

  test("test_parse_short_strict_number_noMatch") {
    parseContext.setStrict(true)
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(MONTH_OF_YEAR,
                                                         TextStyle.SHORT,
                                                         TestTextParser.PROVIDER
      )
    val newPos: Int                                        = pp.parse(parseContext, "1", 0)
    assertEquals(newPos, ~0)
    assertEquals(parseContext.toParsed.query(TemporalQueries.chronology), null)
    assertEquals(parseContext.toParsed.query(TemporalQueries.zoneId), null)
  }

  test("test_parse_french_short_strict_full_noMatch") {
    parseContext.setLocale(Locale.FRENCH)
    parseContext.setStrict(true)
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(MONTH_OF_YEAR,
                                                         TextStyle.SHORT,
                                                         TestTextParser.PROVIDER
      )
    val newPos: Int                                        = pp.parse(parseContext, "janvier", 0)
    assertEquals(newPos, ~0)
    assertEquals(parseContext.toParsed.query(TemporalQueries.chronology), null)
    assertEquals(parseContext.toParsed.query(TemporalQueries.zoneId), null)
  }

  test("test_parse_french_short_strict_short_match") {
    parseContext.setLocale(Locale.FRENCH)
    parseContext.setStrict(true)
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(MONTH_OF_YEAR,
                                                         TextStyle.SHORT,
                                                         TestTextParser.PROVIDER
      )
    val newPos: Int                                        = pp.parse(parseContext, "janv.", 0)
    assertEquals(newPos, 5)
    assertParsed(parseContext, MONTH_OF_YEAR, 1L)
  }

  test("test_parse_full_lenient_full_match") {
    parseContext.setStrict(false)
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(MONTH_OF_YEAR,
                                                         TextStyle.FULL,
                                                         TestTextParser.PROVIDER
      )
    val newPos: Int                                        = pp.parse(parseContext, "January", 0)
    assertEquals(newPos, 7)
    assertParsed(parseContext, MONTH_OF_YEAR, 1L)
  }

  test("test_parse_full_lenient_short_match") {
    parseContext.setStrict(false)
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(MONTH_OF_YEAR,
                                                         TextStyle.FULL,
                                                         TestTextParser.PROVIDER
      )
    val newPos: Int                                        = pp.parse(parseContext, "Janua", 0)
    assertEquals(newPos, 3)
    assertParsed(parseContext, MONTH_OF_YEAR, 1L)
  }

  test("test_parse_full_lenient_number_match") {
    parseContext.setStrict(false)
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(MONTH_OF_YEAR,
                                                         TextStyle.FULL,
                                                         TestTextParser.PROVIDER
      )
    val newPos: Int                                        = pp.parse(parseContext, "1", 0)
    assertEquals(newPos, 1)
    assertParsed(parseContext, MONTH_OF_YEAR, 1L)
  }

  test("test_parse_short_lenient_full_match") {
    parseContext.setStrict(false)
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(MONTH_OF_YEAR,
                                                         TextStyle.SHORT,
                                                         TestTextParser.PROVIDER
      )
    val newPos: Int                                        = pp.parse(parseContext, "January", 0)
    assertEquals(newPos, 7)
    assertParsed(parseContext, MONTH_OF_YEAR, 1L)
  }

  test("test_parse_short_lenient_short_match") {
    parseContext.setStrict(false)
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(MONTH_OF_YEAR,
                                                         TextStyle.SHORT,
                                                         TestTextParser.PROVIDER
      )
    val newPos: Int                                        = pp.parse(parseContext, "Janua", 0)
    assertEquals(newPos, 3)
    assertParsed(parseContext, MONTH_OF_YEAR, 1L)
  }

  test("test_parse_short_lenient_number_match") {
    parseContext.setStrict(false)
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(MONTH_OF_YEAR,
                                                         TextStyle.SHORT,
                                                         TestTextParser.PROVIDER
      )
    val newPos: Int                                        = pp.parse(parseContext, "1", 0)
    assertEquals(newPos, 1)
    assertParsed(parseContext, MONTH_OF_YEAR, 1L)
  }

  private def assertParsed(
    context: TTBPDateTimeParseContext,
    field:   TemporalField,
    value:   java.lang.Long
  ): Unit =
    if (value == null)
      assertEquals(context.getParsed(field), null)
    else
      assertEquals(context.getParsed(field), value)
}
