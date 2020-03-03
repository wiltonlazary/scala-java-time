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
import org.threeten.bp.Month
import org.threeten.bp.temporal.TemporalAdjusters
import org.threeten.bp.chrono.internal.TTBPJapaneseEra

/** Test. */
class TestJapaneseChronology extends AnyFunSuite with AssertionsHelper {
  test("test_chrono_byName") {
    val c: Chronology    = JapaneseChronology.INSTANCE
    val test: Chronology = Chronology.of("Japanese")
    assertNotNull(test, "The Japanese calendar could not be found byName")
    assertEquals(test.getId, "Japanese", "ID mismatch")
    assertEquals(test.getCalendarType, "japanese", "Type mismatch")
    assertEquals(test, c)
  }

  val data_samples: List[(ChronoLocalDate, LocalDate)] =
    List(
      (JapaneseChronology.INSTANCE.date(1890, 3, 3), LocalDate.of(1890, 3, 3)),
      (JapaneseChronology.INSTANCE.date(1890, 10, 28), LocalDate.of(1890, 10, 28)),
      (JapaneseChronology.INSTANCE.date(1890, 10, 29), LocalDate.of(1890, 10, 29))
    )

  test("test_toLocalDate") {
    data_samples.foreach {
      case (jdate, iso) =>
        assertEquals(LocalDate.from(jdate), iso)
    }
  }

  test("test_fromCalendrical") {
    data_samples.foreach {
      case (jdate, iso) =>
        assertEquals(JapaneseChronology.INSTANCE.date(iso), jdate)
    }
  }

  val data_badDates: List[(Int, Int, Int)] =
    List((1728, 0, 0),
         (1890, 0, 0),
         (1890, -1, 1),
         (1890, 0, 1),
         (1890, 14, 1),
         (1890, 15, 1),
         (1890, 1, -1),
         (1890, 1, 0),
         (1890, 1, 32),
         (1890, 12, -1),
         (1890, 12, 0),
         (1890, 12, 32))

  test("test_badDates") {
    data_badDates.foreach {
      case (year, month, dom) =>
        assertThrows[DateTimeException] {
          JapaneseChronology.INSTANCE.date(year, month, dom)
        }
    }
  }

  test("test_adjust1") {
    val base: ChronoLocalDate = JapaneseChronology.INSTANCE.date(1890, 10, 29)
    val test: ChronoLocalDate = base.`with`(TemporalAdjusters.lastDayOfMonth)
    assertEquals(test, JapaneseChronology.INSTANCE.date(1890, 10, 31))
  }

  test("test_adjust2") {
    val base: ChronoLocalDate = JapaneseChronology.INSTANCE.date(1890, 12, 2)
    val test: ChronoLocalDate = base.`with`(TemporalAdjusters.lastDayOfMonth)
    assertEquals(test, JapaneseChronology.INSTANCE.date(1890, 12, 31))
  }

  test("test_adjust_toLocalDate") {
    val jdate: ChronoLocalDate = JapaneseChronology.INSTANCE.date(1890, 1, 4)
    val test: ChronoLocalDate  = jdate.`with`(LocalDate.of(2012, 7, 6))
    assertEquals(test, JapaneseChronology.INSTANCE.date(2012, 7, 6))
  }

  test("test_adjust_toMonth") {
    assertThrows[DateTimeException] {
      val jdate: ChronoLocalDate = JapaneseChronology.INSTANCE.date(1890, 1, 4)
      jdate.`with`(Month.APRIL)
    }
  }

  test("test_LocalDate_adjustToJapaneseDate") {
    val jdate: ChronoLocalDate = JapaneseChronology.INSTANCE.date(1890, 10, 29)
    val test: LocalDate        = LocalDate.MIN.`with`(jdate)
    assertEquals(test, LocalDate.of(1890, 10, 29))
  }

  test("test_LocalDateTime_adjustToJapaneseDate") {
    val jdate: ChronoLocalDate = JapaneseChronology.INSTANCE.date(1890, 10, 29)
    val test: LocalDateTime    = LocalDateTime.MIN.`with`(jdate)
    assertEquals(test, LocalDateTime.of(1890, 10, 29, 0, 0))
  }

