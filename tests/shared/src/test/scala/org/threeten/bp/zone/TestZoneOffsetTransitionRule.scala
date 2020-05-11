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
package org.threeten.bp.zone

import org.scalatest.funsuite.AnyFunSuite
import org.threeten.bp._
import org.threeten.bp.zone.ZoneOffsetTransitionRule.TimeDefinition

/** Test ZoneOffsetTransitionRule. */
object TestZoneOffsetTransitionRule {
  val TIME_0100: LocalTime    = LocalTime.of(1, 0)
  val OFFSET_0200: ZoneOffset = ZoneOffset.ofHours(2)
  val OFFSET_0300: ZoneOffset = ZoneOffset.ofHours(3)
}

class TestZoneOffsetTransitionRule extends AnyFunSuite with AssertionsHelper {
  test("factory_nullMonth") {
    assertThrows[NullPointerException] {
      ZoneOffsetTransitionRule.of(
        null,
        20,
        DayOfWeek.SUNDAY,
        TestZoneOffsetTransitionRule.TIME_0100,
        false,
        TimeDefinition.WALL,
        TestZoneOffsetTransitionRule.OFFSET_0200,
        TestZoneOffsetTransitionRule.OFFSET_0200,
        TestZoneOffsetTransitionRule.OFFSET_0300
      )
    }
  }

  test("factory_nullTime") {
    assertThrows[NullPointerException] {
      ZoneOffsetTransitionRule.of(
        Month.MARCH,
        20,
        DayOfWeek.SUNDAY,
        null,
        false,
        TimeDefinition.WALL,
        TestZoneOffsetTransitionRule.OFFSET_0200,
        TestZoneOffsetTransitionRule.OFFSET_0200,
        TestZoneOffsetTransitionRule.OFFSET_0300
      )
    }
  }

  test("factory_nullTimeDefinition") {
    assertThrows[NullPointerException] {
      ZoneOffsetTransitionRule.of(
        Month.MARCH,
        20,
        DayOfWeek.SUNDAY,
        TestZoneOffsetTransitionRule.TIME_0100,
        false,
        null,
        TestZoneOffsetTransitionRule.OFFSET_0200,
        TestZoneOffsetTransitionRule.OFFSET_0200,
        TestZoneOffsetTransitionRule.OFFSET_0300
      )
    }
  }

  test("factory_nullStandardOffset") {
    assertThrows[NullPointerException] {
      ZoneOffsetTransitionRule.of(
        Month.MARCH,
        20,
        DayOfWeek.SUNDAY,
        TestZoneOffsetTransitionRule.TIME_0100,
        false,
        TimeDefinition.WALL,
        null,
        TestZoneOffsetTransitionRule.OFFSET_0200,
        TestZoneOffsetTransitionRule.OFFSET_0300
      )
    }
  }

  test("factory_nullOffsetBefore") {
    assertThrows[NullPointerException] {
      ZoneOffsetTransitionRule.of(
        Month.MARCH,
        20,
        DayOfWeek.SUNDAY,
        TestZoneOffsetTransitionRule.TIME_0100,
        false,
        TimeDefinition.WALL,
        TestZoneOffsetTransitionRule.OFFSET_0200,
        null,
        TestZoneOffsetTransitionRule.OFFSET_0300
      )
    }
  }

  test("factory_nullOffsetAfter") {
    assertThrows[NullPointerException] {
      ZoneOffsetTransitionRule.of(
        Month.MARCH,
        20,
        DayOfWeek.SUNDAY,
        TestZoneOffsetTransitionRule.TIME_0100,
        false,
        TimeDefinition.WALL,
        TestZoneOffsetTransitionRule.OFFSET_0200,
        TestZoneOffsetTransitionRule.OFFSET_0200,
        null
      )
    }
  }

  test("factory_invalidDayOfMonthIndicator_tooSmall") {
    assertThrows[IllegalArgumentException] {
      ZoneOffsetTransitionRule.of(
        Month.MARCH,
        -29,
        DayOfWeek.SUNDAY,
        TestZoneOffsetTransitionRule.TIME_0100,
        false,
        TimeDefinition.WALL,
        TestZoneOffsetTransitionRule.OFFSET_0200,
        TestZoneOffsetTransitionRule.OFFSET_0200,
        TestZoneOffsetTransitionRule.OFFSET_0300
      )
    }
  }

