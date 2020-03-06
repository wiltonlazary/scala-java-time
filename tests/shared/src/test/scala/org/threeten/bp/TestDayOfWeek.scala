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
import org.threeten.bp.DayOfWeek.{ MONDAY, SUNDAY, WEDNESDAY }
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoField.DAY_OF_WEEK
import org.threeten.bp.temporal._

/** Test DayOfWeek. */
class TestDayOfWeek extends AnyFunSuite with GenDateTimeTest with AssertionsHelper {

  protected def samples: List[TemporalAccessor] =
    List(MONDAY, WEDNESDAY, SUNDAY)

  protected def validFields: List[TemporalField] =
    List(DAY_OF_WEEK)

  protected def invalidFields: List[TemporalField] = {
    val list: List[TemporalField] = List(ChronoField.values: _*)
    (list :+ JulianFields.JULIAN_DAY :+ JulianFields.MODIFIED_JULIAN_DAY :+ JulianFields.RATA_DIE)
      .filterNot(validFields.contains)
  }

  test("factory_int_singleton") {
    var i: Int = 1
    while (i <= 7) {
      val test: DayOfWeek = DayOfWeek.of(i)
      assertEquals(test.getValue, i)
      assertSame(DayOfWeek.of(i), test)
      i += 1
    }
  }

  test("factory_int_valueTooLow") {
    assertThrows[DateTimeException] {
      DayOfWeek.of(0)
    }
  }

  test("factory_int_valueTooHigh") {
    assertThrows[DateTimeException] {
      DayOfWeek.of(8)
    }
  }

  test("factory_CalendricalObject") {
    assertEquals(DayOfWeek.from(LocalDate.of(2011, 6, 6)), DayOfWeek.MONDAY)
  }

  test("factory_CalendricalObject_invalid_noDerive") {
    assertThrows[DateTimeException] {
      DayOfWeek.from(LocalTime.of(12, 30))
    }
  }

  test("factory_CalendricalObject_null") {
    assertThrows[Platform.NPE] {
      DayOfWeek.from(null.asInstanceOf[TemporalAccessor])
    }
  }

  test("get_TemporalField") {
    assertEquals(DayOfWeek.WEDNESDAY.getLong(ChronoField.DAY_OF_WEEK), 3)
  }

  test("getLong_TemporalField") {
    assertEquals(DayOfWeek.WEDNESDAY.getLong(ChronoField.DAY_OF_WEEK), 3)
  }

  test("query") {
    assertEquals(DayOfWeek.FRIDAY.query(TemporalQueries.chronology), null)
    assertEquals(DayOfWeek.FRIDAY.query(TemporalQueries.localDate), null)
    assertEquals(DayOfWeek.FRIDAY.query(TemporalQueries.localTime), null)
    assertEquals(DayOfWeek.FRIDAY.query(TemporalQueries.offset), null)
    assertEquals(DayOfWeek.FRIDAY.query(TemporalQueries.precision), ChronoUnit.DAYS)
    assertEquals(DayOfWeek.FRIDAY.query(TemporalQueries.zone), null)
    assertEquals(DayOfWeek.FRIDAY.query(TemporalQueries.zoneId), null)
  }

  test("query_null") {
    assertThrows[Platform.NPE] {
      DayOfWeek.FRIDAY.query(null)
    }
  }

  test("getDisplayName") {
    assertEquals(DayOfWeek.MONDAY.getDisplayName(TextStyle.SHORT, Locale.US), "Mon")
  }

  test("getDisplayName_nullStyle") {
    assertThrows[NullPointerException] {
      DayOfWeek.MONDAY.getDisplayName(null, Locale.US)
    }
  }

