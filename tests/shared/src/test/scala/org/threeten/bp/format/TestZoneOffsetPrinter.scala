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
package org.threeten.bp.format

import org.scalatest.funsuite.AnyFunSuite
import org.threeten.bp.AssertionsHelper
import org.threeten.bp.temporal.ChronoField.OFFSET_SECONDS
import org.threeten.bp.DateTimeException
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.internal.TTBPDateTimeFormatterBuilder

/** Test ZoneOffsetPrinterParser. */
object TestZoneOffsetPrinter {
  private val OFFSET_0130: ZoneOffset = ZoneOffset.of("+01:30")
}

class TestZoneOffsetPrinter extends AnyFunSuite with GenTestPrinterParser with AssertionsHelper {
  val provider_offsets: List[List[AnyRef]] =
    List(
      List("+HH", "NO-OFFSET", ZoneOffset.UTC),
      List("+HH", "+01", ZoneOffset.ofHours(1)),
      List("+HH", "-01", ZoneOffset.ofHours(-1)),
      List("+HHMM", "NO-OFFSET", ZoneOffset.UTC),
      List("+HHMM", "+0102", ZoneOffset.ofHoursMinutes(1, 2)),
      List("+HHMM", "-0102", ZoneOffset.ofHoursMinutes(-1, -2)),
      List("+HH:MM", "NO-OFFSET", ZoneOffset.UTC),
      List("+HH:MM", "+01:02", ZoneOffset.ofHoursMinutes(1, 2)),
      List("+HH:MM", "-01:02", ZoneOffset.ofHoursMinutes(-1, -2)),
      List("+HHMMss", "NO-OFFSET", ZoneOffset.UTC),
      List("+HHMMss", "+0100", ZoneOffset.ofHoursMinutesSeconds(1, 0, 0)),
      List("+HHMMss", "+0102", ZoneOffset.ofHoursMinutesSeconds(1, 2, 0)),
      List("+HHMMss", "+0159", ZoneOffset.ofHoursMinutesSeconds(1, 59, 0)),
      List("+HHMMss", "+0200", ZoneOffset.ofHoursMinutesSeconds(2, 0, 0)),
      List("+HHMMss", "+1800", ZoneOffset.ofHoursMinutesSeconds(18, 0, 0)),
      List("+HHMMss", "+010215", ZoneOffset.ofHoursMinutesSeconds(1, 2, 15)),
      List("+HHMMss", "-0100", ZoneOffset.ofHoursMinutesSeconds(-1, 0, 0)),
      List("+HHMMss", "-0200", ZoneOffset.ofHoursMinutesSeconds(-2, 0, 0)),
      List("+HHMMss", "-1800", ZoneOffset.ofHoursMinutesSeconds(-18, 0, 0)),
      List("+HHMMss", "NO-OFFSET", ZoneOffset.UTC),
      List("+HHMMss", "+0100", ZoneOffset.ofHoursMinutesSeconds(1, 0, 0)),
      List("+HHMMss", "+010203", ZoneOffset.ofHoursMinutesSeconds(1, 2, 3)),
      List("+HHMMss", "+015959", ZoneOffset.ofHoursMinutesSeconds(1, 59, 59)),
      List("+HHMMss", "+0200", ZoneOffset.ofHoursMinutesSeconds(2, 0, 0)),
      List("+HHMMss", "+1800", ZoneOffset.ofHoursMinutesSeconds(18, 0, 0)),
      List("+HHMMss", "-0100", ZoneOffset.ofHoursMinutesSeconds(-1, 0, 0)),
      List("+HHMMss", "-0200", ZoneOffset.ofHoursMinutesSeconds(-2, 0, 0)),
      List("+HHMMss", "-1800", ZoneOffset.ofHoursMinutesSeconds(-18, 0, 0)),
      List("+HH:MM:ss", "NO-OFFSET", ZoneOffset.UTC),
      List("+HH:MM:ss", "+01:00", ZoneOffset.ofHoursMinutesSeconds(1, 0, 0)),
      List("+HH:MM:ss", "+01:02", ZoneOffset.ofHoursMinutesSeconds(1, 2, 0)),
      List("+HH:MM:ss", "+01:59", ZoneOffset.ofHoursMinutesSeconds(1, 59, 0)),
      List("+HH:MM:ss", "+02:00", ZoneOffset.ofHoursMinutesSeconds(2, 0, 0)),
      List("+HH:MM:ss", "+18:00", ZoneOffset.ofHoursMinutesSeconds(18, 0, 0)),
      List("+HH:MM:ss", "+01:02:15", ZoneOffset.ofHoursMinutesSeconds(1, 2, 15)),
      List("+HH:MM:ss", "-01:00", ZoneOffset.ofHoursMinutesSeconds(-1, 0, 0)),
      List("+HH:MM:ss", "-02:00", ZoneOffset.ofHoursMinutesSeconds(-2, 0, 0)),
      List("+HH:MM:ss", "-18:00", ZoneOffset.ofHoursMinutesSeconds(-18, 0, 0)),
      List("+HH:MM:ss", "NO-OFFSET", ZoneOffset.UTC),
      List("+HH:MM:ss", "+01:00", ZoneOffset.ofHoursMinutesSeconds(1, 0, 0)),
      List("+HH:MM:ss", "+01:02:03", ZoneOffset.ofHoursMinutesSeconds(1, 2, 3)),
      List("+HH:MM:ss", "+01:59:59", ZoneOffset.ofHoursMinutesSeconds(1, 59, 59)),
      List("+HH:MM:ss", "+02:00", ZoneOffset.ofHoursMinutesSeconds(2, 0, 0)),
      List("+HH:MM:ss", "+18:00", ZoneOffset.ofHoursMinutesSeconds(18, 0, 0)),
      List("+HH:MM:ss", "-01:00", ZoneOffset.ofHoursMinutesSeconds(-1, 0, 0)),
      List("+HH:MM:ss", "-02:00", ZoneOffset.ofHoursMinutesSeconds(-2, 0, 0)),
      List("+HH:MM:ss", "-18:00", ZoneOffset.ofHoursMinutesSeconds(-18, 0, 0)),
      List("+HHMMSS", "NO-OFFSET", ZoneOffset.UTC),
      List("+HHMMSS", "+010203", ZoneOffset.ofHoursMinutesSeconds(1, 2, 3)),
      List("+HHMMSS", "-010203", ZoneOffset.ofHoursMinutesSeconds(-1, -2, -3)),
      List("+HHMMSS", "+010200", ZoneOffset.ofHoursMinutesSeconds(1, 2, 0)),
      List("+HHMMSS", "-010200", ZoneOffset.ofHoursMinutesSeconds(-1, -2, 0)),
      List("+HH:MM:SS", "NO-OFFSET", ZoneOffset.UTC),
      List("+HH:MM:SS", "+01:02:03", ZoneOffset.ofHoursMinutesSeconds(1, 2, 3)),
      List("+HH:MM:SS", "-01:02:03", ZoneOffset.ofHoursMinutesSeconds(-1, -2, -3)),
      List("+HH:MM:SS", "+01:02:00", ZoneOffset.ofHoursMinutesSeconds(1, 2, 0)),
      List("+HH:MM:SS", "-01:02:00", ZoneOffset.ofHoursMinutesSeconds(-1, -2, 0))
    )