  test("factory_invalidDayOfMonthIndicator_zero") {
    assertThrows[IllegalArgumentException] {
      ZoneOffsetTransitionRule.of(
        Month.MARCH,
        0,
        DayOfWeek.SUNDAY,
        TestZoneOffsetTransitionRule.TIME_0100,
        false,
        TimeDefinition.WALL,
        TestZoneOffsetTransitionRule.OFFSET_0200,
        TestZoneOffsetTransitionRule.OFFSET_0200,
        TestZoneOffsetTransitionRule.OFFSET_0300
      )
    }
  }

  test("factory_invalidDayOfMonthIndicator_tooLarge") {
    assertThrows[IllegalArgumentException] {
      ZoneOffsetTransitionRule.of(
        Month.MARCH,
        32,
        DayOfWeek.SUNDAY,
        TestZoneOffsetTransitionRule.TIME_0100,
        false,
        TimeDefinition.WALL,
        TestZoneOffsetTransitionRule.OFFSET_0200,
        TestZoneOffsetTransitionRule.OFFSET_0200,
        TestZoneOffsetTransitionRule.OFFSET_0300
      )
    }
  }

  test("factory_invalidMidnightFlag") {
    assertThrows[IllegalArgumentException] {
      ZoneOffsetTransitionRule.of(
        Month.MARCH,
        20,
        DayOfWeek.SUNDAY,
        TestZoneOffsetTransitionRule.TIME_0100,
        true,
        TimeDefinition.WALL,
        TestZoneOffsetTransitionRule.OFFSET_0200,
        TestZoneOffsetTransitionRule.OFFSET_0200,
        TestZoneOffsetTransitionRule.OFFSET_0300
      )
    }
  }

  test("getters_floatingWeek") {
    val test: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertEquals(test.getMonth, Month.MARCH)
    assertEquals(test.getDayOfMonthIndicator, 20)
    assertEquals(test.getDayOfWeek, DayOfWeek.SUNDAY)
    assertEquals(test.getLocalTime, TestZoneOffsetTransitionRule.TIME_0100)
    assertEquals(test.isMidnightEndOfDay, false)
    assertEquals(test.getTimeDefinition, TimeDefinition.WALL)
    assertEquals(test.getStandardOffset, TestZoneOffsetTransitionRule.OFFSET_0200)
    assertEquals(test.getOffsetBefore, TestZoneOffsetTransitionRule.OFFSET_0200)
    assertEquals(test.getOffsetAfter, TestZoneOffsetTransitionRule.OFFSET_0300)
  }

  test("getters_floatingWeekBackwards") {
    val test: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      -1,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertEquals(test.getMonth, Month.MARCH)
    assertEquals(test.getDayOfMonthIndicator, -1)
    assertEquals(test.getDayOfWeek, DayOfWeek.SUNDAY)
    assertEquals(test.getLocalTime, TestZoneOffsetTransitionRule.TIME_0100)
    assertEquals(test.isMidnightEndOfDay, false)
    assertEquals(test.getTimeDefinition, TimeDefinition.WALL)
    assertEquals(test.getStandardOffset, TestZoneOffsetTransitionRule.OFFSET_0200)
    assertEquals(test.getOffsetBefore, TestZoneOffsetTransitionRule.OFFSET_0200)
    assertEquals(test.getOffsetAfter, TestZoneOffsetTransitionRule.OFFSET_0300)
  }

  test("getters_fixedDate") {
    val test: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      null,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertEquals(test.getMonth, Month.MARCH)
    assertEquals(test.getDayOfMonthIndicator, 20)
    assertEquals(test.getDayOfWeek, null)
    assertEquals(test.getLocalTime, TestZoneOffsetTransitionRule.TIME_0100)
    assertEquals(test.isMidnightEndOfDay, false)
    assertEquals(test.getTimeDefinition, TimeDefinition.WALL)
    assertEquals(test.getStandardOffset, TestZoneOffsetTransitionRule.OFFSET_0200)
    assertEquals(test.getOffsetBefore, TestZoneOffsetTransitionRule.OFFSET_0200)
    assertEquals(test.getOffsetAfter, TestZoneOffsetTransitionRule.OFFSET_0300)
  }