  test("getDisplayName_nullLocale") {
    assertThrows[NullPointerException] {
      DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL, null)
    }
  }

  def data_plus: List[List[Long]] =
    List(
      List(1, -8, 7),
      List(1, -7, 1),
      List(1, -6, 2),
      List(1, -5, 3),
      List(1, -4, 4),
      List(1, -3, 5),
      List(1, -2, 6),
      List(1, -1, 7),
      List(1, 0, 1),
      List(1, 1, 2),
      List(1, 2, 3),
      List(1, 3, 4),
      List(1, 4, 5),
      List(1, 5, 6),
      List(1, 6, 7),
      List(1, 7, 1),
      List(1, 8, 2),
      List(1, 1, 2),
      List(2, 1, 3),
      List(3, 1, 4),
      List(4, 1, 5),
      List(5, 1, 6),
      List(6, 1, 7),
      List(7, 1, 1),
      List(1, -1, 7),
      List(2, -1, 1),
      List(3, -1, 2),
      List(4, -1, 3),
      List(5, -1, 4),
      List(6, -1, 5),
      List(7, -1, 6)
    )

  test("plus_long") {
    data_plus.foreach {
      case (base: Long) :: (amount: Long) :: (expected: Long) :: Nil =>
        assertEquals(DayOfWeek.of(base.toInt).plus(amount), DayOfWeek.of(expected.toInt))
      case _ =>
        fail()
    }
  }

  def data_minus: List[List[Long]] =
    List(
      List(1, -8, 2),
      List(1, -7, 1),
      List(1, -6, 7),
      List(1, -5, 6),
      List(1, -4, 5),
      List(1, -3, 4),
      List(1, -2, 3),
      List(1, -1, 2),
      List(1, 0, 1),
      List(1, 1, 7),
      List(1, 2, 6),
      List(1, 3, 5),
      List(1, 4, 4),
      List(1, 5, 3),
      List(1, 6, 2),
      List(1, 7, 1),
      List(1, 8, 7)
    )

  test("minus_long") {
    data_minus.foreach {
      case (base: Long) :: (amount: Long) :: (expected: Long) :: Nil =>
        assertEquals(DayOfWeek.of(base.toInt).minus(amount), DayOfWeek.of(expected.toInt))
      case _ =>
        fail()
    }
  }

  test("adjustInto") {
    assertEquals(DayOfWeek.MONDAY.adjustInto(LocalDate.of(2012, 9, 2)), LocalDate.of(2012, 8, 27))
    assertEquals(DayOfWeek.MONDAY.adjustInto(LocalDate.of(2012, 9, 3)), LocalDate.of(2012, 9, 3))
    assertEquals(DayOfWeek.MONDAY.adjustInto(LocalDate.of(2012, 9, 4)), LocalDate.of(2012, 9, 3))
    assertEquals(DayOfWeek.MONDAY.adjustInto(LocalDate.of(2012, 9, 10)), LocalDate.of(2012, 9, 10))
    assertEquals(DayOfWeek.MONDAY.adjustInto(LocalDate.of(2012, 9, 11)), LocalDate.of(2012, 9, 10))
  }

  test("adjustInto_null") {
    assertThrows[NullPointerException] {
      DayOfWeek.MONDAY.adjustInto(null.asInstanceOf[Temporal])
    }
  }

  test("toString") {
    assertEquals(DayOfWeek.MONDAY.toString, "MONDAY")
    assertEquals(DayOfWeek.TUESDAY.toString, "TUESDAY")
    assertEquals(DayOfWeek.WEDNESDAY.toString, "WEDNESDAY")
    assertEquals(DayOfWeek.THURSDAY.toString, "THURSDAY")
    assertEquals(DayOfWeek.FRIDAY.toString, "FRIDAY")
    assertEquals(DayOfWeek.SATURDAY.toString, "SATURDAY")
    assertEquals(DayOfWeek.SUNDAY.toString, "SUNDAY")
  }

  test("enum") {
    assertEquals(DayOfWeek.valueOf("MONDAY"), DayOfWeek.MONDAY)
    assertEquals(DayOfWeek.values.apply(0), DayOfWeek.MONDAY)
  }
}
