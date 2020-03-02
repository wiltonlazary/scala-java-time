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

import org.scalatest.FunSuite
import org.threeten.bp._
import org.threeten.bp.zone.ZoneOffsetTransitionRule.TimeDefinition

class TestZoneOffsetTransitionRuleSerialization
    extends FunSuite
    with AssertionsHelper
    with AbstractTest {
  test("getters_floatingWeek") {
    val test: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      timeEndOfDay = false,
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
    assertSerializable(test)
  }

  test("getters_floatingWeekBackwards") {
    val test: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      -1,
      DayOfWeek.SUNDAY,
      TestZoneOffsetTransitionRule.TIME_0100,
      timeEndOfDay = false,
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
    assertSerializable(test)
  }

  test("getters_fixedDate") {
    val test: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      null,
      TestZoneOffsetTransitionRule.TIME_0100,
      timeEndOfDay = false,
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
    assertSerializable(test)
  }

  test("serialization_unusualOffsets") {
    val test: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      null,
      TestZoneOffsetTransitionRule.TIME_0100,
      timeEndOfDay = false,
      TimeDefinition.STANDARD,
      ZoneOffset.ofHoursMinutesSeconds(-12, -20, -50),
      ZoneOffset.ofHoursMinutesSeconds(-4, -10, -34),
      ZoneOffset.ofHours(-18)
    )
    assertSerializable(test)
  }

  test("serialization_endOfDay") {
    val test: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.FRIDAY,
      LocalTime.MIDNIGHT,
      timeEndOfDay = true,
      TimeDefinition.UTC,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertSerializable(test)
  }

  test("serialization_unusualTime") {
    val test: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.WEDNESDAY,
      LocalTime.of(13, 34, 56),
      timeEndOfDay = false,
      TimeDefinition.STANDARD,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertSerializable(test)
  }

  test("serialization_format") {
    val test: ZoneOffsetTransitionRule = ZoneOffsetTransitionRule.of(
      Month.MARCH,
      20,
      DayOfWeek.TUESDAY,
      LocalTime.of(13, 34, 56),
      timeEndOfDay = false,
      TimeDefinition.STANDARD,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0200,
      TestZoneOffsetTransitionRule.OFFSET_0300
    )
    assertEqualsSerialisedForm(test)
  }
}
