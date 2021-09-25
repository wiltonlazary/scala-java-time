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

import org.scalatest.funsuite.AnyFunSuite
import org.threeten.bp.Month.{ DECEMBER, JANUARY, JUNE }
import org.threeten.bp.chrono.IsoChronology
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoField.MONTH_OF_YEAR
import org.threeten.bp.temporal._

/** Test Month. */
object TestMonth {
  val MAX_LENGTH: Int = 12
}

class TestMonth extends AnyFunSuite with GenDateTimeTest with AssertionsHelper {
  protected def samples: List[TemporalAccessor]    =
    List(JANUARY, JUNE, DECEMBER)

  protected def validFields: List[TemporalField]   =
    List(MONTH_OF_YEAR)

  protected def invalidFields: List[TemporalField] = {
    val list: List[TemporalField] = List(ChronoField.values: _*)
    (list :+ JulianFields.JULIAN_DAY :+ JulianFields.MODIFIED_JULIAN_DAY :+ JulianFields.RATA_DIE)
      .filterNot(validFields.contains)
  }

  test("factory_int_singleton") {
    var i: Int = 1
    while (i <= TestMonth.MAX_LENGTH) {
      val test: Month = Month.of(i)
      assertEquals(test.getValue, i)
      i += 1
    }
  }

  test("factory_int_tooLow") {
    assertThrows[DateTimeException] {
      Month.of(0)
    }
  }

  test("factory_int_tooHigh") {
    assertThrows[DateTimeException] {
      Month.of(13)
    }
  }

  test("factory_CalendricalObject") {
    assertEquals(Month.from(LocalDate.of(2011, 6, 6)), JUNE)
  }

  test("factory_CalendricalObject_invalid_noDerive") {
    assertThrows[DateTimeException] {
      Month.from(LocalTime.of(12, 30))
    }
  }

  test("factory_CalendricalObject_null") {
    assertThrows[NullPointerException] {
      Month.from(null.asInstanceOf[TemporalAccessor])
    }
  }

  test("get_TemporalField") {
    assertEquals(Month.JULY.get(ChronoField.MONTH_OF_YEAR), 7)
  }

  test("getLong_TemporalField") {
    assertEquals(Month.JULY.getLong(ChronoField.MONTH_OF_YEAR), 7)
  }

  test("query") {
    assertEquals(Month.JUNE.query(TemporalQueries.chronology), IsoChronology.INSTANCE)
    assertEquals(Month.JUNE.query(TemporalQueries.localDate), null)
    assertEquals(Month.JUNE.query(TemporalQueries.localTime), null)
    assertEquals(Month.JUNE.query(TemporalQueries.offset), null)
    assertEquals(Month.JUNE.query(TemporalQueries.precision), ChronoUnit.MONTHS)
    assertEquals(Month.JUNE.query(TemporalQueries.zone), null)
    assertEquals(Month.JUNE.query(TemporalQueries.zoneId), null)
  }

  test("query_null") {
    assertThrows[Platform.NPE] {
      Month.JUNE.query(null)
    }
  }

  test("getDisplayName") {
    assertEquals(Month.JANUARY.getDisplayName(TextStyle.SHORT, Locale.US), "Jan")
  }

  test("getDisplayName_nullStyle") {
    assertThrows[NullPointerException] {
      Month.JANUARY.getDisplayName(null, Locale.US)
    }
  }

  test("getDisplayName_nullLocale") {
    assertThrows[NullPointerException] {
      Month.JANUARY.getDisplayName(TextStyle.FULL, null)
    }
  }

  def data_plus: List[List[Int]] =
    List(
      List(1, -13, 12),
      List(1, -12, 1),
      List(1, -11, 2),
      List(1, -10, 3),
      List(1, -9, 4),
      List(1, -8, 5),
      List(1, -7, 6),
      List(1, -6, 7),
      List(1, -5, 8),
      List(1, -4, 9),
      List(1, -3, 10),
      List(1, -2, 11),
      List(1, -1, 12),
      List(1, 0, 1),
      List(1, 1, 2),
      List(1, 2, 3),
      List(1, 3, 4),
      List(1, 4, 5),
      List(1, 5, 6),
      List(1, 6, 7),
      List(1, 7, 8),
      List(1, 8, 9),
      List(1, 9, 10),
      List(1, 10, 11),
      List(1, 11, 12),
      List(1, 12, 1),
      List(1, 13, 2),
      List(1, 1, 2),
      List(2, 1, 3),
      List(3, 1, 4),
      List(4, 1, 5),
      List(5, 1, 6),
      List(6, 1, 7),
      List(7, 1, 8),
      List(8, 1, 9),
      List(9, 1, 10),
      List(10, 1, 11),
      List(11, 1, 12),
      List(12, 1, 1),
      List(1, -1, 12),
      List(2, -1, 1),
      List(3, -1, 2),
      List(4, -1, 3),
      List(5, -1, 4),
      List(6, -1, 5),
      List(7, -1, 6),
      List(8, -1, 7),
      List(9, -1, 8),
      List(10, -1, 9),
      List(11, -1, 10),
      List(12, -1, 11)
    )

