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

import org.scalatest.BeforeAndAfter
import org.threeten.bp.temporal.ChronoField.DAY_OF_MONTH
import org.threeten.bp.temporal.ChronoField.MONTH_OF_YEAR

import org.threeten.bp._
import org.threeten.bp.chrono.IsoChronology
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException

/** Test MonthDay. */
class TestMonthDay extends GenDateTimeTest with BeforeAndAfter {
  private var TEST_07_15: MonthDay = null

  before {
    TEST_07_15 = MonthDay.of(7, 15)
  }

  override protected def samples: List[TemporalAccessor] =
    List(TEST_07_15)

  override protected def validFields: List[TemporalField] =
    List(DAY_OF_MONTH, MONTH_OF_YEAR)

  protected def invalidFields: List[TemporalField] = {
    val list: List[TemporalField] = List(ChronoField.values: _*)
    (list :+ JulianFields.JULIAN_DAY :+ JulianFields.MODIFIED_JULIAN_DAY :+ JulianFields.RATA_DIE)
      .filterNot(validFields.contains)
  }

  ignore("test_immutable") {
    // "private constructor shows up public due to companion object"
    // AbstractTest.assertImmutable(classOf[YearMonth])
  }

  private[temporal] def check(test: MonthDay, m: Int, d: Int): Unit = {
    assertEquals(test.getMonth.getValue, m)
    assertEquals(test.getDayOfMonth, d)
  }

  test("now") {
    var expected: MonthDay = MonthDay.now(Clock.systemDefaultZone)
    var test: MonthDay     = MonthDay.now
    var i: Int             = 0
    while (i < 100 && expected != test) {
      expected = MonthDay.now(Clock.systemDefaultZone)
      test = MonthDay.now
      i += 1
    }
    assertEquals(test, expected)
  }

  test("now_ZoneId_nullZoneId") {
    assertThrows[NullPointerException] {
      MonthDay.now(null.asInstanceOf[ZoneId])
    }
  }

  test("now_ZoneId") {
    val zone: ZoneId       = ZoneId.of("UTC+01:02:03")
    var expected: MonthDay = MonthDay.now(Clock.system(zone))
    var test: MonthDay     = MonthDay.now(zone)
    var i: Int             = 0
    while (i < 100 && expected != test) {
      expected = MonthDay.now(Clock.system(zone))
      test = MonthDay.now(zone)
      i += 1
    }
    assertEquals(test, expected)
  }

  test("now_Clock") {
    val instant: Instant = LocalDateTime.of(2010, 12, 31, 0, 0).toInstant(ZoneOffset.UTC)
    val clock: Clock     = Clock.fixed(instant, ZoneOffset.UTC)
    val test: MonthDay   = MonthDay.now(clock)
    assertEquals(test.getMonth, Month.DECEMBER)
    assertEquals(test.getDayOfMonth, 31)
  }

  test("now_Clock_nullClock") {
    assertThrows[NullPointerException] {
      MonthDay.now(null.asInstanceOf[Clock])
    }
  }

  test("factory_intMonth") {
    assertEquals(TEST_07_15, MonthDay.of(Month.JULY, 15))
  }

  test("test_factory_intMonth_dayTooLow") {
    assertThrows[DateTimeException] {
      MonthDay.of(Month.JANUARY, 0)
    }
  }

  test("test_factory_intMonth_dayTooHigh") {
    assertThrows[DateTimeException] {
      MonthDay.of(Month.JANUARY, 32)
    }
  }

  test("factory_intMonth_nullMonth") {
    assertThrows[NullPointerException] {
      MonthDay.of(null, 15)
    }
  }

  test("factory_ints") {
    check(TEST_07_15, 7, 15)
  }

  test("test_factory_ints_dayTooLow") {
    assertThrows[DateTimeException] {
      MonthDay.of(1, 0)
    }
  }

  test("test_factory_ints_dayTooHigh") {
    assertThrows[DateTimeException] {
      MonthDay.of(1, 32)
    }
  }

