/*
 * Copyright (c) 2007-present Stephen Colebourne & Michael Nascimento Santos
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

import org.scalatest.funsuite.AnyFunSuite

/** Test system clock. */
object TestClock_System {
  val MOSCOW: ZoneId = ZoneId.of("Europe/Moscow")
  val PARIS: ZoneId  = ZoneId.of("Europe/Paris")
}

class TestClock_System extends AnyFunSuite with AssertionsHelper {
  test("instant") {
    val system: Clock = Clock.systemUTC
    assertEquals(system.getZone, ZoneOffset.UTC)

    var i: Int = 0
    while (i < 10000) {
      {
        val instant: Instant   = system.instant
        val systemMillis: Long = System.currentTimeMillis
        if (systemMillis - instant.toEpochMilli < 10) {
          i = 9999
        }
      }
      {
        i += 1
        i - 1
      }
    }

    assert(i == 10000)
  }

  test("millis") {
    val system: Clock = Clock.systemUTC
    assertEquals(system.getZone, ZoneOffset.UTC)

    var i: Int = 0
    while (i < 10000) {
      {
        val instant: Long      = system.millis
        val systemMillis: Long = System.currentTimeMillis
        if (systemMillis - instant < 10) {
          i = 9999
        }
      }
      {
        i += 1
        i - 1
      }
    }
    assert(i == 10000)
  }

  test("systemUTC") {
    val test: Clock = Clock.systemUTC
    assertEquals(test.getZone, ZoneOffset.UTC)
    assertEquals(test, Clock.system(ZoneOffset.UTC))
  }

  test("systemDefaultZone") {
    val test: Clock = Clock.systemDefaultZone
    assertEquals(test.getZone, ZoneId.systemDefault)
    assertEquals(test, Clock.system(ZoneId.systemDefault))
  }

  test("system_ZoneId") {
    val test: Clock = Clock.system(TestClock_System.PARIS)
    assertEquals(test.getZone, TestClock_System.PARIS)
  }

  test("zoneId_nullZoneId") {
    assertThrows[NullPointerException] {
      Clock.system(null)
    }
  }

  test("withZone") {
    val test: Clock    = Clock.system(TestClock_System.PARIS)
    val changed: Clock = test.withZone(TestClock_System.MOSCOW)
    assertEquals(test.getZone, TestClock_System.PARIS)
    assertEquals(changed.getZone, TestClock_System.MOSCOW)
  }

  test("withZone_same") {
    val test: Clock    = Clock.system(TestClock_System.PARIS)
    val changed: Clock = test.withZone(TestClock_System.PARIS)
    assertSame(test, changed)
  }

  test("withZone_fromUTC") {
    val test: Clock    = Clock.systemUTC
    val changed: Clock = test.withZone(TestClock_System.PARIS)
    assertEquals(changed.getZone, TestClock_System.PARIS)
  }

  test("withZone_null") {
    assertThrows[NullPointerException] {
      Clock.systemUTC.withZone(null)
    }
  }

  test("equals") {
    val a: Clock = Clock.systemUTC
    val b: Clock = Clock.systemUTC
    assertEquals(a == a, true)
    assertEquals(a == b, true)
    assertEquals(b == a, true)
    assertEquals(b == b, true)
    val c: Clock = Clock.system(TestClock_System.PARIS)
    val d: Clock = Clock.system(TestClock_System.PARIS)
    assertEquals(c == c, true)
    assertEquals(c == d, true)
    assertEquals(d == c, true)
    assertEquals(d == d, true)
    assertEquals(a == c, false)
    assertEquals(c == a, false)
    assertEquals(a == null, false)
    assertNotEquals(a, "other type")
    assertEquals(a == Clock.fixed(Instant.now, ZoneOffset.UTC), false)
  }

  test("hashCode") {
    val a: Clock = Clock.system(ZoneOffset.UTC)
    val b: Clock = Clock.system(ZoneOffset.UTC)
    assertEquals(a.hashCode, a.hashCode)
    assertEquals(a.hashCode, b.hashCode)
    val c: Clock = Clock.system(TestClock_System.PARIS)
    assertEquals(a.hashCode == c.hashCode, false)
  }

  test("toString") {
    val test: Clock = Clock.system(TestClock_System.PARIS)
    assertEquals(test.toString, "SystemClock[Europe/Paris]")
  }
}
