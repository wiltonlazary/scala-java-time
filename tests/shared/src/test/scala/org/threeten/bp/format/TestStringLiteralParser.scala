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
import org.threeten.bp.format.internal.TTBPDateTimeFormatterBuilder
import org.threeten.bp.temporal.TemporalQueries

/** Test StringLiteralPrinterParser. */
class TestStringLiteralParser extends AnyFunSuite with GenTestPrinterParser with AssertionsHelper {
  val data_success
    : List[(TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser, Boolean, String, Int, Int)] =
    List(
      (new TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser("hello"), true, "hello", 0, 5),
      (new TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser("hello"),
       true,
       "helloOTHER",
       0,
       5
      ),
      (new TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser("hello"),
       true,
       "OTHERhelloOTHER",
       5,
       10
      ),
      (new TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser("hello"),
       true,
       "OTHERhello",
       5,
       10
      ),
      (new TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser("hello"), true, "", 0, ~0),
      (new TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser("hello"), true, "a", 1, ~1),
      (new TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser("hello"), true, "HELLO", 0, ~0),
      (new TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser("hello"), true, "hlloo", 0, ~0),
      (new TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser("hello"),
       true,
       "OTHERhllooOTHER",
       5,
       ~5
      ),
      (new TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser("hello"),
       true,
       "OTHERhlloo",
       5,
       ~5
      ),
      (new TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser("hello"), true, "h", 0, ~0),
      (new TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser("hello"), true, "OTHERh", 5, ~5),
      (new TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser("hello"), false, "hello", 0, 5),
      (new TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser("hello"), false, "HELLO", 0, 5),
      (new TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser("hello"), false, "HelLo", 0, 5),
      (new TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser("hello"), false, "HelLO", 0, 5)
    )

  test("test_parse_success") {
    data_success.foreach {
      case (pp, caseSensitive, text, pos, expectedPos) =>
        parseContext.setCaseSensitive(caseSensitive)
        val result: Int = pp.parse(parseContext, text, pos)
        assertEquals(result, expectedPos)
        assertEquals(parseContext.toParsed.query(TemporalQueries.chronology), null)
        assertEquals(parseContext.toParsed.query(TemporalQueries.zoneId), null)
      case _                                           =>
        fail()
    }
  }

  val data_error
    : List[(TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser, String, Int, Class[_])] =
    List(
      (new TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser("hello"),
       "hello",
       -1,
       classOf[IndexOutOfBoundsException]
      ),
      (new TTBPDateTimeFormatterBuilder.StringLiteralPrinterParser("hello"),
       "hello",
       6,
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
}
