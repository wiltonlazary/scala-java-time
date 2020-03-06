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
import org.threeten.bp.temporal.ChronoUnit.HOURS

class TestZoneOffsetTransitionSerialization
    extends AnyFunSuite
    with AssertionsHelper
    with AbstractTest {

  test("getters_gap") {
    val before: LocalDateTime = LocalDateTime.of(2010, 3, 31, 1, 0)
    val after: LocalDateTime  = LocalDateTime.of(2010, 3, 31, 2, 0)
    val test: ZoneOffsetTransition = ZoneOffsetTransition.of(before,
                                                             TestZoneOffsetTransition.OFFSET_0200,
                                                             TestZoneOffsetTransition.OFFSET_0300)
    assertEquals(test.isGap, true)
    assertEquals(test.isOverlap, false)
    assertEquals(test.getDateTimeBefore, before)
    assertEquals(test.getDateTimeAfter, after)
    assertEquals(test.getInstant, before.toInstant(TestZoneOffsetTransition.OFFSET_0200))
    assertEquals(test.getOffsetBefore, TestZoneOffsetTransition.OFFSET_0200)
    assertEquals(test.getOffsetAfter, TestZoneOffsetTransition.OFFSET_0300)
    assertEquals(test.getDuration, Duration.of(1, HOURS))
    assertSerializable(test)
  }

  test("getters_overlap") {
    val before: LocalDateTime = LocalDateTime.of(2010, 10, 31, 1, 0)
    val after: LocalDateTime  = LocalDateTime.of(2010, 10, 31, 0, 0)
    val test: ZoneOffsetTransition = ZoneOffsetTransition.of(before,
                                                             TestZoneOffsetTransition.OFFSET_0300,
                                                             TestZoneOffsetTransition.OFFSET_0200)
    assertEquals(test.isGap, false)
    assertEquals(test.isOverlap, true)
    assertEquals(test.getDateTimeBefore, before)
    assertEquals(test.getDateTimeAfter, after)
    assertEquals(test.getInstant, before.toInstant(TestZoneOffsetTransition.OFFSET_0300))
    assertEquals(test.getOffsetBefore, TestZoneOffsetTransition.OFFSET_0300)
    assertEquals(test.getOffsetAfter, TestZoneOffsetTransition.OFFSET_0200)
    assertEquals(test.getDuration, Duration.of(-1, HOURS))
    assertSerializable(test)
  }

  test("serialization_unusual1") {
    val ldt: LocalDateTime = LocalDateTime.of(Year.MAX_VALUE, 12, 31, 1, 31, 53)
    val test: ZoneOffsetTransition =
      ZoneOffsetTransition.of(ldt, ZoneOffset.of("+02:04:56"), ZoneOffset.of("-10:02:34"))
    assertSerializable(test)
  }

  test("serialization_unusual2") {
    val ldt: LocalDateTime = LocalDateTime.of(Year.MIN_VALUE, 1, 1, 12, 1, 3)
    val test: ZoneOffsetTransition =
      ZoneOffsetTransition.of(ldt, ZoneOffset.of("+02:04:56"), ZoneOffset.of("+10:02:34"))
    assertSerializable(test)
  }

  test("serialization_format") {
    val ldt: LocalDateTime = LocalDateTime.of(Year.MIN_VALUE, 1, 1, 12, 1, 3)
    val test: ZoneOffsetTransition =
      ZoneOffsetTransition.of(ldt, ZoneOffset.of("+02:04:56"), ZoneOffset.of("+10:02:34"))
    assertEqualsSerialisedForm(test)
  }
}
