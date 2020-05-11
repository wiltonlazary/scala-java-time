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
import org.threeten.bp.temporal.ChronoField.HOUR_OF_DAY
import org.threeten.bp.DateTimeException
import org.threeten.bp.LocalDate
import org.threeten.bp.format.internal.TTBPDateTimeFormatterBuilder
import org.threeten.bp.temporal.MockFieldValue

/** Test SimpleNumberPrinterParser. */
class TestNumberPrinter extends AnyFunSuite with GenTestPrinterParser with AssertionsHelper {
  test("print_emptyCalendrical") {
    assertThrows[DateTimeException] {
      val pp: TTBPDateTimeFormatterBuilder.NumberPrinterParser =
        new TTBPDateTimeFormatterBuilder.NumberPrinterParser(DAY_OF_MONTH, 1, 2, SignStyle.NEVER)
      pp.print(printEmptyContext, buf)
    }
  }

  test("print_append") {
    printContext.setDateTime(LocalDate.of(2012, 1, 3))
    val pp: TTBPDateTimeFormatterBuilder.NumberPrinterParser =
      new TTBPDateTimeFormatterBuilder.NumberPrinterParser(DAY_OF_MONTH, 1, 2, SignStyle.NEVER)
    buf.append("EXISTING")
    pp.print(printContext, buf)
    assertEquals(buf.toString, "EXISTING3")
  }

  val provider_pad: List[(Int, Int, Long, String)] =
    List(
      (1, 1, -10, null),
      (1, 1, -9, "9"),
      (1, 1, -1, "1"),
      (1, 1, 0, "0"),
      (1, 1, 3, "3"),
      (1, 1, 9, "9"),
      (1, 1, 10, null),
      (1, 2, -100, null),
      (1, 2, -99, "99"),
      (1, 2, -10, "10"),
      (1, 2, -9, "9"),
      (1, 2, -1, "1"),
      (1, 2, 0, "0"),
      (1, 2, 3, "3"),
      (1, 2, 9, "9"),
      (1, 2, 10, "10"),
      (1, 2, 99, "99"),
      (1, 2, 100, null),
      (2, 2, -100, null),
      (2, 2, -99, "99"),
      (2, 2, -10, "10"),
      (2, 2, -9, "09"),
      (2, 2, -1, "01"),
      (2, 2, 0, "00"),
      (2, 2, 3, "03"),
      (2, 2, 9, "09"),
      (2, 2, 10, "10"),
      (2, 2, 99, "99"),
      (2, 2, 100, null),
      (1, 3, -1000, null),
      (1, 3, -999, "999"),
      (1, 3, -100, "100"),
      (1, 3, -99, "99"),
      (1, 3, -10, "10"),
      (1, 3, -9, "9"),
      (1, 3, -1, "1"),
      (1, 3, 0, "0"),
      (1, 3, 3, "3"),
      (1, 3, 9, "9"),
      (1, 3, 10, "10"),
      (1, 3, 99, "99"),
      (1, 3, 100, "100"),
      (1, 3, 999, "999"),
      (1, 3, 1000, null),
      (2, 3, -1000, null),
      (2, 3, -999, "999"),
      (2, 3, -100, "100"),
      (2, 3, -99, "99"),
      (2, 3, -10, "10"),
      (2, 3, -9, "09"),
      (2, 3, -1, "01"),
      (2, 3, 0, "00"),
      (2, 3, 3, "03"),
      (2, 3, 9, "09"),
      (2, 3, 10, "10"),
      (2, 3, 99, "99"),
      (2, 3, 100, "100"),
      (2, 3, 999, "999"),
      (2, 3, 1000, null),
      (3, 3, -1000, null),
      (3, 3, -999, "999"),
      (3, 3, -100, "100"),
      (3, 3, -99, "099"),
      (3, 3, -10, "010"),
      (3, 3, -9, "009"),
      (3, 3, -1, "001"),
      (3, 3, 0, "000"),
      (3, 3, 3, "003"),
      (3, 3, 9, "009"),
      (3, 3, 10, "010"),
      (3, 3, 99, "099"),
      (3, 3, 100, "100"),
      (3, 3, 999, "999"),
      (3, 3, 1000, null),
      (1, 10, Integer.MAX_VALUE - 1, "2147483646"),
      (1, 10, Integer.MAX_VALUE, "2147483647"),
      (1, 10, Integer.MIN_VALUE + 1, "2147483647"),
      (1, 10, Integer.MIN_VALUE, "2147483648")
    )

  test("pad_NOT_NEGATIVE") {
    provider_pad.foreach {
      case (minPad, maxPad, value, result) =>
        super.beforeEach
        printContext.setDateTime(new MockFieldValue(DAY_OF_MONTH, value))
        val pp: TTBPDateTimeFormatterBuilder.NumberPrinterParser =
          new TTBPDateTimeFormatterBuilder.NumberPrinterParser(DAY_OF_MONTH,
                                                               minPad,
                                                               maxPad,
                                                               SignStyle.NOT_NEGATIVE
          )
        try {
          pp.print(printContext, buf)
          if (result == null || value < 0)
            fail("Expected exception")
          assertEquals(buf.toString, result)
        } catch {
          case ex: DateTimeException =>
            if (result == null || value < 0)
              assertEquals(ex.getMessage.contains(DAY_OF_MONTH.toString), true)
            else
              throw ex
        }
      case _                               =>
        fail()
    }
  }

