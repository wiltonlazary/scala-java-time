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

import org.scalatest.FunSuite
import org.threeten.bp.AssertionsHelper
import org.threeten.bp.Platform
import org.threeten.bp.temporal.ChronoField.MONTH_OF_YEAR
import org.threeten.bp.format.internal.TTBPDateTimeFormatterBuilder
import org.threeten.bp.temporal.TemporalField

/** Test PadPrinterParserDecorator. */
class TestPadParserDecorator extends FunSuite with GenTestPrinterParser with AssertionsHelper {
  test("test_parse_negativePosition") {
    assertThrows[Platform.DFE] {
      val pp: TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator =
        new TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator(
          new TTBPDateTimeFormatterBuilder.CharLiteralPrinterParser('Z'),
          3,
          '-'
        )
      pp.parse(parseContext, "--Z", -1)
    }
  }

  test("test_parse_offEndPosition") {
    assertThrows[IndexOutOfBoundsException] {
      val pp: TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator =
        new TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator(
          new TTBPDateTimeFormatterBuilder.CharLiteralPrinterParser('Z'),
          3,
          '-'
        )
      pp.parse(parseContext, "--Z", 4)
    }
  }

  test("test_parse") {
    val pp: TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator =
      new TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator(
        new TTBPDateTimeFormatterBuilder.NumberPrinterParser(MONTH_OF_YEAR, 1, 3, SignStyle.NEVER),
        3,
        '-'
      )
    val result: Int = pp.parse(parseContext, "--2", 0)
    assertEquals(result, 3)
    assertParsed(MONTH_OF_YEAR, 2L)
  }

  test("test_parse_noReadBeyond") {
    val pp: TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator =
      new TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator(
        new TTBPDateTimeFormatterBuilder.NumberPrinterParser(MONTH_OF_YEAR, 1, 3, SignStyle.NEVER),
        3,
        '-'
      )
    val result: Int = pp.parse(parseContext, "--22", 0)
    assertEquals(result, 3)
    assertParsed(MONTH_OF_YEAR, 2L)
  }

  test("test_parse_textLessThanPadWidth") {
    val pp: TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator =
      new TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator(
        new TTBPDateTimeFormatterBuilder.NumberPrinterParser(MONTH_OF_YEAR, 1, 3, SignStyle.NEVER),
        3,
        '-'
      )
    val result: Int = pp.parse(parseContext, "-1", 0)
    assertEquals(result, ~0)
  }

  test("test_parse_decoratedErrorPassedBack") {
    val pp: TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator =
      new TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator(
        new TTBPDateTimeFormatterBuilder.NumberPrinterParser(MONTH_OF_YEAR, 1, 3, SignStyle.NEVER),
        3,
        '-'
      )
    val result: Int = pp.parse(parseContext, "--A", 0)
    assertEquals(result, ~2)
  }

  test("test_parse_decoratedDidNotParseToPadWidth") {
    val pp: TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator =
      new TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator(
        new TTBPDateTimeFormatterBuilder.NumberPrinterParser(MONTH_OF_YEAR, 1, 3, SignStyle.NEVER),
        3,
        '-'
      )
    val result: Int = pp.parse(parseContext, "-1X", 0)
    assertEquals(result, ~1)
  }

  private def assertParsed(field: TemporalField, value: java.lang.Long): Unit =
    if (value == null) {
      assertEquals(parseContext.getParsed(field), null)
    } else {
      assertEquals(parseContext.getParsed(field), value)
    }
}
