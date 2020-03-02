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

import java.io.{
  ByteArrayInputStream,
  ByteArrayOutputStream,
  ObjectInputStream,
  ObjectOutputStream
}

import org.scalatest.FunSuite
import org.threeten.bp._

/** Test ZoneRules Serialization for fixed offset time-zones. */
class TestFixedZoneRulesSerialization extends FunSuite with AssertionsHelper with AbstractTest {
  private def make(offset: ZoneOffset): ZoneRules =
    offset.getRules

  private[zone] def data_rules: List[(ZoneRules, ZoneOffset)] =
    List(
      (make(TestFixedZoneRules.OFFSET_PONE), TestFixedZoneRules.OFFSET_PONE),
      (make(TestFixedZoneRules.OFFSET_PTWO), TestFixedZoneRules.OFFSET_PTWO),
      (make(TestFixedZoneRules.OFFSET_M18), TestFixedZoneRules.OFFSET_M18)
    )

  test("serialization(test: ZoneRules, expectedOffset: ZoneOffset)") {
    data_rules.foreach {
      case (test: ZoneRules, _: ZoneOffset) =>
        val baos: ByteArrayOutputStream = new ByteArrayOutputStream
        val out: ObjectOutputStream     = new ObjectOutputStream(baos)
        out.writeObject(test)
        baos.close()
        val bytes: Array[Byte]         = baos.toByteArray
        val bais: ByteArrayInputStream = new ByteArrayInputStream(bytes)
        val in: ObjectInputStream      = new ObjectInputStream(bais)
        val result: ZoneRules          = in.readObject.asInstanceOf[ZoneRules]
        assertEquals(result, test)
        assertEquals(result.getClass, test.getClass)
    }
  }

}
