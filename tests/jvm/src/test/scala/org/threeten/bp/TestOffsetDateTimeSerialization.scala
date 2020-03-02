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
package org.threeten.bp

import java.lang.reflect.{ Constructor, InvocationTargetException }

import org.scalatest.FunSuite

class TestOffsetDateTimeSerialization extends FunSuite with AssertionsHelper with AbstractTest {
  test("test_serialization") {
    assertSerializable(
      OffsetDateTime.of(LocalDate.of(2008, 6, 30),
                        LocalTime.of(11, 30, 59, 500),
                        TestOffsetDateTime.OFFSET_PONE)
    )
    assertSerializable(OffsetDateTime.MIN)
    assertSerializable(OffsetDateTime.MAX)
  }

  test("test_serialization_format") {
    val date: LocalDate    = LocalDate.of(2012, 9, 16)
    val time: LocalTime    = LocalTime.of(22, 17, 59, 464 * 1000000)
    val offset: ZoneOffset = ZoneOffset.of("+01:00")
    assertEqualsSerialisedForm(OffsetDateTime.of(date, time, offset))
  }

  test("constructor_nullTime") {
    assertThrows[NullPointerException] {
      val con: Constructor[OffsetDateTime] =
        classOf[OffsetDateTime].getDeclaredConstructor(classOf[LocalDateTime], classOf[ZoneOffset])
      con.setAccessible(true)
      try con.newInstance(null, TestOffsetDateTime.OFFSET_PONE)
      catch {
        case ex: InvocationTargetException =>
          throw ex.getCause
      }
    }
  }

  test("constructor_nullOffset") {
    assertThrows[NullPointerException] {
      val con: Constructor[OffsetDateTime] =
        classOf[OffsetDateTime].getDeclaredConstructor(classOf[LocalDateTime], classOf[ZoneOffset])
      con.setAccessible(true)
      try con.newInstance(LocalDateTime.of(LocalDate.of(2008, 6, 30), LocalTime.of(11, 30)), null)
      catch {
        case ex: InvocationTargetException =>
          throw ex.getCause
      }
    }
  }

}
