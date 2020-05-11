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
import org.threeten.bp.DateTimeException
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.Month
import org.threeten.bp.ZoneOffset
import org.threeten.bp.temporal.ChronoUnit
import org.threeten.bp.temporal.TemporalAdjusters

/** Test. */
class TestMinguoChronology extends AnyFunSuite with AssertionsHelper {
  test("test_chrono_byName") {
    val c: Chronology    = MinguoChronology.INSTANCE
    val test: Chronology = Chronology.of("Minguo")
    assertNotNull(test, "The Minguo calendar could not be found byName")
    assertEquals(test.getId, "Minguo", "ID mismatch")
    assertEquals(test.getCalendarType, "roc", "Type mismatch")
    assertEquals(test, c)
  }

  val data_samples: List[(ChronoLocalDate, LocalDate)] =
    List(
      (MinguoChronology.INSTANCE.date(1, 1, 1), LocalDate.of(1912, 1, 1)),
      (MinguoChronology.INSTANCE.date(1, 1, 2), LocalDate.of(1912, 1, 2)),
      (MinguoChronology.INSTANCE.date(1, 1, 3), LocalDate.of(1912, 1, 3)),
      (MinguoChronology.INSTANCE.date(2, 1, 1), LocalDate.of(1913, 1, 1)),
      (MinguoChronology.INSTANCE.date(3, 1, 1), LocalDate.of(1914, 1, 1)),
      (MinguoChronology.INSTANCE.date(3, 12, 6), LocalDate.of(1914, 12, 6)),
      (MinguoChronology.INSTANCE.date(4, 1, 1), LocalDate.of(1915, 1, 1)),
      (MinguoChronology.INSTANCE.date(4, 7, 3), LocalDate.of(1915, 7, 3)),
      (MinguoChronology.INSTANCE.date(4, 7, 4), LocalDate.of(1915, 7, 4)),
      (MinguoChronology.INSTANCE.date(5, 1, 1), LocalDate.of(1916, 1, 1)),
      (MinguoChronology.INSTANCE.date(100, 3, 3), LocalDate.of(2011, 3, 3)),
      (MinguoChronology.INSTANCE.date(101, 10, 28), LocalDate.of(2012, 10, 28)),
      (MinguoChronology.INSTANCE.date(101, 10, 29), LocalDate.of(2012, 10, 29))
    )

  test("test_toLocalDate") {
    data_samples.foreach {
      case (minguo, iso) =>
        assertEquals(LocalDate.from(minguo), iso)
    }
  }

  test("test_fromCalendrical") {
    data_samples.foreach {
      case (minguo, iso) =>
        assertEquals(MinguoChronology.INSTANCE.date(iso), minguo)
    }
  }

  // This was already ignored on the original version and it doesn't compile
  // properly on scala 2.10
  ignore("test_MinguoDate") {
    data_samples.foreach {
      case (minguoDate, _) =>
        val hd: ChronoLocalDate         = minguoDate
        var hdt: ChronoLocalDateTime[_] = hd.atTime(LocalTime.NOON)
        val zo: ZoneOffset              = ZoneOffset.ofHours(1)
        hdt.atZone(zo)
        hdt = hdt.plus(1, ChronoUnit.YEARS)
        hdt = hdt.plus(1, ChronoUnit.MONTHS)
        hdt = hdt.plus(1, ChronoUnit.DAYS)
        hdt = hdt.plus(1, ChronoUnit.HOURS)
        hdt = hdt.plus(1, ChronoUnit.MINUTES)
        hdt = hdt.plus(1, ChronoUnit.SECONDS)
        hdt = hdt.plus(1, ChronoUnit.NANOS)
      //val a2: ChronoLocalDateTime[_] = hzdt.toLocalDateTime
      //val a3: ChronoLocalDate = a2.toLocalDate
      //val a5: ChronoLocalDate = hzdt.toLocalDate
    }
  }

