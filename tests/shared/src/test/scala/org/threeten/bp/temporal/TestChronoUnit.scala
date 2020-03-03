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

import org.scalatest.funsuite.AnyFunSuite
import org.threeten.bp.AssertionsHelper
import org.threeten.bp.Month.AUGUST
import org.threeten.bp.Month.FEBRUARY
import org.threeten.bp.Month.JULY
import org.threeten.bp.Month.JUNE
import org.threeten.bp.Month.MARCH
import org.threeten.bp.Month.OCTOBER
import org.threeten.bp.Month.SEPTEMBER
import org.threeten.bp.temporal.ChronoUnit.DAYS
import org.threeten.bp.temporal.ChronoUnit.FOREVER
import org.threeten.bp.temporal.ChronoUnit.MONTHS
import org.threeten.bp.temporal.ChronoUnit.WEEKS
import org.threeten.bp.temporal.ChronoUnit.YEARS
import org.threeten.bp.LocalDate
import org.threeten.bp.Month
import org.threeten.bp.ZoneOffset

/** Test. */
object TestChronoUnit {
  private def date(year: Int, month: Month, dom: Int): LocalDate = LocalDate.of(year, month, dom)
}

class TestChronoUnit extends AnyFunSuite with AssertionsHelper {
  val data_yearsBetween: List[(LocalDate, LocalDate, Int)] = {
    List(
      (TestChronoUnit.date(1939, SEPTEMBER, 2), TestChronoUnit.date(1939, SEPTEMBER, 1), 0),
      (TestChronoUnit.date(1939, SEPTEMBER, 2), TestChronoUnit.date(1939, SEPTEMBER, 2), 0),
      (TestChronoUnit.date(1939, SEPTEMBER, 2), TestChronoUnit.date(1939, SEPTEMBER, 3), 0),
      (TestChronoUnit.date(1939, SEPTEMBER, 2), TestChronoUnit.date(1940, SEPTEMBER, 1), 0),
      (TestChronoUnit.date(1939, SEPTEMBER, 2), TestChronoUnit.date(1940, SEPTEMBER, 2), 1),
      (TestChronoUnit.date(1939, SEPTEMBER, 2), TestChronoUnit.date(1940, SEPTEMBER, 3), 1),
      (TestChronoUnit.date(1939, SEPTEMBER, 2), TestChronoUnit.date(1938, SEPTEMBER, 1), -1),
      (TestChronoUnit.date(1939, SEPTEMBER, 2), TestChronoUnit.date(1938, SEPTEMBER, 2), -1),
      (TestChronoUnit.date(1939, SEPTEMBER, 2), TestChronoUnit.date(1938, SEPTEMBER, 3), 0),
      (TestChronoUnit.date(1939, SEPTEMBER, 2), TestChronoUnit.date(1945, SEPTEMBER, 3), 6),
      (TestChronoUnit.date(1939, SEPTEMBER, 2), TestChronoUnit.date(1945, OCTOBER, 3), 6),
      (TestChronoUnit.date(1939, SEPTEMBER, 2), TestChronoUnit.date(1945, AUGUST, 3), 5)
    )
  }

  test("test_yearsBetween") {
    data_yearsBetween.foreach {
      case (start, end, expected) =>
        assertEquals(YEARS.between(start, end), expected)
    }
  }

  test("test_yearsBetweenReversed") {
    data_yearsBetween.foreach {
      case (start, end, expected) =>
        assertEquals(YEARS.between(end, start), -expected)
    }
  }

  test("test_yearsBetween_LocalDateTimeSameTime") {
    data_yearsBetween.foreach {
      case (start, end, expected) =>
        assertEquals(YEARS.between(start.atTime(12, 30), end.atTime(12, 30)), expected)
    }
  }

  test("test_yearsBetween_LocalDateTimeLaterTime") {
    data_yearsBetween.foreach {
      case (start, end, expected) =>
        if (end.isAfter(start)) {
          assertEquals(YEARS.between(start.atTime(12, 30), end.atTime(12, 31)), expected)
        } else {
          assertEquals(YEARS.between(start.atTime(12, 31), end.atTime(12, 30)), expected)
        }
    }
  }

  test("test_yearsBetween_ZonedDateSameOffset") {
    data_yearsBetween.foreach {
      case (start, end, expected) =>
        assertEquals(YEARS.between(start.atStartOfDay(ZoneOffset.ofHours(2)),
                                   end.atStartOfDay(ZoneOffset.ofHours(2))),
                     expected)
    }
  }