  val data_japansesEras: List[(Era, Int, String)] = {
    List((JapaneseEra.MEIJI, -1, "Meiji"),
         (JapaneseEra.TAISHO, 0, "Taisho"),
         (JapaneseEra.SHOWA, 1, "Showa"),
         (JapaneseEra.HEISEI, 2, "Heisei"))
  }

  test("test_Japanese_Eras") {
    data_japansesEras.foreach {
      case (era, eraValue, name) =>
        assertEquals(era.getValue, eraValue, "EraValue")
        assertEquals(era.toString, name, "Era Name")
        assertEquals(era, JapaneseChronology.INSTANCE.eraOf(eraValue), "JapaneseChrono.eraOf()")
        assertEquals(JapaneseEra.valueOf(name), era)
        val eras: java.util.List[Era] = JapaneseChronology.INSTANCE.eras
        assertTrue(eras.contains(era))
    }
  }

  test("test_Japanese_badEras") {
    val badEras: Array[Int] = Array(-1000, -998, -997, -2, 3, 4, 1000)
    for (badEra <- badEras)
      try {
        val era: Era = JapaneseChronology.INSTANCE.eraOf(badEra)
        fail(s"JapaneseChronology.eraOf returned $era + for invalid eraValue $badEra")
      } catch {
        case _: DateTimeException =>
      }
    try {
      val era = JapaneseEra.valueOf("Rubbish")
      fail("JapaneseEra.valueOf returned " + era + " + for invalid era name Rubbish")
    } catch {
      case _: IllegalArgumentException =>
      // ignore expected exception
    }
  }

  test("test_Japanese_registerEra") {
    try {
      val showaEndDate = LocalDate.of(1926, 12, 25)
      TTBPJapaneseEra.registerEra(showaEndDate, "TestAdditional")
      fail("JapaneseEra.registerEra should have failed")
    } catch {
      case _: DateTimeException =>
      // ignore expected exception
    }
    val additional = TTBPJapaneseEra.registerEra(LocalDate.of(2100, 1, 1), "TestAdditional")
    assertEquals(JapaneseEra.of(3), additional)
    assertEquals(JapaneseEra.valueOf("TestAdditional"), additional)
    assertEquals(JapaneseEra.values.apply(4), additional)
    try {
      TTBPJapaneseEra.registerEra(LocalDate.of(2200, 1, 1), "TestAdditional2")
      fail("JapaneseEra.registerEra should have failed")

    } catch {
      case _: DateTimeException =>
      // ignore expected exception
    }
  }

  val data_toString: List[(ChronoLocalDate, String)] =
    List(
      (JapaneseChronology.INSTANCE.date(1873, 9, 8), "Japanese Meiji 6-09-08"),
      (JapaneseChronology.INSTANCE.date(1912, 7, 29), "Japanese Meiji 45-07-29"),
      (JapaneseChronology.INSTANCE.date(1912, 7, 30), "Japanese Taisho 1-07-30"),
      (JapaneseChronology.INSTANCE.date(1926, 12, 24), "Japanese Taisho 15-12-24"),
      (JapaneseChronology.INSTANCE.date(1926, 12, 25), "Japanese Showa 1-12-25"),
      (JapaneseChronology.INSTANCE.date(1989, 1, 7), "Japanese Showa 64-01-07"),
      (JapaneseChronology.INSTANCE.date(1989, 1, 8), "Japanese Heisei 1-01-08"),
      (JapaneseChronology.INSTANCE.date(2012, 12, 6), "Japanese Heisei 24-12-06")
    )

  test("test_toString") {
    data_toString.foreach {
      case (jdate, expected) =>
        assertEquals(jdate.toString, expected)
    }
  }

  test("test_equals_true") {
    assertTrue(JapaneseChronology.INSTANCE == JapaneseChronology.INSTANCE)
  }

  test("test_equals_false") {
    assertFalse(JapaneseChronology.INSTANCE == IsoChronology.INSTANCE)
  }
}
