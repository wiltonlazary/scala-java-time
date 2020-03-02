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
package org.threeten.bp.temporal

import org.scalatest.FunSuite
import org.threeten.bp.DayOfWeek.{ MONDAY, TUESDAY }
import org.threeten.bp.{ AssertionsHelper, DayOfWeek, LocalDate, Month }
import org.threeten.bp.Month.{ DECEMBER, JANUARY }

/** Test DateTimeAdjusters. */
class TestTemporalAdjusters extends FunSuite with AssertionsHelper {
  test("factory_firstDayOfMonth") {
    assertNotNull(TemporalAdjusters.firstDayOfMonth)
  }

  test("firstDayOfMonth_nonLeap") {
    for (month <- Month.values) {
      var i: Int = 1
      while (i <= month.length(false)) {
        val _date: LocalDate = date(2007, month, i)
        val test: LocalDate = TemporalAdjusters.firstDayOfMonth
          .adjustInto(_date)
          .asInstanceOf[LocalDate]
        assertEquals(test.getYear, 2007)
        assertEquals(test.getMonth, month)
        assertEquals(test.getDayOfMonth, 1)
        i += 1
      }
    }
  }

  test("firstDayOfMonth_leap") {
    for (month <- Month.values) {
      var i: Int = 1
      while (i <= month.length(true)) {
        val _date: LocalDate = date(2008, month, i)
        val test: LocalDate = TemporalAdjusters.firstDayOfMonth
          .adjustInto(_date)
          .asInstanceOf[LocalDate]
        assertEquals(test.getYear, 2008)
        assertEquals(test.getMonth, month)
        assertEquals(test.getDayOfMonth, 1)
        i += 1
      }
    }
  }

  test("factory_lastDayOfMonth") {
    assertNotNull(TemporalAdjusters.lastDayOfMonth)
  }

  test("lastDayOfMonth_nonLeap") {
    for (month <- Month.values) {
      var i: Int = 1
      while (i <= month.length(false)) {
        val _date: LocalDate = date(2007, month, i)
        val test: LocalDate = TemporalAdjusters.lastDayOfMonth
          .adjustInto(_date)
          .asInstanceOf[LocalDate]
        assertEquals(test.getYear, 2007)
        assertEquals(test.getMonth, month)
        assertEquals(test.getDayOfMonth, month.length(false))
        i += 1
      }
    }
  }

  test("lastDayOfMonth_leap") {
    for (month <- Month.values) {
      var i: Int = 1
      while (i <= month.length(true)) {
        val _date: LocalDate = date(2008, month, i)
        val test: LocalDate = TemporalAdjusters.lastDayOfMonth
          .adjustInto(_date)
          .asInstanceOf[LocalDate]
        assertEquals(test.getYear, 2008)
        assertEquals(test.getMonth, month)
        assertEquals(test.getDayOfMonth, month.length(true))
        i += 1
      }
    }
  }

  test("factory_firstDayOfNextMonth") {
    assertNotNull(TemporalAdjusters.firstDayOfNextMonth)
  }

  test("firstDayOfNextMonth_nonLeap") {
    for (month <- Month.values) {
      var i: Int = 1
      while (i <= month.length(false)) {
        val _date: LocalDate = date(2007, month, i)
        val test: LocalDate = TemporalAdjusters.firstDayOfNextMonth
          .adjustInto(_date)
          .asInstanceOf[LocalDate]
        assertEquals(test.getYear, if (month eq DECEMBER) 2008 else 2007)
        assertEquals(test.getMonth, month.plus(1))
        assertEquals(test.getDayOfMonth, 1)
        i += 1
      }
    }
  }

  test("firstDayOfNextMonth_leap") {
    for (month <- Month.values) {
      var i: Int = 1
      while (i <= month.length(true)) {
        val _date: LocalDate = date(2008, month, i)
        val test: LocalDate = TemporalAdjusters.firstDayOfNextMonth
          .adjustInto(_date)
          .asInstanceOf[LocalDate]
        assertEquals(test.getYear, if (month eq DECEMBER) 2009 else 2008)
        assertEquals(test.getMonth, month.plus(1))
        assertEquals(test.getDayOfMonth, 1)
        i += 1
      }
    }
  }

