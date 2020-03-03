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
import org.threeten.bp.DateTimeException
import org.threeten.bp.LocalDate
import org.threeten.bp.format.internal.TTBPDateTimeFormatterBuilder

/** Test PadPrinterDecorator. */
class TestPadPrinterDecorator extends AnyFunSuite with GenTestPrinterParser with AssertionsHelper {
  test("test_print_emptyCalendrical") {
    val pp: TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator =
      new TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator(
        new TTBPDateTimeFormatterBuilder.CharLiteralPrinterParser('Z'),
        3,
        '-'
      )
    pp.print(printEmptyContext, buf)
    assertEquals(buf.toString, "--Z")
  }

  test("test_print_fullDateTime") {
    printContext.setDateTime(LocalDate.of(2008, 12, 3))
    val pp: TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator =
      new TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator(
        new TTBPDateTimeFormatterBuilder.CharLiteralPrinterParser('Z'),
        3,
        '-'
      )
    pp.print(printContext, buf)
    assertEquals(buf.toString, "--Z")
  }

  test("test_print_append") {
    buf.append("EXISTING")
    val pp: TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator =
      new TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator(
        new TTBPDateTimeFormatterBuilder.CharLiteralPrinterParser('Z'),
        3,
        '-'
      )
    pp.print(printEmptyContext, buf)
    assertEquals(buf.toString, "EXISTING--Z")
  }

  test("test_print_noPadRequiredSingle") {
    val pp: TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator =
      new TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator(
        new TTBPDateTimeFormatterBuilder.CharLiteralPrinterParser('Z'),
        1,
        '-'
      )
    pp.print(printEmptyContext, buf)
    assertEquals(buf.toString, "Z")
  }

  test("test_print_padRequiredSingle") {
    val pp: TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator =
      new TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator(
        new TTBPDateTimeFormatterBuilder.CharLiteralPrinterParser('Z'),
        5,
        '-'
      )
    pp.print(printEmptyContext, buf)
    assertEquals(buf.toString, "----Z")
  }

  test("test_print_noPadRequiredMultiple") {
    val pp: TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator =
      new TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator(
        new TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser("WXYZ"),
        4,
        '-'
      )
    pp.print(printEmptyContext, buf)
    assertEquals(buf.toString, "WXYZ")
  }

  test("test_print_padRequiredMultiple") {
    val pp: TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator =
      new TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator(
        new TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser("WXYZ"),
        5,
        '-'
      )
    pp.print(printEmptyContext, buf)
    assertEquals(buf.toString, "-WXYZ")
  }

  test("test_print_overPad") {
    assertThrows[DateTimeException] {
      val pp: TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator =
        new TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator(
          new TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser("WXYZ"),
          3,
          '-'
        )
      pp.print(printEmptyContext, buf)
    }
  }

  test("test_toString1") {
    val wrapped: TTBPDateTimeFormatterBuilder.CharLiteralPrinterParser =
      new TTBPDateTimeFormatterBuilder.CharLiteralPrinterParser('Y')
    val pp: TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator =
      new TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator(wrapped, 5, ' ')
    assertEquals(pp.toString, "Pad('Y',5)")
  }

  test("test_toString2") {
    val wrapped: TTBPDateTimeFormatterBuilder.CharLiteralPrinterParser =
      new TTBPDateTimeFormatterBuilder.CharLiteralPrinterParser('Y')
    val pp: TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator =
      new TTBPDateTimeFormatterBuilder.PadPrinterParserDecorator(wrapped, 5, '-')
    assertEquals(pp.toString, "Pad('Y',5,'-')")
  }
}
