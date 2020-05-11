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

import java.util.Locale
import org.threeten.bp.temporal.ChronoField
import org.threeten.bp.AssertionsHelper

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.BeforeAndAfterEach

/** Test Chrono class. */
class TestChronology extends AnyFunSuite with BeforeAndAfterEach with AssertionsHelper {
  override def beforeEach(): Unit = {
    var c: Chronology = null
    c = HijrahChronology.INSTANCE
    c = IsoChronology.INSTANCE
    // c = JapaneseChronology.INSTANCE
    c = MinguoChronology.INSTANCE
    c = ThaiBuddhistChronology.INSTANCE
  }

  val data_of_calendars: List[(String, String, String)] =
    List(
      ("Hijrah-umalqura", "islamic-umalqura", "Hijrah calendar"),
      ("ISO", "iso8601", "ISO calendar"),
      // ("Japanese", "japanese", "Japanese calendar"),
      ("Minguo", "roc", "Minguo Calendar"),
      ("ThaiBuddhist", "buddhist", "ThaiBuddhist calendar")
    )

  test("test_getters") {
    data_of_calendars.foreach {
      case (chronoId, calendarSystemType, _) =>
        val chrono: Chronology = Chronology.of(chronoId)
        assertNotNull(chrono)
        assertEquals(chrono.getId, chronoId)
        assertEquals(chrono.getCalendarType, calendarSystemType)
      case _                                 =>
        fail()
    }
  }

  test("test_required_calendars") {
    data_of_calendars.foreach {
      case (chronoId, calendarSystemType, _) =>
        var chrono: Chronology              = Chronology.of(chronoId)
        assertNotNull(chrono)
        chrono = Chronology.of(calendarSystemType)
        assertNotNull(chrono)
        val cals: java.util.Set[Chronology] = Chronology.getAvailableChronologies
        assertTrue(cals.contains(chrono))
      case _                                 =>
        fail()
    }
  }

  test("test_calendar_list") {
    val chronos: java.util.Set[Chronology] = Chronology.getAvailableChronologies
    assertNotNull(chronos, "Required list of calendars must be non-null")
    import scala.collection.JavaConverters._
    for (chrono <- chronos.asScala) {
      val lookup: Chronology = Chronology.of(chrono.getId)
      assertNotNull(lookup, s"Required calendar not found: $chrono")
    }
    assertTrue(chronos.size >= data_of_calendars.length)
  }

  /** Compute the number of days from the Epoch and compute the date from the number of days. */
  test("test_epoch") {
    data_of_calendars.foreach {
      case (name, _, _) =>
        val chrono: Chronology     = Chronology.of(name)
        val date1: ChronoLocalDate = chrono.dateNow
        val epoch1: Long           = date1.getLong(ChronoField.EPOCH_DAY)
        val date2: ChronoLocalDate = date1.`with`(ChronoField.EPOCH_DAY, epoch1)
        assertEquals(date1,
                     date2,
                     "Date from epoch day is not same date: " + date1 + " != " + date2
        )
        val epoch2: Long           = date1.getLong(ChronoField.EPOCH_DAY)
        assertEquals(epoch1, epoch2, "Epoch day not the same: " + epoch1 + " != " + epoch2)
    }
  }

  val data_CalendarType: List[(Chronology, String)] =
    List(
      (HijrahChronology.INSTANCE, "islamic-umalqura"),
      (IsoChronology.INSTANCE, "iso8601"),
      // (JapaneseChronology.INSTANCE, "japanese"),
      (MinguoChronology.INSTANCE, "roc"),
      (ThaiBuddhistChronology.INSTANCE, "buddhist")
    )

  test("test_getCalendarType") {
    data_CalendarType.foreach {
      case (chrono, calendarType) =>
        assertEquals(chrono.getCalendarType, calendarType)
    }
  }

  test("test_lookupLocale_jp_JP") {
    val test: Chronology = Chronology.ofLocale(new Locale("ja", "JP"))
    assertEquals(test.getId, "ISO")
    assertEquals(test, IsoChronology.INSTANCE)
  }

  // ignore("test_lookupLocale_jp_JP_JP") {
  //   // TODO This requires a fix on the locales side with the locale.getUnicodeLocaleType("ca") call
  //   val test: Chronology = Chronology.ofLocale(new Locale("ja", "JP", "JP"))
  //   assertEquals(test.getId, "Japanese")
  //   assertEquals(test, JapaneseChronology.INSTANCE)
  // }

}