  test("test_print") {
    provider_offsets.foreach {
      case (pattern: String) :: (expected: String) :: (offset: ZoneOffset) :: Nil =>
        super.beforeEach()
        buf.append("EXISTING")
        printContext.setDateTime(new DateTimeBuilder(OFFSET_SECONDS, offset.getTotalSeconds))
        val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
          new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("NO-OFFSET", pattern)
        pp.print(printContext, buf)
        assertEquals(buf.toString, "EXISTING" + expected)
      case _                                                                      =>
        fail()
    }
  }

  test("test_toString") {
    provider_offsets.foreach {
      case (pattern: String) :: (_: String) :: (_: ZoneOffset) :: Nil =>
        val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
          new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("NO-OFFSET", pattern)
        assertEquals(pp.toString, "Offset(" + pattern + ",'NO-OFFSET')")
      case _                                                          =>
        fail()
    }
  }

  test("test_print_emptyCalendrical") {
    val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
      new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("Z", "+HH:MM:ss")
    assertThrows[DateTimeException] {
      pp.print(printEmptyContext, buf)
    }
  }

  test("test_print_emptyAppendable") {
    printContext.setDateTime(
      new DateTimeBuilder(OFFSET_SECONDS, TestZoneOffsetPrinter.OFFSET_0130.getTotalSeconds)
    )
    val pp: TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser =
      new TTBPDateTimeFormatterBuilder.OffsetIdPrinterParser("Z", "+HH:MM:ss")
    pp.print(printContext, buf)
    assertEquals(buf.toString, "+01:30")
  }
}