  test("createTransition_floatingWeek_gap_notEndOfDay") {
    val test: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    val trans: ZoneOffsetTransition    =
      new ZoneOffsetTransition(LocalDateTime.of(2000, Month.MARCH, 26, 1, 0),
                               TestZoneOffsetTransitionRule.OFFSET_0200,
                               TestZoneOffsetTransitionRule.OFFSET_0300
      )
    assertEquals(test.createTransition(2000), trans)
  }

  test("createTransition_floatingWeek_overlap_endOfDay") {
    val test: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      LocalTime.MIDNIGHT,
      true,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300,
      TestZoneOffsetTransitionRule.OFFSET_0200
    )
    val trans: ZoneOffsetTransition    =
      new ZoneOffsetTransition(LocalDateTime.of(2000, Month.MARCH, 27, 0, 0),
                               TestZoneOffsetTransitionRule.OFFSET_0300,
                               TestZoneOffsetTransitionRule.OFFSET_0200
      )
    assertEquals(test.createTransition(2000), trans)
  }

  test("createTransition_floatingWeekBackwards_last") {
    val test: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      -1,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    val trans: ZoneOffsetTransition    =
      new ZoneOffsetTransition(LocalDateTime.of(2000, Month.MARCH, 26, 1, 0),
                               TestZoneOffsetTransitionRule.OFFSET_0200,
                               TestZoneOffsetTransitionRule.OFFSET_0300
      )
    assertEquals(test.createTransition(2000), trans)
  }

  test("createTransition_floatingWeekBackwards_seventhLast") {
    val test: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      -7,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    val trans: ZoneOffsetTransition    =
      new ZoneOffsetTransition(LocalDateTime.of(2000, Month.MARCH, 19, 1, 0),
                               TestZoneOffsetTransitionRule.OFFSET_0200,
                               TestZoneOffsetTransitionRule.OFFSET_0300
      )
    assertEquals(test.createTransition(2000), trans)
  }

  test("createTransition_floatingWeekBackwards_secondLast") {
    val test: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      -2,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    val trans: ZoneOffsetTransition    =
      new ZoneOffsetTransition(LocalDateTime.of(2000, Month.MARCH, 26, 1, 0),
                               TestZoneOffsetTransitionRule.OFFSET_0200,
                               TestZoneOffsetTransitionRule.OFFSET_0300
      )
    assertEquals(test.createTransition(2000), trans)
  }

  test("createTransition_fixedDate") {
    val test: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      null,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.STANDARD,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    val trans: ZoneOffsetTransition    =
      new ZoneOffsetTransition(LocalDateTime.of(2000, Month.MARCH, 20, 1, 0),
                               TestZoneOffsetTransitionRule.OFFSET_0200,
                               TestZoneOffsetTransitionRule.OFFSET_0300
      )
    assertEquals(test.createTransition(2000), trans)
  }

  test("equals_monthDifferent") {
    val a: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    val b: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.APRIL,
      20,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertEquals(a == a, true)
    assertEquals(a == b, false)
    assertEquals(b == a, false)
    assertEquals(b == b, true)
  }

  test("equals_dayOfMonthDifferent") {
    val a: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    val b: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      21,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertEquals(a == a, true)
    assertEquals(a == b, false)
    assertEquals(b == a, false)
    assertEquals(b == b, true)
  }

  test("equals_dayOfWeekDifferent") {
    val a: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    val b: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SATURDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertEquals(a == a, true)
    assertEquals(a == b, false)
    assertEquals(b == a, false)
    assertEquals(b == b, true)
  }

  test("equals_dayOfWeekDifferentNull") {
    val a: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    val b: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      null,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertEquals(a == a, true)
    assertEquals(a == b, false)
    assertEquals(b == a, false)
    assertEquals(b == b, true)
  }

  test("equals_localTimeDifferent") {
    val a: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    val b: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      LocalTime.MIDNIGHT,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertEquals(a == a, true)
    assertEquals(a == b, false)
    assertEquals(b == a, false)
    assertEquals(b == b, true)
  }

  test("equals_endOfDayDifferent") {
    val a: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      LocalTime.MIDNIGHT,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    val b: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      LocalTime.MIDNIGHT,
      true,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertEquals(a == a, true)
    assertEquals(a == b, false)
    assertEquals(b == a, false)
    assertEquals(b == b, true)
  }

  test("equals_timeDefinitionDifferent") {
    val a: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    val b: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.STANDARD,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertEquals(a == a, true)
    assertEquals(a == b, false)
    assertEquals(b == a, false)
    assertEquals(b == b, true)
  }

  test("equals_standardOffsetDifferent") {
    val a: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    val b: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0300,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertEquals(a == a, true)
    assertEquals(a == b, false)
    assertEquals(b == a, false)
    assertEquals(b == b, true)
  }

  test("equals_offsetBeforeDifferent") {
    val a: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    val b: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertEquals(a == a, true)
    assertEquals(a == b, false)
    assertEquals(b == a, false)
    assertEquals(b == b, true)
  }

  test("equals_offsetAfterDifferent") {
    val a: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    val b: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200
    )
    assertEquals(a == a, true)
    assertEquals(a == b, false)
    assertEquals(b == a, false)
    assertEquals(b == b, true)
  }

  test("equals_string_false") {
    val a: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertNotEquals(a, "TZDB")
  }

  test("equals_null_false") {
    val a: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertEquals(a == null, false)
  }

  test("hashCode_floatingWeek_gap_notEndOfDay") {
    val a: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    val b: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertEquals(a.hashCode, b.hashCode)
  }

  test("hashCode_floatingWeek_overlap_endOfDay_nullDayOfWeek") {
    val a: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.OCTOBER,
      20,
      null,
      LocalTime.MIDNIGHT,
      true,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300,
      TestZoneOffsetTransitionRule.OFFSET_0200
    )
    val b: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.OCTOBER,
      20,
      null,
      LocalTime.MIDNIGHT,
      true,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300,
      TestZoneOffsetTransitionRule.OFFSET_0200
    )
    assertEquals(a.hashCode, b.hashCode)
  }

  test("hashCode_floatingWeekBackwards") {
    val a: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      -1,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    val b: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      -1,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertEquals(a.hashCode, b.hashCode)
  }

  test("hashCode_fixedDate") {
    val a: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      null,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.STANDARD,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    val b: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      null,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.STANDARD,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertEquals(a.hashCode, b.hashCode)
  }

  test("toString_floatingWeek_gap_notEndOfDay") {
    val test: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertEquals(
      test.toString,
      "TransitionRule[Gap +02:00 to +03:00, SUNDAY on or after MARCH 20 at 01:00 WALL, standard offset +02:00]"
    )
  }

  test("toString_floatingWeek_overlap_endOfDay") {
    val test: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.OCTOBER,
      20,
      DayOfWeek.SUNDAY,
      LocalTime.MIDNIGHT,
      true,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300,
      TestZoneOffsetTransitionRule.OFFSET_0200
    )
    assertEquals(
      test.toString,
      "TransitionRule[Overlap +03:00 to +02:00, SUNDAY on or after OCTOBER 20 at 24:00 WALL, standard offset +02:00]"
    )
  }

  test("toString_floatingWeekBackwards_last") {
    val test: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      -1,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertEquals(
      test.toString,
      "TransitionRule[Gap +02:00 to +03:00, SUNDAY on or before last day of MARCH at 01:00 WALL, standard offset +02:00]"
    )
  }

  test("toString_floatingWeekBackwards_secondLast") {
    val test: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      -2,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.WALL,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertEquals(
      test.toString,
      "TransitionRule[Gap +02:00 to +03:00, SUNDAY on or before last day minus 1 of MARCH at 01:00 WALL, standard offset +02:00]"
    )
  }

  test("toString_fixedDate") {
    val test: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      null,
      TestZoneOffsetTransitionRule.TIME_0100,
      false,
      TimeDefinition.STANDARD,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertEquals(
      test.toString,
      "TransitionRule[Gap +02:00 to +03:00, MARCH 20 at 01:00 STANDARD, standard offset +02:00]"
    )
  }
}
