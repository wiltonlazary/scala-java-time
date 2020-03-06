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

import java.util.Locale

import org.scalatest.funsuite.AnyFunSuite
import org.threeten.bp.AssertionsHelper

/** Test DecimalStyle. */
class TestDecimalStyle extends AnyFunSuite with AssertionsHelper {
  test("getAvailableLocales") {
    val locales: java.util.Set[Locale] = DecimalStyle.getAvailableLocales
    assertEquals(locales.size > 0, true)
    assertEquals(locales.contains(Locale.US), true)
  }

  test("of_Locale") {
    val loc1: DecimalStyle = DecimalStyle.of(Locale.CANADA)
    assertEquals(loc1.getZeroDigit, '0')
    assertEquals(loc1.getPositiveSign, '+')
    assertEquals(loc1.getNegativeSign, '-')
    assertEquals(loc1.getDecimalSeparator, '.')
  }

  test("STANDARD") {
    val loc1: DecimalStyle = DecimalStyle.STANDARD
    assertEquals(loc1.getZeroDigit, '0')
    assertEquals(loc1.getPositiveSign, '+')
    assertEquals(loc1.getNegativeSign, '-')
    assertEquals(loc1.getDecimalSeparator, '.')
  }

  test("zeroDigit") {
    val base: DecimalStyle = DecimalStyle.STANDARD
    assertEquals(base.withZeroDigit('A').getZeroDigit, 'A')
  }

  test("positiveSign") {
    val base: DecimalStyle = DecimalStyle.STANDARD
    assertEquals(base.withPositiveSign('A').getPositiveSign, 'A')
  }

  test("negativeSign") {
    val base: DecimalStyle = DecimalStyle.STANDARD
    assertEquals(base.withNegativeSign('A').getNegativeSign, 'A')
  }

  test("decimalSeparator") {
    val base: DecimalStyle = DecimalStyle.STANDARD
    assertEquals(base.withDecimalSeparator('A').getDecimalSeparator, 'A')
  }

  test("convertToDigit_base") {
    val base: DecimalStyle = DecimalStyle.STANDARD
    assertEquals(base.convertToDigit('0'), 0)
    assertEquals(base.convertToDigit('1'), 1)
    assertEquals(base.convertToDigit('9'), 9)
    assertEquals(base.convertToDigit(' '), -1)
    assertEquals(base.convertToDigit('A'), -1)
  }

  test("convertToDigit_altered") {
    val base: DecimalStyle = DecimalStyle.STANDARD.withZeroDigit('A')
    assertEquals(base.convertToDigit('A'), 0)
    assertEquals(base.convertToDigit('B'), 1)
    assertEquals(base.convertToDigit('J'), 9)
    assertEquals(base.convertToDigit(' '), -1)
    assertEquals(base.convertToDigit('0'), -1)
  }

  test("convertNumberToI18N_base") {
    val base: DecimalStyle = DecimalStyle.STANDARD
    assertEquals(base.convertNumberToI18N("134"), "134")
  }

  test("convertNumberToI18N_altered") {
    val base: DecimalStyle = DecimalStyle.STANDARD.withZeroDigit('A')
    assertEquals(base.convertNumberToI18N("134"), "BDE")
  }

  test("equalsHashCode1") {
    val a: DecimalStyle = DecimalStyle.STANDARD
    val b: DecimalStyle = DecimalStyle.STANDARD
    assertEquals(a == b, true)
    assertEquals(b == a, true)
    assertEquals(a.hashCode, b.hashCode)
  }

  test("equalsHashCode2") {
    val a: DecimalStyle = DecimalStyle.STANDARD.withZeroDigit('A')
    val b: DecimalStyle = DecimalStyle.STANDARD.withZeroDigit('A')
    assertEquals(a == b, true)
    assertEquals(b == a, true)
    assertEquals(a.hashCode, b.hashCode)
  }

  test("equalsHashCode3") {
    val a: DecimalStyle = DecimalStyle.STANDARD.withZeroDigit('A')
    val b: DecimalStyle = DecimalStyle.STANDARD.withDecimalSeparator('A')
    assertEquals(a == b, false)
    assertEquals(b == a, false)
  }

  test("equalsHashCode_bad") {
    val a: DecimalStyle = DecimalStyle.STANDARD
    assertNotEquals(a, "")
    assertEquals(a == null, false)
  }

  test("toString_base") {
    val base: DecimalStyle = DecimalStyle.STANDARD
    assertEquals(base.toString, "DecimalStyle[0+-.]")
  }

  test("toString_altered") {
    val base: DecimalStyle = DecimalStyle.of(Locale.US).withZeroDigit('A').withDecimalSeparator('@')
    assertEquals(base.toString, "DecimalStyle[A+-@]")
  }
}