  test("plus_long") {
    data_plus.foreach {
      case base :: amount :: expected :: Nil =>
        assertEquals(Month.of(base).plus(amount), Month.of(expected))
      case _                                 =>
        fail()
    }
  }

  def data_minus: List[List[Int]] =
    List(
      List(1, -13, 2),
      List(1, -12, 1),
      List(1, -11, 12),
      List(1, -10, 11),
      List(1, -9, 10),
      List(1, -8, 9),
      List(1, -7, 8),
      List(1, -6, 7),
      List(1, -5, 6),
      List(1, -4, 5),
      List(1, -3, 4),
      List(1, -2, 3),
      List(1, -1, 2),
      List(1, 0, 1),
      List(1, 1, 12),
      List(1, 2, 11),
      List(1, 3, 10),
      List(1, 4, 9),
      List(1, 5, 8),
      List(1, 6, 7),
      List(1, 7, 6),
      List(1, 8, 5),
      List(1, 9, 4),
      List(1, 10, 3),
      List(1, 11, 2),
      List(1, 12, 1),
      List(1, 13, 12)
    )

  test("minus_long") {
    data_minus.foreach {
      case base :: amount :: expected :: Nil =>
        assertEquals(Month.of(base).minus(amount), Month.of(expected))
      case _                                 =>
        fail()
    }
  }

  test("length_boolean_notLeapYear") {
    assertEquals(Month.JANUARY.length(false), 31)
    assertEquals(Month.FEBRUARY.length(false), 28)
    assertEquals(Month.MARCH.length(false), 31)
    assertEquals(Month.APRIL.length(false), 30)
    assertEquals(Month.MAY.length(false), 31)
    assertEquals(Month.JUNE.length(false), 30)
    assertEquals(Month.JULY.length(false), 31)
    assertEquals(Month.AUGUST.length(false), 31)
    assertEquals(Month.SEPTEMBER.length(false), 30)
    assertEquals(Month.OCTOBER.length(false), 31)
    assertEquals(Month.NOVEMBER.length(false), 30)
    assertEquals(Month.DECEMBER.length(false), 31)
  }

  test("length_boolean_leapYear") {
    assertEquals(Month.JANUARY.length(true), 31)
    assertEquals(Month.FEBRUARY.length(true), 29)
    assertEquals(Month.MARCH.length(true), 31)
    assertEquals(Month.APRIL.length(true), 30)
    assertEquals(Month.MAY.length(true), 31)
    assertEquals(Month.JUNE.length(true), 30)
    assertEquals(Month.JULY.length(true), 31)
    assertEquals(Month.AUGUST.length(true), 31)
    assertEquals(Month.SEPTEMBER.length(true), 30)
    assertEquals(Month.OCTOBER.length(true), 31)
    assertEquals(Month.NOVEMBER.length(true), 30)
    assertEquals(Month.DECEMBER.length(true), 31)
  }

  test("minLength") {
    assertEquals(Month.JANUARY.minLength, 31)
    assertEquals(Month.FEBRUARY.minLength, 28)
    assertEquals(Month.MARCH.minLength, 31)
    assertEquals(Month.APRIL.minLength, 30)
    assertEquals(Month.MAY.minLength, 31)
    assertEquals(Month.JUNE.minLength, 30)
    assertEquals(Month.JULY.minLength, 31)
    assertEquals(Month.AUGUST.minLength, 31)
    assertEquals(Month.SEPTEMBER.minLength, 30)
    assertEquals(Month.OCTOBER.minLength, 31)
    assertEquals(Month.NOVEMBER.minLength, 30)
    assertEquals(Month.DECEMBER.minLength, 31)
  }

  test("maxLength") {
    assertEquals(Month.JANUARY.maxLength, 31)
    assertEquals(Month.FEBRUARY.maxLength, 29)
    assertEquals(Month.MARCH.maxLength, 31)
    assertEquals(Month.APRIL.maxLength, 30)
    assertEquals(Month.MAY.maxLength, 31)
    assertEquals(Month.JUNE.maxLength, 30)
    assertEquals(Month.JULY.maxLength, 31)
    assertEquals(Month.AUGUST.maxLength, 31)
    assertEquals(Month.SEPTEMBER.maxLength, 30)
    assertEquals(Month.OCTOBER.maxLength, 31)
    assertEquals(Month.NOVEMBER.maxLength, 30)
    assertEquals(Month.DECEMBER.maxLength, 31)
  }