  test("test_factory_ints_monthTooLow") {
    assertThrows[DateTimeException] {
      MonthDay.of(0, 1)
    }
  }

  test("test_factory_ints_monthTooHigh") {
    assertThrows[DateTimeException] {
      MonthDay.of(13, 1)
    }
  }

  test("test_factory_CalendricalObject") {
    assertEquals(MonthDay.from(LocalDate.of(2007, 7, 15)), TEST_07_15)
  }

  test("test_factory_CalendricalObject_invalid_noDerive") {
    assertThrows[DateTimeException] {
      MonthDay.from(LocalTime.of(12, 30))
    }
  }

  test("test_factory_CalendricalObject_null") {
    assertThrows[NullPointerException] {
      MonthDay.from(null.asInstanceOf[TemporalAccessor])
    }
  }

  val provider_goodParseData: List[(String, MonthDay)] =
    List(
      ("--01-01", MonthDay.of(1, 1)),
      ("--01-31", MonthDay.of(1, 31)),
      ("--02-01", MonthDay.of(2, 1)),
      ("--02-29", MonthDay.of(2, 29)),
      ("--03-01", MonthDay.of(3, 1)),
      ("--03-31", MonthDay.of(3, 31)),
      ("--04-01", MonthDay.of(4, 1)),
      ("--04-30", MonthDay.of(4, 30)),
      ("--05-01", MonthDay.of(5, 1)),
      ("--05-31", MonthDay.of(5, 31)),
      ("--06-01", MonthDay.of(6, 1)),
      ("--06-30", MonthDay.of(6, 30)),
      ("--07-01", MonthDay.of(7, 1)),
      ("--07-31", MonthDay.of(7, 31)),
      ("--08-01", MonthDay.of(8, 1)),
      ("--08-31", MonthDay.of(8, 31)),
      ("--09-01", MonthDay.of(9, 1)),
      ("--09-30", MonthDay.of(9, 30)),
      ("--10-01", MonthDay.of(10, 1)),
      ("--10-31", MonthDay.of(10, 31)),
      ("--11-01", MonthDay.of(11, 1)),
      ("--11-30", MonthDay.of(11, 30)),
      ("--12-01", MonthDay.of(12, 1)),
      ("--12-31", MonthDay.of(12, 31))
    )

  test("factory_parse_success") {
    provider_goodParseData.foreach { case (text, expected) =>
      val monthDay: MonthDay = MonthDay.parse(text)
      assertEquals(monthDay, expected)
    }
  }

  val provider_badParseData: List[(String, Int)] =
    List(("", 0), ("-00", 0), ("--FEB-23", 2), ("--01-0", 5), ("--01-3A", 5))

  test("factory_parse_fail") {
    provider_badParseData.foreach { case (text, pos) =>
      try {
        MonthDay.parse(text)
        fail(f"Parse should have failed for $text%s at position $pos%d")
      } catch {
        case ex: DateTimeParseException =>
          assertEquals(ex.getParsedString, text)
          assertEquals(ex.getErrorIndex, pos)
      }
    }
  }

  test("factory_parse_illegalValue_Day") {
    assertThrows[DateTimeParseException] {
      MonthDay.parse("--06-32")
    }
  }

  test("factory_parse_invalidValue_Day") {
    assertThrows[DateTimeParseException] {
      MonthDay.parse("--06-31")
    }
  }

  test("factory_parse_illegalValue_Month") {
    assertThrows[DateTimeParseException] {
      MonthDay.parse("--13-25")
    }
  }

  test("factory_parse_nullText") {
    assertThrows[NullPointerException] {
      MonthDay.parse(null)
    }
  }

  test("factory_parse_formatter") {
    val f: DateTimeFormatter = DateTimeFormatter.ofPattern("M d")
    val test: MonthDay       = MonthDay.parse("12 3", f)
    assertEquals(test, MonthDay.of(12, 3))
  }

