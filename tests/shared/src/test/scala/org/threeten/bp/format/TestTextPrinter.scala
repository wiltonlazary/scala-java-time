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

import org.threeten.bp.temporal.ChronoField.DAY_OF_MONTH
import org.threeten.bp.temporal.ChronoField.DAY_OF_WEEK
import org.threeten.bp.temporal.ChronoField.MONTH_OF_YEAR
import java.util.Locale

import org.scalatest.funsuite.AnyFunSuite
import org.threeten.bp.AssertionsHelper
import org.threeten.bp.DateTimeException
import org.threeten.bp.LocalDate
import org.threeten.bp.format.internal.TTBPDateTimeFormatterBuilder
import org.threeten.bp.format.internal.TTBPDateTimeTextProvider
import org.threeten.bp.temporal.MockFieldValue
import org.threeten.bp.temporal.TemporalField

/** Test TextPrinterParser. */
object TestTextPrinter {
  private val PROVIDER: TTBPDateTimeTextProvider = TTBPDateTimeTextProvider.Provider
}

class TestTextPrinter extends AnyFunSuite with GenTestPrinterParser with AssertionsHelper {
  test("test_print_emptyCalendrical") {
    assertThrows[DateTimeException] {
      val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
        new TTBPDateTimeFormatterBuilder.TextPrinterParser(DAY_OF_WEEK,
                                                           TextStyle.FULL,
                                                           TestTextPrinter.PROVIDER)
      pp.print(printEmptyContext, buf)
    }
  }

  test("test_print_append") {
    printContext.setDateTime(LocalDate.of(2012, 4, 18))
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(DAY_OF_WEEK,
                                                         TextStyle.FULL,
                                                         TestTextPrinter.PROVIDER)
    buf.append("EXISTING")
    pp.print(printContext, buf)
    assertEquals(buf.toString, "EXISTINGWednesday")
  }

  val provider_dow: List[(TemporalField, TextStyle, Int, String)] = {
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
      (DAY_OF_MONTH, TextStyle.FULL, 1, "1"),
      (DAY_OF_MONTH, TextStyle.FULL, 2, "2"),
      (DAY_OF_MONTH, TextStyle.FULL, 3, "3"),
      (DAY_OF_MONTH, TextStyle.FULL, 28, "28"),
      (DAY_OF_MONTH, TextStyle.FULL, 29, "29"),
      (DAY_OF_MONTH, TextStyle.FULL, 30, "30"),
      (DAY_OF_MONTH, TextStyle.FULL, 31, "31"),
      (DAY_OF_MONTH, TextStyle.SHORT, 1, "1"),
      (DAY_OF_MONTH, TextStyle.SHORT, 2, "2"),
      (DAY_OF_MONTH, TextStyle.SHORT, 3, "3"),
      (DAY_OF_MONTH, TextStyle.SHORT, 28, "28"),
      (DAY_OF_MONTH, TextStyle.SHORT, 29, "29"),
      (DAY_OF_MONTH, TextStyle.SHORT, 30, "30"),
      (DAY_OF_MONTH, TextStyle.SHORT, 31, "31"),
      (MONTH_OF_YEAR, TextStyle.FULL, 1, "January"),
      (MONTH_OF_YEAR, TextStyle.FULL, 12, "December"),
      (MONTH_OF_YEAR, TextStyle.SHORT, 1, "Jan"),
      (MONTH_OF_YEAR, TextStyle.SHORT, 12, "Dec")
    )
  }

  test("test_print") {
    provider_dow.foreach {
      case (field, style, value, expected) =>
        super.beforeEach()
        printContext.setDateTime(new MockFieldValue(field, value))
        val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
          new TTBPDateTimeFormatterBuilder.TextPrinterParser(field, style, TestTextPrinter.PROVIDER)
        pp.print(printContext, buf)
        assertEquals(buf.toString, expected)
      case _ =>
        fail()
    }
  }

  test("test_print_french_long") {
    printContext.setLocale(Locale.FRENCH)
    printContext.setDateTime(LocalDate.of(2012, 1, 1))
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(MONTH_OF_YEAR,
                                                         TextStyle.FULL,
                                                         TestTextPrinter.PROVIDER)
    pp.print(printContext, buf)
    assertEquals(buf.toString, "janvier")
  }

  test("test_print_french_short") {
    printContext.setLocale(Locale.FRENCH)
    printContext.setDateTime(LocalDate.of(2012, 1, 1))
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(MONTH_OF_YEAR,
                                                         TextStyle.SHORT,
                                                         TestTextPrinter.PROVIDER)
    pp.print(printContext, buf)
    assertEquals(buf.toString, "janv.")
  }

  test("test_toString1") {
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(MONTH_OF_YEAR,
                                                         TextStyle.FULL,
                                                         TestTextPrinter.PROVIDER)
    assertEquals(pp.toString, "Text(MonthOfYear)")
  }

  test("test_toString2") {
    val pp: TTBPDateTimeFormatterBuilder.TextPrinterParser =
      new TTBPDateTimeFormatterBuilder.TextPrinterParser(MONTH_OF_YEAR,
                                                         TextStyle.SHORT,
                                                         TestTextPrinter.PROVIDER)
    assertEquals(pp.toString, "Text(MonthOfYear,SHORT)")
  }
}