  test("firstDayOfYear_notLeapYear") {
    assertEquals(Month.JANUARY.firstDayOfYear(false), 1)
    assertEquals(Month.FEBRUARY.firstDayOfYear(false), 1 + 31)
    assertEquals(Month.MARCH.firstDayOfYear(false), 1 + 31 + 28)
    assertEquals(Month.APRIL.firstDayOfYear(false), 1 + 31 + 28 + 31)
    assertEquals(Month.MAY.firstDayOfYear(false), 1 + 31 + 28 + 31 + 30)
    assertEquals(Month.JUNE.firstDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31)
    assertEquals(Month.JULY.firstDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31 + 30)
    assertEquals(Month.AUGUST.firstDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31 + 30 + 31)
    assertEquals(Month.SEPTEMBER.firstDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31)
    assertEquals(Month.OCTOBER.firstDayOfYear(false),
                 1 + 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30
    )
    assertEquals(Month.NOVEMBER.firstDayOfYear(false),
                 1 + 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31
    )
    assertEquals(Month.DECEMBER.firstDayOfYear(false),
                 1 + 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30
    )
  }

  test("firstDayOfYear_leapYear") {
    assertEquals(Month.JANUARY.firstDayOfYear(true), 1)
    assertEquals(Month.FEBRUARY.firstDayOfYear(true), 1 + 31)
    assertEquals(Month.MARCH.firstDayOfYear(true), 1 + 31 + 29)
    assertEquals(Month.APRIL.firstDayOfYear(true), 1 + 31 + 29 + 31)
    assertEquals(Month.MAY.firstDayOfYear(true), 1 + 31 + 29 + 31 + 30)
    assertEquals(Month.JUNE.firstDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31)
    assertEquals(Month.JULY.firstDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31 + 30)
    assertEquals(Month.AUGUST.firstDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31 + 30 + 31)
    assertEquals(Month.SEPTEMBER.firstDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31)
    assertEquals(Month.OCTOBER.firstDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30)
    assertEquals(Month.NOVEMBER.firstDayOfYear(true),
                 1 + 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31
    )
    assertEquals(Month.DECEMBER.firstDayOfYear(true),
                 1 + 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30
    )
  }

  test("firstMonthOfQuarter") {
    assertEquals(Month.JANUARY.firstMonthOfQuarter, Month.JANUARY)
    assertEquals(Month.FEBRUARY.firstMonthOfQuarter, Month.JANUARY)
    assertEquals(Month.MARCH.firstMonthOfQuarter, Month.JANUARY)
    assertEquals(Month.APRIL.firstMonthOfQuarter, Month.APRIL)
    assertEquals(Month.MAY.firstMonthOfQuarter, Month.APRIL)
    assertEquals(Month.JUNE.firstMonthOfQuarter, Month.APRIL)
    assertEquals(Month.JULY.firstMonthOfQuarter, Month.JULY)
    assertEquals(Month.AUGUST.firstMonthOfQuarter, Month.JULY)
    assertEquals(Month.SEPTEMBER.firstMonthOfQuarter, Month.JULY)
    assertEquals(Month.OCTOBER.firstMonthOfQuarter, Month.OCTOBER)
    assertEquals(Month.NOVEMBER.firstMonthOfQuarter, Month.OCTOBER)
    assertEquals(Month.DECEMBER.firstMonthOfQuarter, Month.OCTOBER)
  }

  test("toString") {
    assertEquals(Month.JANUARY.toString, "JANUARY")
    assertEquals(Month.FEBRUARY.toString, "FEBRUARY")
    assertEquals(Month.MARCH.toString, "MARCH")
    assertEquals(Month.APRIL.toString, "APRIL")
    assertEquals(Month.MAY.toString, "MAY")
    assertEquals(Month.JUNE.toString, "JUNE")
    assertEquals(Month.JULY.toString, "JULY")
    assertEquals(Month.AUGUST.toString, "AUGUST")
    assertEquals(Month.SEPTEMBER.toString, "SEPTEMBER")
    assertEquals(Month.OCTOBER.toString, "OCTOBER")
    assertEquals(Month.NOVEMBER.toString, "NOVEMBER")
    assertEquals(Month.DECEMBER.toString, "DECEMBER")
  }

  test("enum") {
    assertEquals(Month.valueOf("JANUARY"), Month.JANUARY)
    assertEquals(Month.values.apply(0), Month.JANUARY)
  }
}
