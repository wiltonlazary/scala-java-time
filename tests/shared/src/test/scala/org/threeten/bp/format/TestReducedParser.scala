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
import org.threeten.bp.temporal.ChronoField.DAY_OF_YEAR
import org.threeten.bp.temporal.ChronoField.YEAR
import org.threeten.bp.format.internal.TTBPDateTimeFormatterBuilder
import org.threeten.bp.temporal.TemporalField
import org.threeten.bp.temporal.TemporalQueries

/** Test ReducedPrinterParser. */
class TestReducedParser extends AnyFunSuite with GenTestPrinterParser with AssertionsHelper {
  val data_error
    : List[(TTBPDateTimeFormatterBuilder.ReducedPrinterParser, String, Int, Class[_])] = {
    List(
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "12",
       -1,
       classOf[IndexOutOfBoundsException]),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "12",
       3,
       classOf[IndexOutOfBoundsException])
    )
  }

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
      case _ =>
        fail()
    }
  }

  test("test_parse_fieldRangeIgnored") {
    val pp: TTBPDateTimeFormatterBuilder.ReducedPrinterParser =
      new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(DAY_OF_YEAR, 3, 3, 10, null)
    val newPos: Int = pp.parse(parseContext, "456", 0)
    assertEquals(newPos, 3)
    assertParsed(DAY_OF_YEAR, 456L)
  }

  val provider_parse
    : List[(TTBPDateTimeFormatterBuilder.ReducedPrinterParser, String, Int, Int, Integer)] = {
    List(
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 1, 1, 2010, null),
       "-0",
       0,
       ~0,
       null),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "Xxx12Xxx",
       3,
       5,
       2012),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "12345",
       0,
       2,
       2012),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "12-45",
       0,
       2,
       2012),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "0",
       0,
       ~0,
       null),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "1",
       0,
       ~0,
       null),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "1",
       1,
       ~1,
       null),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "1-2",
       0,
       ~0,
       null),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "9",
       0,
       ~0,
       null),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "A0",
       0,
       ~0,
       null),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "0A",
       0,
       ~0,
       null),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "  1",
       0,
       ~0,
       null),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "-1",
       0,
       ~0,
       null),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "-10",
       0,
       ~0,
       null),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 1, 1, 2010, null),
       "0",
       0,
       1,
       2010),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 1, 1, 2010, null),
       "9",
       0,
       1,
       2019),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 1, 1, 2010, null),
       "10",
       0,
       1,
       2011),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 1, 1, 2005, null),
       "0",
       0,
       1,
       2010),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 1, 1, 2005, null),
       "4",
       0,
       1,
       2014),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 1, 1, 2005, null),
       "5",
       0,
       1,
       2005),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 1, 1, 2005, null),
       "9",
       0,
       1,
       2009),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 1, 1, 2005, null),
       "10",
       0,
       1,
       2011),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "00",
       0,
       2,
       2100),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "09",
       0,
       2,
       2109),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "10",
       0,
       2,
       2010),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "99",
       0,
       2,
       2099),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "100",
       0,
       2,
       2010),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, -2005, null),
       "05",
       0,
       2,
       -2005),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, -2005, null),
       "00",
       0,
       2,
       -2000),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, -2005, null),
       "99",
       0,
       2,
       -1999),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, -2005, null),
       "06",
       0,
       2,
       -1906),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, -2005, null),
       "100",
       0,
       2,
       -1910)
    )
  }

  test("test_parse") {
    provider_parse.foreach {
      case (pp, input, pos, parseLen, parseVal) =>
        super.beforeEach()
        val newPos: Int = pp.parse(parseContext, input, pos)
        assertEquals(newPos, parseLen)
        assertParsed(YEAR, if (parseVal != null) parseVal.toLong else null)
      case _ =>
        fail()
    }
  }

  val provider_parseLenient
    : List[(TTBPDateTimeFormatterBuilder.ReducedPrinterParser, String, Int, Int, Integer)] = {
    List(
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 1, 1, 2010, null),
       "-0",
       0,
       ~0,
       null),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "Xxx12Xxx",
       3,
       5,
       2012),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "12345",
       0,
       5,
       12345),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "12-45",
       0,
       2,
       2012),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null), "0", 0, 1, 0),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null), "1", 0, 1, 1),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "1",
       1,
       ~1,
       null),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "1-2",
       0,
       1,
       1),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null), "9", 0, 1, 9),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "A0",
       0,
       ~0,
       null),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "0A",
       0,
       1,
       0),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "  1",
       0,
       ~0,
       null),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "-1",
       0,
       ~0,
       null),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "-10",
       0,
       ~0,
       null),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 1, 1, 2010, null),
       "0",
       0,
       1,
       2010),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 1, 1, 2010, null),
       "9",
       0,
       1,
       2019),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 1, 1, 2010, null),
       "10",
       0,
       2,
       10),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 1, 1, 2005, null),
       "0",
       0,
       1,
       2010),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 1, 1, 2005, null),
       "4",
       0,
       1,
       2014),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 1, 1, 2005, null),
       "5",
       0,
       1,
       2005),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 1, 1, 2005, null),
       "9",
       0,
       1,
       2009),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 1, 1, 2005, null),
       "10",
       0,
       2,
       10),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "00",
       0,
       2,
       2100),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "09",
       0,
       2,
       2109),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "10",
       0,
       2,
       2010),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "99",
       0,
       2,
       2099),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null),
       "100",
       0,
       3,
       100),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, -2005, null),
       "05",
       0,
       2,
       -2005),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, -2005, null),
       "00",
       0,
       2,
       -2000),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, -2005, null),
       "99",
       0,
       2,
       -1999),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, -2005, null),
       "06",
       0,
       2,
       -1906),
      (new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, -2005, null),
       "100",
       0,
       3,
       100)
    )
  }

  test("test_parseLenient") {
    provider_parseLenient.foreach {
      case (pp, input, pos, parseLen, parseVal) =>
        super.beforeEach()
        parseContext.setStrict(false)
        val newPos: Int = pp.parse(parseContext, input, pos)
        assertEquals(newPos, parseLen)
        assertParsed(YEAR, if (parseVal != null) parseVal.toLong else null)
      case _ =>
        fail()
    }
  }

  private def assertParsed(field: TemporalField, value: java.lang.Long): Unit =
    if (value == null)
      assertEquals(parseContext.getParsed(field), null)
    else
      assertEquals(parseContext.getParsed(field), value)
}
