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

class TestStandardZoneRulesNegative extends AnyFunSuite with AssertionsHelper {
  private def createZDT(year: Int, month: Int, day: Int, zone: ZoneId): ZonedDateTime =
    LocalDateTime.of(year, month, day, 0, 0).atZone(zone)

  private def europeDublin: ZoneRules =
    ZoneId.of("Europe/Dublin").getRules

  test("Dublin_getStandardOffset") {
    val test: ZoneRules    = europeDublin
    var zdt: ZonedDateTime = createZDT(1840, 1, 1, ZoneOffset.UTC)
    while (zdt.getYear < 2010) {
      val instant: Instant = zdt.toInstant
      if (zdt.getYear < 1881) {
        assertEquals(test.getStandardOffset(instant), ZoneOffset.ofHoursMinutes(0, -25))
      } else if (zdt.getYear >= 1881 && zdt.getYear < 1917) {
        assertEquals(test.getStandardOffset(instant), ZoneOffset.ofHoursMinutesSeconds(0, -25, -21))
      } else if (zdt.getYear >= 1917 && zdt.getYear < 1969) {
        assertEquals(test.getStandardOffset(instant),
                     TestStandardZoneRules.OFFSET_ZERO,
                     zdt.toString())
      } else {
        assertEquals(test.getStandardOffset(instant),
                     TestStandardZoneRules.OFFSET_PONE) // negative DST
      }
      zdt = zdt.plusMonths(6)
    }
  }

  test("Dublin_dst") {
    val test = europeDublin
    assertEquals(test.isDaylightSavings(createZDT(1960, 1, 1, ZoneOffset.UTC).toInstant), false)
    assertEquals(test.getDaylightSavings(createZDT(1960, 1, 1, ZoneOffset.UTC).toInstant),
                 Duration.ofHours(0))
    assertEquals(test.isDaylightSavings(createZDT(1960, 7, 1, ZoneOffset.UTC).toInstant), true)
    assertEquals(test.getDaylightSavings(createZDT(1960, 7, 1, ZoneOffset.UTC).toInstant),
                 Duration.ofHours(1))
    // negative DST causes isDaylightSavings() to reverse
    assertEquals(test.isDaylightSavings(createZDT(2016, 1, 1, ZoneOffset.UTC).toInstant), true)
    assertEquals(test.getDaylightSavings(createZDT(2016, 1, 1, ZoneOffset.UTC).toInstant),
                 Duration.ofHours(-1))
    assertEquals(test.isDaylightSavings(createZDT(2016, 7, 1, ZoneOffset.UTC).toInstant), false)
    assertEquals(test.getDaylightSavings(createZDT(2016, 7, 1, ZoneOffset.UTC).toInstant),
                 Duration.ofHours(0))

    // TZDB data is messed up, comment out tests until better fix available
    // val formatter1 = new DateTimeFormatterBuilder().appendZoneText(TextStyle.FULL).toFormatter()
    // assertEquals(formatter1.format(createZDT(2016, 1, 1, ZoneId.of("Europe/Dublin"))), "Greenwich Mean Time")
    // assertEquals(formatter1.format(createZDT(2016, 7, 1, ZoneId.of("Europe/Dublin"))), "Irish Standard Time")
    //
    // val formatter2 = new DateTimeFormatterBuilder().appendZoneText(TextStyle.SHORT).toFormatter()
    // assertEquals(formatter2.format(createZDT(2016, 1, 1, ZoneId.of("Europe/Dublin"))), "GMT")
    // assertEquals(formatter2.format(createZDT(2016, 7, 1, ZoneId.of("Europe/Dublin"))), "IST")
  }
}
