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

/** Test ZoneOffsetTransition. */
object TestZoneOffsetTransition {
  val OFFSET_0100: ZoneOffset = ZoneOffset.ofHours(1)
  val OFFSET_0200: ZoneOffset = ZoneOffset.ofHours(2)
  val OFFSET_0230: ZoneOffset = ZoneOffset.ofHoursMinutes(2, 30)
  val OFFSET_0300: ZoneOffset = ZoneOffset.ofHours(3)
  val OFFSET_0400: ZoneOffset = ZoneOffset.ofHours(4)
}

class TestZoneOffsetTransition extends AnyFunSuite with AssertionsHelper {
  test("factory_nullTransition") {
    assertThrows[NullPointerException] {
      ZoneOffsetTransition.of(null,
                              TestZoneOffsetTransition.OFFSET_0100,
                              TestZoneOffsetTransition.OFFSET_0200)
    }
  }

  test("factory_nullOffsetBefore") {
    assertThrows[NullPointerException] {
      ZoneOffsetTransition.of(LocalDateTime.of(2010, 12, 3, 11, 30),
                              null,
                              TestZoneOffsetTransition.OFFSET_0200)
    }
  }

  test("factory_nullOffsetAfter") {
    assertThrows[NullPointerException] {
      ZoneOffsetTransition.of(LocalDateTime.of(2010, 12, 3, 11, 30),
                              TestZoneOffsetTransition.OFFSET_0200,
                              null)
    }
  }

  test("factory_sameOffset") {
    assertThrows[IllegalArgumentException] {
      ZoneOffsetTransition.of(LocalDateTime.of(2010, 12, 3, 11, 30),
                              TestZoneOffsetTransition.OFFSET_0200,
                              TestZoneOffsetTransition.OFFSET_0200)
    }
  }