  test("factory_firstDayOfYear") {
    assertNotNull(TemporalAdjusters.firstDayOfYear)
  }

  test("firstDayOfYear_nonLeap") {
    for (month <- Month.values) {
      var i: Int = 1
      while (i <= month.length(false)) {
        val _date: LocalDate = date(2007, month, i)
        val test: LocalDate = TemporalAdjusters.firstDayOfYear
          .adjustInto(_date)
          .asInstanceOf[LocalDate]
        assertEquals(test.getYear, 2007)
        assertEquals(test.getMonth, Month.JANUARY)
        assertEquals(test.getDayOfMonth, 1)
        i += 1
      }
    }
  }

  test("firstDayOfYear_leap") {
    for (month <- Month.values) {
      var i: Int = 1
      while (i <= month.length(true)) {
        val _date: LocalDate = date(2008, month, i)
        val test: LocalDate = TemporalAdjusters.firstDayOfYear
          .adjustInto(_date)
          .asInstanceOf[LocalDate]
        assertEquals(test.getYear, 2008)
        assertEquals(test.getMonth, Month.JANUARY)
        assertEquals(test.getDayOfMonth, 1)
        i += 1
      }
    }
  }

  test("factory_lastDayOfYear") {
    assertNotNull(TemporalAdjusters.lastDayOfYear)
  }

  test("lastDayOfYear_nonLeap") {
    for (month <- Month.values) {
      var i: Int = 1
      while (i <= month.length(false)) {
        val _date: LocalDate = date(2007, month, i)
        val test: LocalDate = TemporalAdjusters.lastDayOfYear
          .adjustInto(_date)
          .asInstanceOf[LocalDate]
        assertEquals(test.getYear, 2007)
        assertEquals(test.getMonth, Month.DECEMBER)
        assertEquals(test.getDayOfMonth, 31)
        i += 1
      }
    }
  }

  test("lastDayOfYear_leap") {
    for (month <- Month.values) {
      var i: Int = 1
      while (i <= month.length(true)) {
        val _date: LocalDate = date(2008, month, i)
        val test: LocalDate = TemporalAdjusters.lastDayOfYear
          .adjustInto(_date)
          .asInstanceOf[LocalDate]
        assertEquals(test.getYear, 2008)
        assertEquals(test.getMonth, Month.DECEMBER)
        assertEquals(test.getDayOfMonth, 31)
        i += 1
      }
    }
  }

  test("factory_firstDayOfNextYear") {
    assertNotNull(TemporalAdjusters.firstDayOfNextYear)
  }

  test("firstDayOfNextYear_nonLeap") {
    for (month <- Month.values) {
      var i: Int = 1
      while (i <= month.length(false)) {
        val _date: LocalDate = date(2007, month, i)
        val test: LocalDate = TemporalAdjusters.firstDayOfNextYear
          .adjustInto(_date)
          .asInstanceOf[LocalDate]
        assertEquals(test.getYear, 2008)
        assertEquals(test.getMonth, JANUARY)
        assertEquals(test.getDayOfMonth, 1)
        i += 1
      }
    }
  }

  test("firstDayOfNextYear_leap") {
    for (month <- Month.values) {
      var i: Int = 1
      while (i <= month.length(true)) {
        val _date: LocalDate = date(2008, month, i)
        val test: LocalDate = TemporalAdjusters.firstDayOfNextYear
          .adjustInto(_date)
          .asInstanceOf[LocalDate]
        assertEquals(test.getYear, 2009)
        assertEquals(test.getMonth, JANUARY)
        assertEquals(test.getDayOfMonth, 1)
        i += 1
      }
    }
  }

  test("factory_dayOfWeekInMonth") {
    assertNotNull(TemporalAdjusters.dayOfWeekInMonth(1, MONDAY))
  }

  test("factory_dayOfWeekInMonth_nullDayOfWeek") {
    assertThrows[NullPointerException] {
      TemporalAdjusters.dayOfWeekInMonth(1, null)
    }
  }

