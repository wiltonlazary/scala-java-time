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

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import org.threeten.bp.AssertionsHelper

import org.scalatest.{ BeforeAndAfterEach, FunSuite }

/** Test Chrono class. */
class TestChronologySerialization extends FunSuite with BeforeAndAfterEach with AssertionsHelper {
  val data_CalendarType: List[(Chronology, String)] = {
    List(
      (HijrahChronology.INSTANCE, "islamic-umalqura"),
      (IsoChronology.INSTANCE, "iso8601"),
      (JapaneseChronology.INSTANCE, "japanese"),
      (MinguoChronology.INSTANCE, "roc"),
      (ThaiBuddhistChronology.INSTANCE, "buddhist")
    )
  }

  test("test_chronoSerializationSingleton") {
    data_CalendarType.foreach {
      case (chrono, _) =>
        val orginal: Chronology         = chrono
        val baos: ByteArrayOutputStream = new ByteArrayOutputStream
        val out: ObjectOutputStream     = new ObjectOutputStream(baos)
        out.writeObject(orginal)
        out.close()
        val bais: ByteArrayInputStream = new ByteArrayInputStream(baos.toByteArray)
        val in: ObjectInputStream      = new ObjectInputStream(bais)
        val ser: Chronology            = in.readObject.asInstanceOf[Chronology]
        assertSame(ser, chrono)
    }
  }
}
