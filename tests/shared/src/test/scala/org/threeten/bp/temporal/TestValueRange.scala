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
package org.threeten.bp.temporal

import org.scalatest.funsuite.AnyFunSuite
import org.threeten.bp.AssertionsHelper

/** Test. */
class TestValueRange extends AnyFunSuite with AssertionsHelper {

  test("of_longlong") {
    val test: ValueRange = ValueRange.of(1, 12)
    assertEquals(test.getMinimum, 1)
    assertEquals(test.getLargestMinimum, 1)
    assertEquals(test.getSmallestMaximum, 12)
    assertEquals(test.getMaximum, 12)
    assertEquals(test.isFixed, true)
    assertEquals(test.isIntValue, true)
  }

  test("of_longlong_big") {
    val test: ValueRange = ValueRange.of(1, 123456789012345L)
    assertEquals(test.getMinimum, 1)
    assertEquals(test.getLargestMinimum, 1)
    assertEquals(test.getSmallestMaximum, 123456789012345L)
    assertEquals(test.getMaximum, 123456789012345L)
    assertEquals(test.isFixed, true)
    assertEquals(test.isIntValue, false)
  }

  test("of_longlong_minGtMax") {
    assertThrows[IllegalArgumentException] {
      ValueRange.of(12, 1)
    }
  }

  test("of_longlonglong") {
    val test: ValueRange = ValueRange.of(1, 28, 31)
    assertEquals(test.getMinimum, 1)
    assertEquals(test.getLargestMinimum, 1)
    assertEquals(test.getSmallestMaximum, 28)
    assertEquals(test.getMaximum, 31)
    assertEquals(test.isFixed, false)
    assertEquals(test.isIntValue, true)
  }

  test("of_longlonglong_minGtMax") {
    assertThrows[IllegalArgumentException] {
      ValueRange.of(12, 1, 2)
    }
  }

  test("of_longlonglong_smallestmaxminGtMax") {
    assertThrows[IllegalArgumentException] {
      ValueRange.of(1, 31, 28)
    }
  }

  def data_valid: List[List[Int]] =
    List(List(1, 1, 1, 1),
         List(1, 1, 1, 2),
         List(1, 1, 2, 2),
         List(1, 2, 3, 4),
         List(1, 1, 28, 31),
         List(1, 3, 31, 31),
         List(-5, -4, -3, -2),
         List(-5, -4, 3, 4),
         List(1, 20, 10, 31)
    )

  test("of_longlonglonglong") {
    data_valid.foreach {
      case sMin :: lMin :: sMax :: lMax :: Nil =>
        val test: ValueRange = ValueRange.of(sMin, lMin, sMax, lMax)
        assertEquals(test.getMinimum, sMin)
        assertEquals(test.getLargestMinimum, lMin)
        assertEquals(test.getSmallestMaximum, sMax)
        assertEquals(test.getMaximum, lMax)
        assertEquals(test.isFixed, sMin == lMin && sMax == lMax)
        assertEquals(test.isIntValue, true)
      case _                                   =>
        fail()
    }
  }

  def data_invalid: List[List[Int]] =
    List(List(1, 2, 31, 28),
         List(1, 31, 2, 28),
         List(31, 2, 1, 28),
         List(31, 2, 3, 28),
         List(2, 1, 28, 31),
         List(2, 1, 31, 28),
         List(12, 13, 1, 2)
    )

  test("of_longlonglonglong_invalid") {
    data_invalid.foreach {
      case sMin :: lMin :: sMax :: lMax :: Nil =>
        assertThrows[IllegalArgumentException] {
          ValueRange.of(sMin, lMin, sMax, lMax)
        }
      case _                                   =>
        fail()
    }
  }

  test("isValidValue_long") {
    val test: ValueRange = ValueRange.of(1, 28, 31)
    assertEquals(test.isValidValue(0), false)
    assertEquals(test.isValidValue(1), true)
    assertEquals(test.isValidValue(2), true)
    assertEquals(test.isValidValue(30), true)
    assertEquals(test.isValidValue(31), true)
    assertEquals(test.isValidValue(32), false)
  }

  test("isValidValue_long_int") {
    val test: ValueRange = ValueRange.of(1, 28, 31)
    assertEquals(test.isValidValue(0), false)
    assertEquals(test.isValidValue(1), true)
    assertEquals(test.isValidValue(31), true)
    assertEquals(test.isValidValue(32), false)
  }

  test("isValidValue_long_long") {
    val test: ValueRange = ValueRange.of(1, 28, Int.MaxValue + 1L)
    assertEquals(test.isValidIntValue(0), false)
    assertEquals(test.isValidIntValue(1), false)
    assertEquals(test.isValidIntValue(31), false)
    assertEquals(test.isValidIntValue(32), false)
  }

  test("equals1") {
    val a: ValueRange = ValueRange.of(1, 2, 3, 4)
    val b: ValueRange = ValueRange.of(1, 2, 3, 4)
    assertEquals(a == a, true)
    assertEquals(a == b, true)
    assertEquals(b == a, true)
    assertEquals(b == b, true)
    assertEquals(a.hashCode == b.hashCode, true)
  }

  test("equals2") {
    val a: ValueRange = ValueRange.of(1, 2, 3, 4)
    assertEquals(a == ValueRange.of(0, 2, 3, 4), false)
    assertEquals(a == ValueRange.of(1, 3, 3, 4), false)
    assertEquals(a == ValueRange.of(1, 2, 4, 4), false)
    assertEquals(a == ValueRange.of(1, 2, 3, 5), false)
  }

  test("equals_otherType") {
    val a: ValueRange = ValueRange.of(1, 12)
    assertNotEquals(a, "Rubbish")
  }

  test("equals_null") {
    val a: ValueRange = ValueRange.of(1, 12)
    assertEquals(a == null, false)
  }

  test("toString") {
    assertEquals(ValueRange.of(1, 1, 4, 4).toString, "1 - 4")
    assertEquals(ValueRange.of(1, 1, 3, 4).toString, "1 - 3/4")
    assertEquals(ValueRange.of(1, 2, 3, 4).toString, "1/2 - 3/4")
    assertEquals(ValueRange.of(1, 2, 4, 4).toString, "1/2 - 4")
  }
}
