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
package org.threeten.bp.chrono

import org.scalatest.funsuite.AnyFunSuite

import org.threeten.bp.AssertionsHelper
import org.threeten.bp.temporal.ChronoField.DAY_OF_MONTH
import org.threeten.bp.temporal.ChronoField.DAY_OF_YEAR
import org.threeten.bp.temporal.ChronoField.MONTH_OF_YEAR
import org.threeten.bp.temporal.ChronoField.YEAR
import org.threeten.bp.temporal.ChronoField.YEAR_OF_ERA
import org.threeten.bp.DateTimeException
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Month
import org.threeten.bp.temporal.ChronoField
import org.threeten.bp.temporal.TemporalAdjusters
import org.threeten.bp.temporal.ValueRange

/** Test. */
object TestThaiBuddhistChronology {
  private val YDIFF: Int = 543
}

class TestThaiBuddhistChronology extends AnyFunSuite with AssertionsHelper {
  test("test_chrono_byName") {
    val c: Chronology    = ThaiBuddhistChronology.INSTANCE
    val test: Chronology = Chronology.of("ThaiBuddhist")
    assertNotNull(test, "The ThaiBuddhist calendar could not be found byName")
    assertEquals(test.getId, "ThaiBuddhist", "ID mismatch")
    assertEquals(test.getCalendarType, "buddhist", "Type mismatch")
    assertEquals(test, c)
  }

  val data_samples: List[(ChronoLocalDate, LocalDate)] = {
    List(
      (ThaiBuddhistChronology.INSTANCE.date(1 + TestThaiBuddhistChronology.YDIFF, 1, 1),
       LocalDate.of(1, 1, 1)),
      (ThaiBuddhistChronology.INSTANCE.date(1 + TestThaiBuddhistChronology.YDIFF, 1, 2),
       LocalDate.of(1, 1, 2)),
      (ThaiBuddhistChronology.INSTANCE.date(1 + TestThaiBuddhistChronology.YDIFF, 1, 3),
       LocalDate.of(1, 1, 3)),
      (ThaiBuddhistChronology.INSTANCE.date(2 + TestThaiBuddhistChronology.YDIFF, 1, 1),
       LocalDate.of(2, 1, 1)),
      (ThaiBuddhistChronology.INSTANCE.date(3 + TestThaiBuddhistChronology.YDIFF, 1, 1),
       LocalDate.of(3, 1, 1)),
      (ThaiBuddhistChronology.INSTANCE.date(3 + TestThaiBuddhistChronology.YDIFF, 12, 6),
       LocalDate.of(3, 12, 6)),
      (ThaiBuddhistChronology.INSTANCE.date(4 + TestThaiBuddhistChronology.YDIFF, 1, 1),
       LocalDate.of(4, 1, 1)),
      (ThaiBuddhistChronology.INSTANCE.date(4 + TestThaiBuddhistChronology.YDIFF, 7, 3),
       LocalDate.of(4, 7, 3)),
      (ThaiBuddhistChronology.INSTANCE.date(4 + TestThaiBuddhistChronology.YDIFF, 7, 4),
       LocalDate.of(4, 7, 4)),
      (ThaiBuddhistChronology.INSTANCE.date(5 + TestThaiBuddhistChronology.YDIFF, 1, 1),
       LocalDate.of(5, 1, 1)),
      (ThaiBuddhistChronology.INSTANCE.date(1662 + TestThaiBuddhistChronology.YDIFF, 3, 3),
       LocalDate.of(1662, 3, 3)),
      (ThaiBuddhistChronology.INSTANCE.date(1728 + TestThaiBuddhistChronology.YDIFF, 10, 28),
       LocalDate.of(1728, 10, 28)),
      (ThaiBuddhistChronology.INSTANCE.date(1728 + TestThaiBuddhistChronology.YDIFF, 10, 29),
       LocalDate.of(1728, 10, 29)),
      (ThaiBuddhistChronology.INSTANCE.date(2555, 8, 29), LocalDate.of(2012, 8, 29))
    )
  }

  test("test_toLocalDate") {
    data_samples.foreach {
      case (jdate, iso) =>
        assertEquals(LocalDate.from(jdate), iso)
    }
  }

  test("test_fromCalendrical") {
    data_samples.foreach {
      case (jdate, iso) =>
        assertEquals(ThaiBuddhistChronology.INSTANCE.date(iso), jdate)
    }
  }

  val data_badDates: List[(Int, Int, Int)] = {
    List((1728, 0, 0),
         (1728, -1, 1),
         (1728, 0, 1),
         (1728, 14, 1),
         (1728, 15, 1),
         (1728, 1, -1),
         (1728, 1, 0),
         (1728, 1, 32),
         (1728, 12, -1),
         (1728, 12, 0),
         (1728, 12, 32))
  }

  test("test_badDates") {
    data_badDates.foreach {
      case (year, month, dom) =>
        assertThrows[DateTimeException] {
          ThaiBuddhistChronology.INSTANCE.date(year, month, dom)
        }
    }
  }

  test("test_adjust1") {
    val base: ChronoLocalDate = ThaiBuddhistChronology.INSTANCE.date(1728, 10, 29)
    val test: ChronoLocalDate = base.`with`(TemporalAdjusters.lastDayOfMonth)
    assertEquals(test, ThaiBuddhistChronology.INSTANCE.date(1728, 10, 31))
  }

  test("test_adjust2") {
    val base: ChronoLocalDate = ThaiBuddhistChronology.INSTANCE.date(1728, 12, 2)
    val test: ChronoLocalDate = base.`with`(TemporalAdjusters.lastDayOfMonth)
    assertEquals(test, ThaiBuddhistChronology.INSTANCE.date(1728, 12, 31))
  }