  test("factory_parse_formatter_nullText") {
    assertThrows[NullPointerException] {
      val f: DateTimeFormatter = DateTimeFormatter.ofPattern("M d")
      MonthDay.parse(null.asInstanceOf[String], f)
    }
  }

  test("factory_parse_formatter_nullFormatter") {
    assertThrows[NullPointerException] {
      MonthDay.parse("ANY", null)
    }
  }

  test("test_get_DateTimeField") {
    assertEquals(TEST_07_15.getLong(ChronoField.DAY_OF_MONTH), 15)
    assertEquals(TEST_07_15.getLong(ChronoField.MONTH_OF_YEAR), 7)
  }

  test("test_get_DateTimeField_null") {
    assertThrows[Platform.NPE] {
      TEST_07_15.getLong(null.asInstanceOf[TemporalField])
    }
  }

  test("test_get_DateTimeField_invalidField") {
    assertThrows[DateTimeException] {
      TEST_07_15.getLong(MockFieldNoValue.INSTANCE)
    }
  }

  test("test_get_DateTimeField_timeField") {
    assertThrows[DateTimeException] {
      TEST_07_15.getLong(ChronoField.AMPM_OF_DAY)
    }
  }

  val provider_sampleDates: List[(Int, Int)] =
    List((1, 1), (1, 31), (2, 1), (2, 28), (2, 29), (7, 4), (7, 5))

  test("test_get") {
    provider_sampleDates.foreach { case (m, d) =>
      val a: MonthDay = MonthDay.of(m, d)
      assertEquals(a.getMonth, Month.of(m))
      assertEquals(a.getDayOfMonth, d)
    }
  }

  test("test_with_Month") {
    assertEquals(MonthDay.of(6, 30).`with`(Month.JANUARY), MonthDay.of(1, 30))
  }

  test("test_with_Month_adjustToValid") {
    assertEquals(MonthDay.of(7, 31).`with`(Month.JUNE), MonthDay.of(6, 30))
  }

  test("test_with_Month_adjustToValidFeb") {
    assertEquals(MonthDay.of(7, 31).`with`(Month.FEBRUARY), MonthDay.of(2, 29))
  }

  test("test_with_Month_noChangeEqual") {
    val test: MonthDay = MonthDay.of(6, 30)
    assertEquals(test.`with`(Month.JUNE), test)
  }

  test("test_with_Month_null") {
    assertThrows[NullPointerException] {
      MonthDay.of(6, 30).`with`(null.asInstanceOf[Month])
    }
  }

  test("test_withMonth") {
    assertEquals(MonthDay.of(6, 30).withMonth(1), MonthDay.of(1, 30))
  }

  test("test_withMonth_adjustToValid") {
    assertEquals(MonthDay.of(7, 31).withMonth(6), MonthDay.of(6, 30))
  }

  test("test_withMonth_adjustToValidFeb") {
    assertEquals(MonthDay.of(7, 31).withMonth(2), MonthDay.of(2, 29))
  }

  test("test_withMonth_int_noChangeEqual") {
    val test: MonthDay = MonthDay.of(6, 30)
    assertEquals(test.withMonth(6), test)
  }

  test("test_withMonth_tooLow") {
    assertThrows[DateTimeException] {
      MonthDay.of(6, 30).withMonth(0)
    }
  }

  test("test_withMonth_tooHigh") {
    assertThrows[DateTimeException] {
      MonthDay.of(6, 30).withMonth(13)
    }
  }

  test("test_withDayOfMonth") {
    assertEquals(MonthDay.of(6, 30).withDayOfMonth(1), MonthDay.of(6, 1))
  }

  test("test_withDayOfMonth_invalid") {
    assertThrows[DateTimeException] {
      MonthDay.of(6, 30).withDayOfMonth(31)
    }
  }

  test("test_withDayOfMonth_adjustToValidFeb") {
    assertEquals(MonthDay.of(2, 1).withDayOfMonth(29), MonthDay.of(2, 29))
  }

  test("test_withDayOfMonth_noChangeEqual") {
    val test: MonthDay = MonthDay.of(6, 30)
    assertEquals(test.withDayOfMonth(30), test)
  }