  test("test_yearsBetween_ZonedDateLaterOffset") {
    data_yearsBetween.foreach {
      case (start, end, expected) =>
        if (end.isAfter(start)) {
          assertEquals(YEARS.between(start.atStartOfDay(ZoneOffset.ofHours(2)),
                                     end.atStartOfDay(ZoneOffset.ofHours(1))),
                       expected)
        } else {
          assertEquals(YEARS.between(start.atStartOfDay(ZoneOffset.ofHours(1)),
                                     end.atStartOfDay(ZoneOffset.ofHours(2))),
                       expected)
        }
    }
  }

  val data_monthsBetween: List[(LocalDate, LocalDate, Long)] = {
    List(
      (TestChronoUnit.date(2012, JULY, 2), TestChronoUnit.date(2012, JULY, 1), 0),
      (TestChronoUnit.date(2012, JULY, 2), TestChronoUnit.date(2012, JULY, 2), 0),
      (TestChronoUnit.date(2012, JULY, 2), TestChronoUnit.date(2012, JULY, 3), 0),
      (TestChronoUnit.date(2012, JULY, 2), TestChronoUnit.date(2012, AUGUST, 1), 0),
      (TestChronoUnit.date(2012, JULY, 2), TestChronoUnit.date(2012, AUGUST, 2), 1),
      (TestChronoUnit.date(2012, JULY, 2), TestChronoUnit.date(2012, AUGUST, 3), 1),
      (TestChronoUnit.date(2012, JULY, 2), TestChronoUnit.date(2012, SEPTEMBER, 1), 1),
      (TestChronoUnit.date(2012, JULY, 2), TestChronoUnit.date(2012, SEPTEMBER, 2), 2),
      (TestChronoUnit.date(2012, JULY, 2), TestChronoUnit.date(2012, SEPTEMBER, 3), 2),
      (TestChronoUnit.date(2012, JULY, 2), TestChronoUnit.date(2012, JUNE, 1), -1),
      (TestChronoUnit.date(2012, JULY, 2), TestChronoUnit.date(2012, JUNE, 2), -1),
      (TestChronoUnit.date(2012, JULY, 2), TestChronoUnit.date(2012, JUNE, 3), 0),
      (TestChronoUnit.date(2012, FEBRUARY, 27), TestChronoUnit.date(2012, MARCH, 26), 0),
      (TestChronoUnit.date(2012, FEBRUARY, 27), TestChronoUnit.date(2012, MARCH, 27), 1),
      (TestChronoUnit.date(2012, FEBRUARY, 27), TestChronoUnit.date(2012, MARCH, 28), 1),
      (TestChronoUnit.date(2012, FEBRUARY, 28), TestChronoUnit.date(2012, MARCH, 27), 0),
      (TestChronoUnit.date(2012, FEBRUARY, 28), TestChronoUnit.date(2012, MARCH, 28), 1),
      (TestChronoUnit.date(2012, FEBRUARY, 28), TestChronoUnit.date(2012, MARCH, 29), 1),
      (TestChronoUnit.date(2012, FEBRUARY, 29), TestChronoUnit.date(2012, MARCH, 28), 0),
      (TestChronoUnit.date(2012, FEBRUARY, 29), TestChronoUnit.date(2012, MARCH, 29), 1),
      (TestChronoUnit.date(2012, FEBRUARY, 29), TestChronoUnit.date(2012, MARCH, 30), 1)
    )
  }

  test("test_monthsBetween") {
    data_monthsBetween.foreach {
      case (start, end, expected) =>
        assertEquals(MONTHS.between(start, end), expected)
    }
  }

  test("test_monthsBetweenReversed") {
    data_monthsBetween.foreach {
      case (start, end, expected) =>
        assertEquals(MONTHS.between(end, start), -expected)
    }
  }

  test("test_monthsBetween_LocalDateTimeSameTime") {
    data_monthsBetween.foreach {
      case (start, end, expected) =>
        assertEquals(MONTHS.between(start.atTime(12, 30), end.atTime(12, 30)), expected)
    }
  }

  test("test_monthsBetween_LocalDateTimeLaterTime") {
    data_monthsBetween.foreach {
      case (start, end, expected) =>
        if (end.isAfter(start)) {
          assertEquals(MONTHS.between(start.atTime(12, 30), end.atTime(12, 31)), expected)
        } else {
          assertEquals(MONTHS.between(start.atTime(12, 31), end.atTime(12, 30)), expected)
        }
    }
  }

  test("test_monthsBetween_ZonedDateSameOffset") {
    data_monthsBetween.foreach {
      case (start, end, expected) =>
        assertEquals(MONTHS.between(start.atStartOfDay(ZoneOffset.ofHours(2)),
                                    end.atStartOfDay(ZoneOffset.ofHours(2))),
                     expected)
    }
  }