  def data_dayOfWeekInMonth_positive: List[List[Any]] =
    List(
      List(2011, 1, TUESDAY, date(2011, 1, 4)),
      List(2011, 2, TUESDAY, date(2011, 2, 1)),
      List(2011, 3, TUESDAY, date(2011, 3, 1)),
      List(2011, 4, TUESDAY, date(2011, 4, 5)),
      List(2011, 5, TUESDAY, date(2011, 5, 3)),
      List(2011, 6, TUESDAY, date(2011, 6, 7)),
      List(2011, 7, TUESDAY, date(2011, 7, 5)),
      List(2011, 8, TUESDAY, date(2011, 8, 2)),
      List(2011, 9, TUESDAY, date(2011, 9, 6)),
      List(2011, 10, TUESDAY, date(2011, 10, 4)),
      List(2011, 11, TUESDAY, date(2011, 11, 1)),
      List(2011, 12, TUESDAY, date(2011, 12, 6))
    )

  test("dayOfWeekInMonth_positive") {
    data_dayOfWeekInMonth_positive.foreach {
      case (year: Int) :: (month: Int) :: (dow: DayOfWeek) :: (expected: LocalDate) :: Nil =>
        var ordinal: Int = 1
        while (ordinal <= 5) {
          var day: Int = 1
          while (day <= Month.of(month).length(false)) {
            val _date: LocalDate = date(year, month, day)
            val test: LocalDate = TemporalAdjusters
              .dayOfWeekInMonth(ordinal, dow)
              .adjustInto(_date)
              .asInstanceOf[LocalDate]
            assertEquals(test, expected.plusWeeks(ordinal - 1))
            day += 1
          }
          ordinal += 1
        }
      case _ =>
        fail()
    }
  }

  def data_dayOfWeekInMonth_zero: List[List[Any]] =
    List(
      List(2011, 1, TUESDAY, date(2010, 12, 28)),
      List(2011, 2, TUESDAY, date(2011, 1, 25)),
      List(2011, 3, TUESDAY, date(2011, 2, 22)),
      List(2011, 4, TUESDAY, date(2011, 3, 29)),
      List(2011, 5, TUESDAY, date(2011, 4, 26)),
      List(2011, 6, TUESDAY, date(2011, 5, 31)),
      List(2011, 7, TUESDAY, date(2011, 6, 28)),
      List(2011, 8, TUESDAY, date(2011, 7, 26)),
      List(2011, 9, TUESDAY, date(2011, 8, 30)),
      List(2011, 10, TUESDAY, date(2011, 9, 27)),
      List(2011, 11, TUESDAY, date(2011, 10, 25)),
      List(2011, 12, TUESDAY, date(2011, 11, 29))
    )

  test("dayOfWeekInMonth_zero") {
    data_dayOfWeekInMonth_zero.foreach {
      case (year: Int) :: (month: Int) :: (dow: DayOfWeek) :: (expected: LocalDate) :: Nil =>
        var day: Int = 1
        while (day <= Month.of(month).length(false)) {
          val _date: LocalDate = date(year, month, day)
          val test: LocalDate = TemporalAdjusters
            .dayOfWeekInMonth(0, dow)
            .adjustInto(_date)
            .asInstanceOf[LocalDate]
          assertEquals(test, expected)
          day += 1
        }
      case _ =>
        fail()
    }
  }

  def data_dayOfWeekInMonth_negative: List[List[Any]] =
    List(
      List(2011, 1, TUESDAY, date(2011, 1, 25)),
      List(2011, 2, TUESDAY, date(2011, 2, 22)),
      List(2011, 3, TUESDAY, date(2011, 3, 29)),
      List(2011, 4, TUESDAY, date(2011, 4, 26)),
      List(2011, 5, TUESDAY, date(2011, 5, 31)),
      List(2011, 6, TUESDAY, date(2011, 6, 28)),
      List(2011, 7, TUESDAY, date(2011, 7, 26)),
      List(2011, 8, TUESDAY, date(2011, 8, 30)),
      List(2011, 9, TUESDAY, date(2011, 9, 27)),
      List(2011, 10, TUESDAY, date(2011, 10, 25)),
      List(2011, 11, TUESDAY, date(2011, 11, 29)),
      List(2011, 12, TUESDAY, date(2011, 12, 27))
    )

