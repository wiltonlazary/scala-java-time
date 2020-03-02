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

import java.lang.reflect.{ Field, Modifier }

import org.scalatest.FunSuite

/** Test ZoneId. */
class TestZoneIdSerialization extends FunSuite with AssertionsHelper with AbstractTest {
  test("immutable") {
    val cls: Class[ZoneId] = classOf[ZoneId]
    assertTrue(Modifier.isPublic(cls.getModifiers))
    val fields: Array[Field] = cls.getDeclaredFields
    for (field <- fields) {
      if (!Modifier.isStatic(field.getModifiers)) {
        assertTrue(Modifier.isPrivate(field.getModifiers))
        assertTrue(
          Modifier.isFinal(field.getModifiers) || (Modifier
            .isVolatile(field.getModifiers) && Modifier.isTransient(field.getModifiers))
        )
      }
    }
  }

  test("serialization_UTC") {
    val test: ZoneId = ZoneOffset.UTC
    assertSerializableAndSame(test)
  }

  test("serialization_fixed") {
    val test: ZoneId = ZoneId.of("UTC+01:30")
    assertSerializable(test)
  }

  test("serialization_Europe") {
    val test: ZoneId = ZoneId.of("Europe/London")
    assertSerializable(test)
  }

  test("serialization_America") {
    val test: ZoneId = ZoneId.of("America/Chicago")
    assertSerializable(test)
  }

  test("serialization_format") {
    assertEqualsSerialisedForm(ZoneId.of("Europe/London"), classOf[ZoneId])
  }

}