  test("test_monthsBetween_ZonedDateLaterOffset") {
    data_monthsBetween.foreach {
      case (start, end, expected) =>
        if (end.isAfter(start)) {
          assertEquals(MONTHS.between(start.atStartOfDay(ZoneOffset.ofHours(2)),
                                      end.atStartOfDay(ZoneOffset.ofHours(1))),
                       expected)
        } else {
          assertEquals(MONTHS.between(start.atStartOfDay(ZoneOffset.ofHours(1)),
                                      end.atStartOfDay(ZoneOffset.ofHours(2))),
                       expected)
        }
    }
  }

  val data_weeksBetween: List[(LocalDate, LocalDate, Long)] = {
    List(
      (TestChronoUnit.date(2012, JULY, 2), TestChronoUnit.date(2012, JUNE, 25), -1),
      (TestChronoUnit.date(2012, JULY, 2), TestChronoUnit.date(2012, JUNE, 26), 0),
      (TestChronoUnit.date(2012, JULY, 2), TestChronoUnit.date(2012, JULY, 2), 0),
      (TestChronoUnit.date(2012, JULY, 2), TestChronoUnit.date(2012, JULY, 8), 0),
      (TestChronoUnit.date(2012, JULY, 2), TestChronoUnit.date(2012, JULY, 9), 1),
      (TestChronoUnit.date(2012, FEBRUARY, 28), TestChronoUnit.date(2012, FEBRUARY, 21), -1),
      (TestChronoUnit.date(2012, FEBRUARY, 28), TestChronoUnit.date(2012, FEBRUARY, 22), 0),
      (TestChronoUnit.date(2012, FEBRUARY, 28), TestChronoUnit.date(2012, FEBRUARY, 28), 0),
      (TestChronoUnit.date(2012, FEBRUARY, 28), TestChronoUnit.date(2012, FEBRUARY, 29), 0),
      (TestChronoUnit.date(2012, FEBRUARY, 28), TestChronoUnit.date(2012, MARCH, 1), 0),
      (TestChronoUnit.date(2012, FEBRUARY, 28), TestChronoUnit.date(2012, MARCH, 5), 0),
      (TestChronoUnit.date(2012, FEBRUARY, 28), TestChronoUnit.date(2012, MARCH, 6), 1),
      (TestChronoUnit.date(2012, FEBRUARY, 29), TestChronoUnit.date(2012, FEBRUARY, 22), -1),
      (TestChronoUnit.date(2012, FEBRUARY, 29), TestChronoUnit.date(2012, FEBRUARY, 23), 0),
      (TestChronoUnit.date(2012, FEBRUARY, 29), TestChronoUnit.date(2012, FEBRUARY, 28), 0),
      (TestChronoUnit.date(2012, FEBRUARY, 29), TestChronoUnit.date(2012, FEBRUARY, 29), 0),
      (TestChronoUnit.date(2012, FEBRUARY, 29), TestChronoUnit.date(2012, MARCH, 1), 0),
      (TestChronoUnit.date(2012, FEBRUARY, 29), TestChronoUnit.date(2012, MARCH, 6), 0),
      (TestChronoUnit.date(2012, FEBRUARY, 29), TestChronoUnit.date(2012, MARCH, 7), 1)
    )
  }

  test("test_weeksBetween") {
    data_weeksBetween.foreach {
      case (start, end, expected) =>
        assertEquals(WEEKS.between(start, end), expected)
    }
  }

  test("test_weeksBetweenReversed") {
    data_weeksBetween.foreach {
      case (start, end, expected) =>
        assertEquals(WEEKS.between(end, start), -expected)
    }
  }