  test("factory_noNanos") {
    assertThrows[IllegalArgumentException] {
      ZoneOffsetTransition.of(LocalDateTime.of(2010, 12, 3, 11, 30, 0, 500),
                              TestZoneOffsetTransition.OFFSET_0200,
                              TestZoneOffsetTransition.OFFSET_0300)
    }
  }

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
  }

  test("isValidOffset_gap") {
    val ldt: LocalDateTime = LocalDateTime.of(2010, 3, 31, 1, 0)
    val test: ZoneOffsetTransition = ZoneOffsetTransition.of(ldt,
                                                             TestZoneOffsetTransition.OFFSET_0200,
                                                             TestZoneOffsetTransition.OFFSET_0300)
    assertEquals(test.isValidOffset(TestZoneOffsetTransition.OFFSET_0100), false)
    assertEquals(test.isValidOffset(TestZoneOffsetTransition.OFFSET_0200), false)
    assertEquals(test.isValidOffset(TestZoneOffsetTransition.OFFSET_0230), false)
    assertEquals(test.isValidOffset(TestZoneOffsetTransition.OFFSET_0300), false)
    assertEquals(test.isValidOffset(TestZoneOffsetTransition.OFFSET_0400), false)
  }

  test("isValidOffset_overlap") {
    val ldt: LocalDateTime = LocalDateTime.of(2010, 10, 31, 1, 0)
    val test: ZoneOffsetTransition = ZoneOffsetTransition.of(ldt,
                                                             TestZoneOffsetTransition.OFFSET_0300,
                                                             TestZoneOffsetTransition.OFFSET_0200)
    assertEquals(test.isValidOffset(TestZoneOffsetTransition.OFFSET_0100), false)
    assertEquals(test.isValidOffset(TestZoneOffsetTransition.OFFSET_0200), true)
    assertEquals(test.isValidOffset(TestZoneOffsetTransition.OFFSET_0230), false)
    assertEquals(test.isValidOffset(TestZoneOffsetTransition.OFFSET_0300), true)
    assertEquals(test.isValidOffset(TestZoneOffsetTransition.OFFSET_0400), false)
  }

  test("compareTo") {
    val a: ZoneOffsetTransition = ZoneOffsetTransition.of(
      LocalDateTime.ofEpochSecond(23875287L - 1, 0, TestZoneOffsetTransition.OFFSET_0200),
      TestZoneOffsetTransition.OFFSET_0200,
      TestZoneOffsetTransition.OFFSET_0300
    )
    val b: ZoneOffsetTransition = ZoneOffsetTransition.of(
      LocalDateTime.ofEpochSecond(23875287L, 0, TestZoneOffsetTransition.OFFSET_0300),
      TestZoneOffsetTransition.OFFSET_0300,
      TestZoneOffsetTransition.OFFSET_0200
    )
    val c: ZoneOffsetTransition = ZoneOffsetTransition.of(
      LocalDateTime.ofEpochSecond(23875287L + 1, 0, TestZoneOffsetTransition.OFFSET_0100),
      TestZoneOffsetTransition.OFFSET_0100,
      TestZoneOffsetTransition.OFFSET_0400
    )
    assertEquals(a.compareTo(a) == 0, true)
    assertEquals(a.compareTo(b) < 0, true)
    assertEquals(a.compareTo(c) < 0, true)
    assertEquals(b.compareTo(a) > 0, true)
    assertEquals(b.compareTo(b) == 0, true)
    assertEquals(b.compareTo(c) < 0, true)
    assertEquals(c.compareTo(a) > 0, true)
    assertEquals(c.compareTo(b) > 0, true)
    assertEquals(c.compareTo(c) == 0, true)
  }

  test("compareTo_sameInstant") {
    val a: ZoneOffsetTransition = ZoneOffsetTransition.of(
      LocalDateTime.ofEpochSecond(23875287L, 0, TestZoneOffsetTransition.OFFSET_0200),
      TestZoneOffsetTransition.OFFSET_0200,
      TestZoneOffsetTransition.OFFSET_0300
    )
    val b: ZoneOffsetTransition = ZoneOffsetTransition.of(
      LocalDateTime.ofEpochSecond(23875287L, 0, TestZoneOffsetTransition.OFFSET_0300),
      TestZoneOffsetTransition.OFFSET_0300,
      TestZoneOffsetTransition.OFFSET_0200
    )
    val c: ZoneOffsetTransition = ZoneOffsetTransition.of(
      LocalDateTime.ofEpochSecond(23875287L, 0, TestZoneOffsetTransition.OFFSET_0100),
      TestZoneOffsetTransition.OFFSET_0100,
      TestZoneOffsetTransition.OFFSET_0400
    )
    assertEquals(a.compareTo(a) == 0, true)
    assertEquals(a.compareTo(b) == 0, true)
    assertEquals(a.compareTo(c) == 0, true)
    assertEquals(b.compareTo(a) == 0, true)
    assertEquals(b.compareTo(b) == 0, true)
    assertEquals(b.compareTo(c) == 0, true)
    assertEquals(c.compareTo(a) == 0, true)
    assertEquals(c.compareTo(b) == 0, true)
    assertEquals(c.compareTo(c) == 0, true)
  }

  test("equals") {
    val ldtA: LocalDateTime = LocalDateTime.of(2010, 3, 31, 1, 0)
    val a1: ZoneOffsetTransition = ZoneOffsetTransition.of(ldtA,
                                                           TestZoneOffsetTransition.OFFSET_0200,
                                                           TestZoneOffsetTransition.OFFSET_0300)
    val a2: ZoneOffsetTransition = ZoneOffsetTransition.of(ldtA,
                                                           TestZoneOffsetTransition.OFFSET_0200,
                                                           TestZoneOffsetTransition.OFFSET_0300)
    val ldtB: LocalDateTime = LocalDateTime.of(2010, 10, 31, 1, 0)
    val b: ZoneOffsetTransition = ZoneOffsetTransition.of(ldtB,
                                                          TestZoneOffsetTransition.OFFSET_0300,
                                                          TestZoneOffsetTransition.OFFSET_0200)
    assertEquals(a1 == a1, true)
    assertEquals(a1 == a2, true)
    assertEquals(a1 == b, false)
    assertEquals(a2 == a1, true)
    assertEquals(a2 == a2, true)
    assertEquals(a2 == b, false)
    assertEquals(b == a1, false)
    assertEquals(b == a2, false)
    assertEquals(b == b, true)
    assertNotEquals(a1, "")
    assertEquals(a1 == null, false)
  }

  test("hashCode_floatingWeek_gap_notEndOfDay") {
    val ldtA: LocalDateTime = LocalDateTime.of(2010, 3, 31, 1, 0)
    val a1: ZoneOffsetTransition = ZoneOffsetTransition.of(ldtA,
                                                           TestZoneOffsetTransition.OFFSET_0200,
                                                           TestZoneOffsetTransition.OFFSET_0300)
    val a2: ZoneOffsetTransition = ZoneOffsetTransition.of(ldtA,
                                                           TestZoneOffsetTransition.OFFSET_0200,
                                                           TestZoneOffsetTransition.OFFSET_0300)
    val ldtB: LocalDateTime = LocalDateTime.of(2010, 10, 31, 1, 0)
    val b: ZoneOffsetTransition = ZoneOffsetTransition.of(ldtB,
                                                          TestZoneOffsetTransition.OFFSET_0300,
                                                          TestZoneOffsetTransition.OFFSET_0200)
    assertEquals(a1.hashCode, a1.hashCode)
    assertEquals(a1.hashCode, a2.hashCode)
    assertEquals(b.hashCode, b.hashCode)
  }

  test("toString_gap") {
    val ldt: LocalDateTime = LocalDateTime.of(2010, 3, 31, 1, 0)
    val test: ZoneOffsetTransition = ZoneOffsetTransition.of(ldt,
                                                             TestZoneOffsetTransition.OFFSET_0200,
                                                             TestZoneOffsetTransition.OFFSET_0300)
    assertEquals(test.toString, "Transition[Gap at 2010-03-31T01:00+02:00 to +03:00]")
  }

  test("toString_overlap") {
    val ldt: LocalDateTime = LocalDateTime.of(2010, 10, 31, 1, 0)
    val test: ZoneOffsetTransition = ZoneOffsetTransition.of(ldt,
                                                             TestZoneOffsetTransition.OFFSET_0300,
                                                             TestZoneOffsetTransition.OFFSET_0200)
    assertEquals(test.toString, "Transition[Overlap at 2010-10-31T01:00+03:00 to +02:00]")
  }
}