  test("test_withYear_BE") {
    val base: ChronoLocalDate = ThaiBuddhistChronology.INSTANCE.date(2555, 8, 29)
    val test: ChronoLocalDate = base.`with`(YEAR, 2554)
    assertEquals(test, ThaiBuddhistChronology.INSTANCE.date(2554, 8, 29))
  }

  test("test_withYear_BBE") {
    val base: ChronoLocalDate = ThaiBuddhistChronology.INSTANCE.date(-2554, 8, 29)
    val test: ChronoLocalDate = base.`with`(YEAR_OF_ERA, 2554)
    assertEquals(test, ThaiBuddhistChronology.INSTANCE.date(-2553, 8, 29))
  }

  test("test_withEra_BE") {
    val base: ChronoLocalDate = ThaiBuddhistChronology.INSTANCE.date(2555, 8, 29)
    val test: ChronoLocalDate = base.`with`(ChronoField.ERA, ThaiBuddhistEra.BE.getValue)
    assertEquals(test, ThaiBuddhistChronology.INSTANCE.date(2555, 8, 29))
  }

  test("test_withEra_BBE") {
    val base: ChronoLocalDate = ThaiBuddhistChronology.INSTANCE.date(-2554, 8, 29)
    val test: ChronoLocalDate = base.`with`(ChronoField.ERA, ThaiBuddhistEra.BEFORE_BE.getValue)
    assertEquals(test, ThaiBuddhistChronology.INSTANCE.date(-2554, 8, 29))
  }

  test("test_withEra_swap") {
    val base: ChronoLocalDate = ThaiBuddhistChronology.INSTANCE.date(-2554, 8, 29)
    val test: ChronoLocalDate = base.`with`(ChronoField.ERA, ThaiBuddhistEra.BE.getValue)
    assertEquals(test, ThaiBuddhistChronology.INSTANCE.date(2555, 8, 29))
  }

  test("test_adjust_toLocalDate") {
    val jdate: ChronoLocalDate = ThaiBuddhistChronology.INSTANCE.date(1726, 1, 4)
    val test: ChronoLocalDate  = jdate.`with`(LocalDate.of(2012, 7, 6))
    assertEquals(test, ThaiBuddhistChronology.INSTANCE.date(2555, 7, 6))
  }

  test("test_adjust_toMonth") {
    assertThrows[DateTimeException] {
      val jdate: ChronoLocalDate = ThaiBuddhistChronology.INSTANCE.date(1726, 1, 4)
      jdate.`with`(Month.APRIL)
    }
  }

  test("test_LocalDate_adjustToBuddhistDate") {
    val jdate: ChronoLocalDate = ThaiBuddhistChronology.INSTANCE.date(2555, 10, 29)
    val test: LocalDate        = LocalDate.MIN.`with`(jdate)
    assertEquals(test, LocalDate.of(2012, 10, 29))
  }

  test("test_LocalDateTime_adjustToBuddhistDate") {
    val jdate: ChronoLocalDate = ThaiBuddhistChronology.INSTANCE.date(2555, 10, 29)
    val test: LocalDateTime    = LocalDateTime.MIN.`with`(jdate)
    assertEquals(test, LocalDateTime.of(2012, 10, 29, 0, 0))
  }

  val data_toString: List[(ChronoLocalDate, String)] = {
    List(
      (ThaiBuddhistChronology.INSTANCE.date(544, 1, 1), "ThaiBuddhist BE 544-01-01"),
      (ThaiBuddhistChronology.INSTANCE.date(2271, 10, 28), "ThaiBuddhist BE 2271-10-28"),
      (ThaiBuddhistChronology.INSTANCE.date(2271, 10, 29), "ThaiBuddhist BE 2271-10-29"),
      (ThaiBuddhistChronology.INSTANCE.date(2270, 12, 5), "ThaiBuddhist BE 2270-12-05"),
      (ThaiBuddhistChronology.INSTANCE.date(2270, 12, 6), "ThaiBuddhist BE 2270-12-06")
    )
  }

  test("test_toString") {
    data_toString.foreach {
      case (jdate, expected) =>
        assertEquals(jdate.toString, expected)
    }
  }

  test("test_Chrono_range") {
    val minYear: Long = LocalDate.MIN.getYear + TestThaiBuddhistChronology.YDIFF
    val maxYear: Long = LocalDate.MAX.getYear + TestThaiBuddhistChronology.YDIFF
    assertEquals(ThaiBuddhistChronology.INSTANCE.range(YEAR), ValueRange.of(minYear, maxYear))
    assertEquals(ThaiBuddhistChronology.INSTANCE.range(YEAR_OF_ERA),
                 ValueRange.of(1, -minYear + 1, maxYear))
    assertEquals(ThaiBuddhistChronology.INSTANCE.range(DAY_OF_MONTH), DAY_OF_MONTH.range)
    assertEquals(ThaiBuddhistChronology.INSTANCE.range(DAY_OF_YEAR), DAY_OF_YEAR.range)
    assertEquals(ThaiBuddhistChronology.INSTANCE.range(MONTH_OF_YEAR), MONTH_OF_YEAR.range)
  }

  test("test_equals_true") {
    assertTrue(ThaiBuddhistChronology.INSTANCE == ThaiBuddhistChronology.INSTANCE)
  }

  test("test_equals_false") {
    assertFalse(ThaiBuddhistChronology.INSTANCE == IsoChronology.INSTANCE)
  }
}