  test("pad_NEVER") {
    provider_pad.foreach {
      case (minPad, maxPad, value, result) =>
        super.beforeEach
        printContext.setDateTime(new MockFieldValue(DAY_OF_MONTH, value))
        val pp: TTBPDateTimeFormatterBuilder.NumberPrinterParser =
          new TTBPDateTimeFormatterBuilder.NumberPrinterParser(DAY_OF_MONTH,
                                                               minPad,
                                                               maxPad,
                                                               SignStyle.NEVER
          )
        try {
          pp.print(printContext, buf)
          if (result == null)
            fail("Expected exception")
          assertEquals(buf.toString, result)
        } catch {
          case ex: DateTimeException =>
            if (result != null)
              throw ex
            assertEquals(ex.getMessage.contains(DAY_OF_MONTH.toString), true)
        }
      case _                               =>
        fail()
    }
  }

  test("pad_NORMAL") {
    provider_pad.foreach {
      case (minPad, maxPad, value, result) =>
        super.beforeEach
        printContext.setDateTime(new MockFieldValue(DAY_OF_MONTH, value))
        val pp: TTBPDateTimeFormatterBuilder.NumberPrinterParser =
          new TTBPDateTimeFormatterBuilder.NumberPrinterParser(DAY_OF_MONTH,
                                                               minPad,
                                                               maxPad,
                                                               SignStyle.NORMAL
          )
        try {
          pp.print(printContext, buf)
          if (result == null)
            fail("Expected exception")
          assertEquals(buf.toString, if (value < 0) "-" + result else result)
        } catch {
          case ex: DateTimeException =>
            if (result != null)
              throw ex
            assertEquals(ex.getMessage.contains(DAY_OF_MONTH.toString), true)
        }
      case _                               =>
        fail()
    }
  }

  test("pad_ALWAYS") {
    provider_pad.foreach {
      case (minPad, maxPad, value, result) =>
        super.beforeEach
        printContext.setDateTime(new MockFieldValue(DAY_OF_MONTH, value))
        val pp: TTBPDateTimeFormatterBuilder.NumberPrinterParser =
          new TTBPDateTimeFormatterBuilder.NumberPrinterParser(DAY_OF_MONTH,
                                                               minPad,
                                                               maxPad,
                                                               SignStyle.ALWAYS
          )
        try {
          pp.print(printContext, buf)
          if (result == null)
            fail("Expected exception")
          assertEquals(buf.toString, if (value < 0) "-" + result else "+" + result)
        } catch {
          case ex: DateTimeException =>
            if (result != null)
              throw ex
            assertEquals(ex.getMessage.contains(DAY_OF_MONTH.toString), true)
        }
      case _                               =>
        fail()
    }
  }

  test("pad_EXCEEDS_PAD") {
    provider_pad.foreach {
      case (minPad, maxPad, value, result) =>
        super.beforeEach
        var _result                                              = result
        printContext.setDateTime(new MockFieldValue(DAY_OF_MONTH, value))
        val pp: TTBPDateTimeFormatterBuilder.NumberPrinterParser =
          new TTBPDateTimeFormatterBuilder.NumberPrinterParser(DAY_OF_MONTH,
                                                               minPad,
                                                               maxPad,
                                                               SignStyle.EXCEEDS_PAD
          )
        try {
          pp.print(printContext, buf)
          if (_result == null)
            fail("Expected exception")
          if (_result.length > minPad || value < 0)
            _result = if (value < 0) "-" + _result else "+" + _result
          assertEquals(buf.toString, _result)
        } catch {
          case ex: DateTimeException =>
            if (_result != null)
              throw ex
            assertEquals(ex.getMessage.contains(DAY_OF_MONTH.toString), true)
        }
      case _                               =>
        fail()
    }
  }

  def test_toString1(): Unit = {
    val pp: TTBPDateTimeFormatterBuilder.NumberPrinterParser =
      new TTBPDateTimeFormatterBuilder.NumberPrinterParser(HOUR_OF_DAY, 1, 19, SignStyle.NORMAL)
    assertEquals(pp.toString, "Value(HourOfDay)")
  }

  def test_toString2(): Unit = {
    val pp: TTBPDateTimeFormatterBuilder.NumberPrinterParser =
      new TTBPDateTimeFormatterBuilder.NumberPrinterParser(HOUR_OF_DAY,
                                                           2,
                                                           2,
                                                           SignStyle.NOT_NEGATIVE
      )
    assertEquals(pp.toString, "Value(HourOfDay,2)")
  }

  def test_toString3(): Unit = {
    val pp: TTBPDateTimeFormatterBuilder.NumberPrinterParser =
      new TTBPDateTimeFormatterBuilder.NumberPrinterParser(HOUR_OF_DAY,
                                                           1,
                                                           2,
                                                           SignStyle.NOT_NEGATIVE
      )
    assertEquals(pp.toString, "Value(HourOfDay,1,2,NOT_NEGATIVE)")
  }
}