  test("dayOfWeekInMonth_negative") {
    data_dayOfWeekInMonth_negative.foreach {
      case (year: Int) :: (month: Int) :: (dow: DayOfWeek) :: (expected: LocalDate) :: Nil =>
        var ordinal: Int = 0
        while (ordinal < 5) {
          var day: Int = 1
          while (day <= Month.of(month).length(false)) {
            val _date: LocalDate = date(year, month, day)
            val test: LocalDate = TemporalAdjusters
              .dayOfWeekInMonth(-1 - ordinal, dow)
              .adjustInto(_date)
              .asInstanceOf[LocalDate]
            assertEquals(test, expected.minusWeeks(ordinal))
            day += 1
          }
          ordinal += 1
        }
      case _ =>
        fail()
    }
  }

  test("factory_firstInMonth") {
    assertNotNull(TemporalAdjusters.firstInMonth(MONDAY))
  }

  test("factory_firstInMonth_nullDayOfWeek") {
    assertThrows[NullPointerException] {
      TemporalAdjusters.firstInMonth(null)
    }
  }

  test("firstInMonth") {
    data_dayOfWeekInMonth_positive.foreach {
      case (year: Int) :: (month: Int) :: (dow: DayOfWeek) :: (expected: LocalDate) :: Nil =>
        var day: Int = 1
        while (day <= Month.of(month).length(false)) {
          val _date: LocalDate = date(year, month, day)
          val test: LocalDate = TemporalAdjusters
            .firstInMonth(dow)
            .adjustInto(_date)
            .asInstanceOf[LocalDate]
          assertEquals(test, expected, "day-of-month=" + day)
          day += 1
        }
      case _ =>
        fail()
    }
  }

  test("factory_lastInMonth") {
    assertNotNull(TemporalAdjusters.lastInMonth(MONDAY))
  }

  test("factory_lastInMonth_nullDayOfWeek") {
    assertThrows[NullPointerException] {
      TemporalAdjusters.lastInMonth(null)
    }
  }

  test("lastInMonth") {
    data_dayOfWeekInMonth_negative.foreach {
      case (year: Int) :: (month: Int) :: (dow: DayOfWeek) :: (expected: LocalDate) :: Nil =>
        var day: Int = 1
        while (day <= Month.of(month).length(false)) {
          val _date: LocalDate = date(year, month, day)
          val test: LocalDate = TemporalAdjusters
            .lastInMonth(dow)
            .adjustInto(_date)
            .asInstanceOf[LocalDate]
          assertEquals(test, expected, "day-of-month=" + day)
          day += 1
        }
      case _ =>
        fail()
    }
  }

  test("factory_next") {
    assertNotNull(TemporalAdjusters.next(MONDAY))
  }

  test("factory_next_nullDayOfWeek") {
    assertThrows[NullPointerException] {
      TemporalAdjusters.next(null)
    }
  }

  test("next") {
    for (month <- Month.values) {
      var i: Int = 1
      while (i <= month.length(false)) {
        val _date: LocalDate = date(2007, month, i)
        for (dow <- DayOfWeek.values) {
          val test: LocalDate = TemporalAdjusters
            .next(dow)
            .adjustInto(_date)
            .asInstanceOf[LocalDate]
          assertSame(test.getDayOfWeek, dow)
          if (test.getYear == 2007) {
            val dayDiff: Int = test.getDayOfYear - _date.getDayOfYear
            assertTrue(dayDiff > 0 && dayDiff < 8)
          } else {
            assertSame(month, Month.DECEMBER)
            assertTrue(_date.getDayOfMonth > 24)
            assertEquals(test.getYear, 2008)
            assertSame(test.getMonth, Month.JANUARY)
            assertTrue(test.getDayOfMonth < 8)
          }
        }
        i += 1
      }
    }
  }