  test("test_withDayOfMonth_tooLow") {
    assertThrows[DateTimeException] {
      MonthDay.of(6, 30).withDayOfMonth(0)
    }
  }

  test("test_withDayOfMonth_tooHigh") {
    assertThrows[DateTimeException] {
      MonthDay.of(6, 30).withDayOfMonth(32)
    }
  }

  test("test_adjustDate") {
    val test: MonthDay  = MonthDay.of(6, 30)
    val date: LocalDate = LocalDate.of(2007, 1, 1)
    assertEquals(test.adjustInto(date), LocalDate.of(2007, 6, 30))
  }

  test("test_adjustDate_resolve") {
    val test: MonthDay  = MonthDay.of(2, 29)
    val date: LocalDate = LocalDate.of(2007, 6, 30)
    assertEquals(test.adjustInto(date), LocalDate.of(2007, 2, 28))
  }

  test("test_adjustDate_equal") {
    val test: MonthDay  = MonthDay.of(6, 30)
    val date: LocalDate = LocalDate.of(2007, 6, 30)
    assertEquals(test.adjustInto(date), date)
  }

  test("test_adjustDate_null") {
    assertThrows[NullPointerException] {
      TEST_07_15.adjustInto(null.asInstanceOf[LocalDate])
    }
  }

  test("test_isValidYear_june") {
    val test: MonthDay = MonthDay.of(6, 30)
    assertEquals(test.isValidYear(2007), true)
  }

  test("test_isValidYear_febNonLeap") {
    val test: MonthDay = MonthDay.of(2, 29)
    assertEquals(test.isValidYear(2007), false)
  }

  test("test_isValidYear_febLeap") {
    val test: MonthDay = MonthDay.of(2, 29)
    assertEquals(test.isValidYear(2008), true)
  }

  test("test_atYear_int") {
    val test: MonthDay = MonthDay.of(6, 30)
    assertEquals(test.atYear(2008), LocalDate.of(2008, 6, 30))
  }

  test("test_atYear_int_leapYearAdjust") {
    val test: MonthDay = MonthDay.of(2, 29)
    assertEquals(test.atYear(2005), LocalDate.of(2005, 2, 28))
  }

  test("test_atYear_int_invalidYear") {
    assertThrows[DateTimeException] {
      val test: MonthDay = MonthDay.of(6, 30)
      test.atYear(Integer.MIN_VALUE)
    }
  }

  test("test_query") {
    assertEquals(TEST_07_15.query(TemporalQueries.chronology), IsoChronology.INSTANCE)
    assertEquals(TEST_07_15.query(TemporalQueries.localDate), null)
    assertEquals(TEST_07_15.query(TemporalQueries.localTime), null)
    assertEquals(TEST_07_15.query(TemporalQueries.offset), null)
    assertEquals(TEST_07_15.query(TemporalQueries.precision), null)
    assertEquals(TEST_07_15.query(TemporalQueries.zone), null)
    assertEquals(TEST_07_15.query(TemporalQueries.zoneId), null)
  }

  test("test_query_null") {
    assertThrows[Platform.NPE] {
      TEST_07_15.query(null)
    }
  }

  test("test_comparisons") {
    doTest_comparisons_MonthDay(MonthDay.of(1, 1),
                                MonthDay.of(1, 31),
                                MonthDay.of(2, 1),
                                MonthDay.of(2, 29),
                                MonthDay.of(3, 1),
                                MonthDay.of(12, 31)
    )
  }

