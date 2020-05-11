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
import org.threeten.bp.temporal.ChronoField.YEAR
import org.threeten.bp.DateTimeException
import org.threeten.bp.LocalDate
import org.threeten.bp.format.internal.TTBPDateTimeFormatterBuilder
import org.threeten.bp.temporal.MockFieldValue

/** Test ReducedPrinterParser. */
class TestReducedPrinter extends AnyFunSuite with GenTestPrinterParser with AssertionsHelper {
  test("test_print_emptyCalendrical") {
    assertThrows[DateTimeException] {
      val pp: TTBPDateTimeFormatterBuilder.ReducedPrinterParser =
        new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null)
      pp.print(printEmptyContext, buf)
    }
  }

  test("test_print_append") {
    printContext.setDateTime(LocalDate.of(2012, 1, 1))
    val pp: TTBPDateTimeFormatterBuilder.ReducedPrinterParser =
      new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2010, null)
    buf.append("EXISTING")
    pp.print(printContext, buf)
    assertEquals(buf.toString, "EXISTING12")
  }

  val provider_pivot: List[List[Any]] =
    List(
      List(1, 2010, 2010, "0"),
      List(1, 2010, 2011, "1"),
      List(1, 2010, 2012, "2"),
      List(1, 2010, 2013, "3"),
      List(1, 2010, 2014, "4"),
      List(1, 2010, 2015, "5"),
      List(1, 2010, 2016, "6"),
      List(1, 2010, 2017, "7"),
      List(1, 2010, 2018, "8"),
      List(1, 2010, 2019, "9"),
      List(1, 2010, 2009, "9"),
      List(1, 2010, 2020, "0"),
      List(2, 2010, 2010, "10"),
      List(2, 2010, 2011, "11"),
      List(2, 2010, 2021, "21"),
      List(2, 2010, 2099, "99"),
      List(2, 2010, 2100, "00"),
      List(2, 2010, 2109, "09"),
      List(2, 2010, 2009, "09"),
      List(2, 2010, 2110, "10"),
      List(2, 2005, 2005, "05"),
      List(2, 2005, 2099, "99"),
      List(2, 2005, 2100, "00"),
      List(2, 2005, 2104, "04"),
      List(2, 2005, 2004, "04"),
      List(2, 2005, 2105, "05"),
      List(3, 2005, 2005, "005"),
      List(3, 2005, 2099, "099"),
      List(3, 2005, 2100, "100"),
      List(3, 2005, 2999, "999"),
      List(3, 2005, 3000, "000"),
      List(3, 2005, 3004, "004"),
      List(3, 2005, 2004, "004"),
      List(3, 2005, 3005, "005"),
      List(9, 2005, 2005, "000002005"),
      List(9, 2005, 2099, "000002099"),
      List(9, 2005, 2100, "000002100"),
      List(9, 2005, 999999999, "999999999"),
      List(9, 2005, 1000000000, "000000000"),
      List(9, 2005, 1000002004, "000002004"),
      List(9, 2005, 2004, "000002004"),
      List(9, 2005, 1000002005, "000002005"),
      List(2, -2005, -2005, "05"),
      List(2, -2005, -2000, "00"),
      List(2, -2005, -1999, "99"),
      List(2, -2005, -1904, "04"),
      List(2, -2005, -2006, "06"),
      List(2, -2005, -1905, "05")
    )

  test("test_pivot") {
    provider_pivot.foreach {
      case (width: Int) :: (baseValue: Int) :: (value: Int) :: (result: String) :: Nil =>
        super.beforeEach()
        printContext.setDateTime(new MockFieldValue(YEAR, value))
        val pp: TTBPDateTimeFormatterBuilder.ReducedPrinterParser =
          new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, width, width, baseValue, null)
        try {
          pp.print(printContext, buf)
          if (result == null)
            fail("Expected exception")
          assertEquals(buf.toString, result)
        } catch {
          case ex: DateTimeException =>
            if (result == null || value < 0)
              assertEquals(ex.getMessage.contains(YEAR.toString), true)
            else
              throw ex
        }
      case _                                                                           =>
        fail()
    }
  }

  test("test_toString") {
    val pp: TTBPDateTimeFormatterBuilder.ReducedPrinterParser =
      new TTBPDateTimeFormatterBuilder.ReducedPrinterParser(YEAR, 2, 2, 2005, null)
    assertEquals(pp.toString, "ReducedValue(Year,2,2,2005)")
  }
}