  test("factory_nextOrSame") {
    assertNotNull(TemporalAdjusters.nextOrSame(MONDAY))
  }

  test("factory_nextOrSame_nullDayOfWeek") {
    assertThrows[NullPointerException] {
      TemporalAdjusters.nextOrSame(null)
    }
  }

  test("nextOrSame") {
    for (month <- Month.values) {
      var i: Int = 1
      while (i <= month.length(false)) {
        val _date: LocalDate = date(2007, month, i)
        for (dow <- DayOfWeek.values) {
          val test: LocalDate = TemporalAdjusters
            .nextOrSame(dow)
            .adjustInto(_date)
            .asInstanceOf[LocalDate]
          assertSame(test.getDayOfWeek, dow)
          if (test.getYear == 2007) {
            val dayDiff: Int = test.getDayOfYear - _date.getDayOfYear
            assertTrue(dayDiff < 8)
            assertEquals(_date == test, _date.getDayOfWeek eq dow)
          } else {
            assertFalse(_date.getDayOfWeek eq dow)
            assertSame(month, Month.DECEMBER)
            assertTrue(_date.getDayOfMonth > 24)
            assertEquals(test.getYear, 2008)
            assertSame(test.getMonth, Month.JANUARY)
            assertTrue(test.getDayOfMonth < 8)
          }
        }
        i += 1
      }
    }
  }

  test("factory_previous") {
    assertNotNull(TemporalAdjusters.previous(MONDAY))
  }

  test("factory_previous_nullDayOfWeek") {
    assertThrows[NullPointerException] {
      TemporalAdjusters.previous(null)
    }
  }

  test("previous") {
    for (month <- Month.values) {
      var i: Int = 1
      while (i <= month.length(false)) {
        val _date: LocalDate = date(2007, month, i)
        for (dow <- DayOfWeek.values) {
          val test: LocalDate = TemporalAdjusters
            .previous(dow)
            .adjustInto(_date)
            .asInstanceOf[LocalDate]
          assertSame(test.getDayOfWeek, dow)
          if (test.getYear == 2007) {
            val dayDiff: Int = test.getDayOfYear - _date.getDayOfYear
            assertTrue(dayDiff < 0 && dayDiff > -8)
          } else {
            assertSame(month, Month.JANUARY)
            assertTrue(_date.getDayOfMonth < 8)
            assertEquals(test.getYear, 2006)
            assertSame(test.getMonth, Month.DECEMBER)
            assertTrue(test.getDayOfMonth > 24)
          }
        }
        i += 1
      }
    }
  }

  test("factory_previousOrSame") {
    assertNotNull(TemporalAdjusters.previousOrSame(MONDAY))
  }

  test("factory_previousOrSame_nullDayOfWeek") {
    assertThrows[NullPointerException] {
      TemporalAdjusters.previousOrSame(null)
    }
  }

  test("previousOrSame") {
    for (month <- Month.values) {
      var i: Int = 1
      while (i <= month.length(false)) {
        val _date: LocalDate = date(2007, month, i)
        for (dow <- DayOfWeek.values) {
          val test: LocalDate = TemporalAdjusters
            .previousOrSame(dow)
            .adjustInto(_date)
            .asInstanceOf[LocalDate]
          assertSame(test.getDayOfWeek, dow)
          if (test.getYear == 2007) {
            val dayDiff: Int = test.getDayOfYear - _date.getDayOfYear
            assertTrue(dayDiff <= 0 && dayDiff > -7)
            assertEquals(_date == test, _date.getDayOfWeek eq dow)
          } else {
            assertFalse(_date.getDayOfWeek eq dow)
            assertSame(month, Month.JANUARY)
            assertTrue(_date.getDayOfMonth < 7)
            assertEquals(test.getYear, 2006)
            assertSame(test.getMonth, Month.DECEMBER)
            assertTrue(test.getDayOfMonth > 25)
          }
        }
        i += 1
      }
    }
  }

  private def date(year: Int, month: Month, day: Int): LocalDate =
    LocalDate.of(year, month, day)

  private def date(year: Int, month: Int, day: Int): LocalDate =
    LocalDate.of(year, month, day)
}