  private[temporal] def doTest_comparisons_MonthDay(localDates: MonthDay*): Unit = {
    var i: Int = 0
    while (i < localDates.length) {
      {
        val a: MonthDay = localDates(i)

        {
          var j: Int = 0
          while (j < localDates.length) {
            {
              val b: MonthDay = localDates(j)
              if (i < j) {
                assertTrue(a.compareTo(b) < 0)
                assertEquals(a.isBefore(b), true, s"$a <=> $b")
                assertEquals(a.isAfter(b), false, s"$a <=> $b")
                assertEquals(a == b, false, s"$a <=> $b")
              } else if (i > j) {
                assertTrue(a.compareTo(b) > 0)
                assertEquals(a.isBefore(b), false, s"$a <=> $b")
                assertEquals(a.isAfter(b), true, s"$a <=> $b")
                assertEquals(a == b, false, s"$a <=> $b")
              } else {
                assertEquals(a.compareTo(b), 0, s"$a <=> $b")
                assertEquals(a.isBefore(b), false, s"$a <=> $b")
                assertEquals(a.isAfter(b), false, s"$a <=> $b")
                assertEquals(a == b, true, s"$a <=> $b")
              }
            }
            {
              j += 1
              j - 1
            }
          }
        }
      }
      {
        i += 1
        i - 1
      }
    }
  }

  test("test_compareTo_ObjectNull") {
    assertThrows[Platform.NPE] {
      TEST_07_15.compareTo(null)
    }
  }

  test("test_isBefore_ObjectNull") {
    assertThrows[Platform.NPE] {
      TEST_07_15.isBefore(null)
    }
  }

  test("test_isAfter_ObjectNull") {
    assertThrows[Platform.NPE] {
      TEST_07_15.isAfter(null)
    }
  }

  test("test_equals") {
    val a: MonthDay = MonthDay.of(1, 1)
    val b: MonthDay = MonthDay.of(1, 1)
    val c: MonthDay = MonthDay.of(2, 1)
    val d: MonthDay = MonthDay.of(1, 2)
    assertEquals(a == a, true)
    assertEquals(a == b, true)
    assertEquals(a == c, false)
    assertEquals(a == d, false)
    assertEquals(b == a, true)
    assertEquals(b == b, true)
    assertEquals(b == c, false)
    assertEquals(b == d, false)
    assertEquals(c == a, false)
    assertEquals(c == b, false)
    assertEquals(c == c, true)
    assertEquals(c == d, false)
    assertEquals(d == a, false)
    assertEquals(d == b, false)
    assertEquals(d == c, false)
    assertEquals(d == d, true)
  }

  test("test_equals_itself_true") {
    assertEquals(TEST_07_15, TEST_07_15)
  }

  test("test_equals_string_false") {
    assertFalse(TEST_07_15.equals("2007-07-15"))
  }

  test("test_equals_null_false") {
    assertEquals(TEST_07_15 == null, false)
  }

  test("test_hashCode") {
    provider_sampleDates.foreach { case (m, d) =>
      val a: MonthDay = MonthDay.of(m, d)
      assertEquals(a.hashCode, a.hashCode)
      val b: MonthDay = MonthDay.of(m, d)
      assertEquals(a.hashCode, b.hashCode)
    }
  }

  test("test_hashCode_unique") {
    val leapYear: Int                   = 2008
    val uniques: java.util.Set[Integer] = new java.util.HashSet[Integer](366)
    var i: Int                          = 1
    while (i <= 12) {
      var j: Int = 1
      while (j <= 31) {
        if (YearMonth.of(leapYear, i).isValidDay(j))
          assertTrue(uniques.add(MonthDay.of(i, j).hashCode))
        j += 1
      }
      i += 1
    }
  }

  val provider_sampleToString: List[(Int, Int, String)] =
    List((7, 5, "--07-05"), (12, 31, "--12-31"), (1, 2, "--01-02"))

  test("test_toString") {
    provider_sampleToString.foreach { case (m, d, expected) =>
      val test: MonthDay = MonthDay.of(m, d)
      val str: String    = test.toString
      assertEquals(str, expected)
    }
  }

  test("test_format_formatter") {
    val f: DateTimeFormatter = DateTimeFormatter.ofPattern("M d")
    val t: String            = MonthDay.of(12, 3).format(f)
    assertEquals(t, "12 3")
  }

  test("test_format_formatter_null") {
    assertThrows[NullPointerException] {
      MonthDay.of(12, 3).format(null)
    }
  }
}