  val data_daysBetween: List[(LocalDate, LocalDate, Long)] = {
    List(
      (TestChronoUnit.date(2012, JULY, 2), TestChronoUnit.date(2012, JULY, 1), -1),
      (TestChronoUnit.date(2012, JULY, 2), TestChronoUnit.date(2012, JULY, 2), 0),
      (TestChronoUnit.date(2012, JULY, 2), TestChronoUnit.date(2012, JULY, 3), 1),
      (TestChronoUnit.date(2012, FEBRUARY, 28), TestChronoUnit.date(2012, FEBRUARY, 27), -1),
      (TestChronoUnit.date(2012, FEBRUARY, 28), TestChronoUnit.date(2012, FEBRUARY, 28), 0),
      (TestChronoUnit.date(2012, FEBRUARY, 28), TestChronoUnit.date(2012, FEBRUARY, 29), 1),
      (TestChronoUnit.date(2012, FEBRUARY, 28), TestChronoUnit.date(2012, MARCH, 1), 2),
      (TestChronoUnit.date(2012, FEBRUARY, 29), TestChronoUnit.date(2012, FEBRUARY, 27), -2),
      (TestChronoUnit.date(2012, FEBRUARY, 29), TestChronoUnit.date(2012, FEBRUARY, 28), -1),
      (TestChronoUnit.date(2012, FEBRUARY, 29), TestChronoUnit.date(2012, FEBRUARY, 29), 0),
      (TestChronoUnit.date(2012, FEBRUARY, 29), TestChronoUnit.date(2012, MARCH, 1), 1),
      (TestChronoUnit.date(2012, MARCH, 1), TestChronoUnit.date(2012, FEBRUARY, 27), -3),
      (TestChronoUnit.date(2012, MARCH, 1), TestChronoUnit.date(2012, FEBRUARY, 28), -2),
      (TestChronoUnit.date(2012, MARCH, 1), TestChronoUnit.date(2012, FEBRUARY, 29), -1),
      (TestChronoUnit.date(2012, MARCH, 1), TestChronoUnit.date(2012, MARCH, 1), 0),
      (TestChronoUnit.date(2012, MARCH, 1), TestChronoUnit.date(2012, MARCH, 2), 1),
      (TestChronoUnit.date(2012, MARCH, 1), TestChronoUnit.date(2013, FEBRUARY, 28), 364),
      (TestChronoUnit.date(2012, MARCH, 1), TestChronoUnit.date(2013, MARCH, 1), 365),
      (TestChronoUnit.date(2011, MARCH, 1), TestChronoUnit.date(2012, FEBRUARY, 28), 364),
      (TestChronoUnit.date(2011, MARCH, 1), TestChronoUnit.date(2012, FEBRUARY, 29), 365),
      (TestChronoUnit.date(2011, MARCH, 1), TestChronoUnit.date(2012, MARCH, 1), 366)
    )
  }

  test("test_daysBetween") {
    data_daysBetween.foreach {
      case (start, end, expected) =>
        assertEquals(DAYS.between(start, end), expected)
    }
  }

  test("test_daysBetweenReversed") {
    data_daysBetween.foreach {
      case (start, end, expected) =>
        assertEquals(DAYS.between(end, start), -expected)
    }
  }

  test("test_daysBetween_LocalDateTimeSameTime") {
    data_daysBetween.foreach {
      case (start, end, expected) =>
        assertEquals(DAYS.between(start.atTime(12, 30), end.atTime(12, 30)), expected)
    }
  }

  test("test_daysBetween_LocalDateTimeLaterTime") {
    data_daysBetween.foreach {
      case (start, end, expected) =>
        if (end.isAfter(start)) {
          assertEquals(DAYS.between(start.atTime(12, 30), end.atTime(12, 31)), expected)
        } else {
          assertEquals(DAYS.between(start.atTime(12, 31), end.atTime(12, 30)), expected)
        }
    }
  }

  test("test_daysBetween_ZonedDateSameOffset") {
    data_daysBetween.foreach {
      case (start, end, expected) =>
        assertEquals(DAYS.between(start.atStartOfDay(ZoneOffset.ofHours(2)),
                                  end.atStartOfDay(ZoneOffset.ofHours(2))),
                     expected)
    }
  }

  test("test_daysBetween_ZonedDateLaterOffset") {
    data_daysBetween.foreach {
      case (start, end, expected) =>
        if (end.isAfter(start)) {
          assertEquals(DAYS.between(start.atStartOfDay(ZoneOffset.ofHours(2)),
                                    end.atStartOfDay(ZoneOffset.ofHours(1))),
                       expected)
        } else {
          assertEquals(DAYS.between(start.atStartOfDay(ZoneOffset.ofHours(1)),
                                    end.atStartOfDay(ZoneOffset.ofHours(2))),
                       expected)
        }
    }
  }

  test("test_isDateBased") {
    for (unit <- ChronoUnit.values) {
      if (unit.getDuration.getSeconds < 86400) {
        assertEquals(unit.isDateBased, false)
      } else if (unit eq FOREVER) {
        assertEquals(unit.isDateBased, false)
      } else {
        assertEquals(unit.isDateBased, true)
      }
    }
  }

  test("test_isTimeBased") {
    for (unit <- ChronoUnit.values) {
      if (unit.getDuration.getSeconds < 86400) {
        assertEquals(unit.isTimeBased, true)
      } else if (unit eq FOREVER) {
        assertEquals(unit.isTimeBased, false)
      } else {
        assertEquals(unit.isTimeBased, false)
      }
    }
  }
}