  test("test_MinguoChrono") {
    val h1: ChronoLocalDate        = MinguoChronology.INSTANCE.date(MinguoEra.ROC, 1, 2, 3)
    val h2: ChronoLocalDate        = h1
    val h3: ChronoLocalDateTime[_] = h2.atTime(LocalTime.NOON)
    h3.atZone(ZoneOffset.UTC)
  }

  val data_badDates: List[(Int, Int, Int)] =
    List(
      (1912, 0, 0),
      (1912, -1, 1),
      (1912, 0, 1),
      (1912, 14, 1),
      (1912, 15, 1),
      (1912, 1, -1),
      (1912, 1, 0),
      (1912, 1, 32),
      (1912, 2, 29),
      (1912, 2, 30),
      (1912, 12, -1),
      (1912, 12, 0),
      (1912, 12, 32)
    )

  test("test_badDates") {
    data_badDates.foreach {
      case (year, month, dom) =>
        assertThrows[DateTimeException] {
          MinguoChronology.INSTANCE.date(year, month, dom)
        }
    }
  }

  test("test_adjust1") {
    val base: ChronoLocalDate = MinguoChronology.INSTANCE.date(2012, 10, 29)
    val test: ChronoLocalDate = base.`with`(TemporalAdjusters.lastDayOfMonth)
    assertEquals(test, MinguoChronology.INSTANCE.date(2012, 10, 31))
  }

  test("test_adjust2") {
    val base: ChronoLocalDate = MinguoChronology.INSTANCE.date(1728, 12, 2)
    val test: ChronoLocalDate = base.`with`(TemporalAdjusters.lastDayOfMonth)
    assertEquals(test, MinguoChronology.INSTANCE.date(1728, 12, 31))
  }

  test("test_adjust_toLocalDate") {
    val minguo: ChronoLocalDate = MinguoChronology.INSTANCE.date(99, 1, 4)
    val test: ChronoLocalDate   = minguo.`with`(LocalDate.of(2012, 7, 6))
    assertEquals(test, MinguoChronology.INSTANCE.date(101, 7, 6))
  }

  test("test_adjust_toMonth") {
    assertThrows[DateTimeException] {
      val minguo: ChronoLocalDate = MinguoChronology.INSTANCE.date(1726, 1, 4)
      minguo.`with`(Month.APRIL)
    }
  }

  test("test_LocalDate_adjustToMinguoDate") {
    val minguo: ChronoLocalDate = MinguoChronology.INSTANCE.date(101, 10, 29)
    val test: LocalDate         = LocalDate.MIN.`with`(minguo)
    assertEquals(test, LocalDate.of(2012, 10, 29))
  }

  test("test_LocalDateTime_adjustToMinguoDate") {
    val minguo: ChronoLocalDate = MinguoChronology.INSTANCE.date(101, 10, 29)
    val test: LocalDateTime     = LocalDateTime.MIN.`with`(minguo)
    assertEquals(test, LocalDateTime.of(2012, 10, 29, 0, 0))
  }

  val data_toString: List[(ChronoLocalDate, String)] =
    List(
      (MinguoChronology.INSTANCE.date(1, 1, 1), "Minguo ROC 1-01-01"),
      (MinguoChronology.INSTANCE.date(1728, 10, 28), "Minguo ROC 1728-10-28"),
      (MinguoChronology.INSTANCE.date(1728, 10, 29), "Minguo ROC 1728-10-29"),
      (MinguoChronology.INSTANCE.date(1727, 12, 5), "Minguo ROC 1727-12-05"),
      (MinguoChronology.INSTANCE.date(1727, 12, 6), "Minguo ROC 1727-12-06")
    )

  test("test_toString") {
    data_toString.foreach {
      case (minguo, expected) =>
        assertEquals(minguo.toString, expected)
    }
  }

  test("test_equals_true") {
    assertTrue(MinguoChronology.INSTANCE == MinguoChronology.INSTANCE)
  }

  test("test_equals_false") {
    assertFalse(MinguoChronology.INSTANCE == IsoChronology.INSTANCE)
  }
}
