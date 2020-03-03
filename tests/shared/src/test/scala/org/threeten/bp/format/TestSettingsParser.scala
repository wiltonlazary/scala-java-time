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

import java.lang.StringBuilder

import org.scalatest.funsuite.AnyFunSuite
import org.threeten.bp.AssertionsHelper
import org.threeten.bp.format.internal.TTBPDateTimeFormatterBuilder
import org.threeten.bp.format.internal.TTBPDateTimeFormatterBuilder.SettingsParser

/** Test SettingsParser. */
class TestSettingsParser extends AnyFunSuite with GenTestPrinterParser with AssertionsHelper {
  test("test_print_sensitive") {
    val pp: TTBPDateTimeFormatterBuilder.SettingsParser = SettingsParser.SENSITIVE
    val buf: StringBuilder                              = new StringBuilder
    pp.print(printContext, buf)
    assertEquals(buf.toString, "")
  }

  test("test_print_strict") {
    val pp: TTBPDateTimeFormatterBuilder.SettingsParser = SettingsParser.STRICT
    val buf: StringBuilder                              = new StringBuilder
    pp.print(printContext, buf)
    assertEquals(buf.toString, "")
  }

  test("test_print_nulls") {
    val pp: TTBPDateTimeFormatterBuilder.SettingsParser = SettingsParser.SENSITIVE
    pp.print(null, null)
  }

  test("test_parse_changeStyle_sensitive") {
    val pp: TTBPDateTimeFormatterBuilder.SettingsParser = SettingsParser.SENSITIVE
    val result: Int                                     = pp.parse(parseContext, "a", 0)
    assertEquals(result, 0)
    assertEquals(parseContext.isCaseSensitive, true)
  }

  test("test_parse_changeStyle_insensitive") {
    val pp: TTBPDateTimeFormatterBuilder.SettingsParser = SettingsParser.INSENSITIVE
    val result: Int                                     = pp.parse(parseContext, "a", 0)
    assertEquals(result, 0)
    assertEquals(parseContext.isCaseSensitive, false)
  }

  test("test_parse_changeStyle_strict") {
    val pp: TTBPDateTimeFormatterBuilder.SettingsParser = SettingsParser.STRICT
    val result: Int                                     = pp.parse(parseContext, "a", 0)
    assertEquals(result, 0)
    assertEquals(parseContext.isStrict, true)
  }

  test("test_parse_changeStyle_lenient") {
    val pp: TTBPDateTimeFormatterBuilder.SettingsParser = SettingsParser.LENIENT
    val result: Int                                     = pp.parse(parseContext, "a", 0)
    assertEquals(result, 0)
    assertEquals(parseContext.isStrict, false)
  }

  test("test_toString_sensitive") {
    val pp: TTBPDateTimeFormatterBuilder.SettingsParser = SettingsParser.SENSITIVE
    assertEquals(pp.toString, "ParseCaseSensitive(true)")
  }

  test("test_toString_insensitive") {
    val pp: TTBPDateTimeFormatterBuilder.SettingsParser = SettingsParser.INSENSITIVE
    assertEquals(pp.toString, "ParseCaseSensitive(false)")
  }

  test("test_toString_strict") {
    val pp: TTBPDateTimeFormatterBuilder.SettingsParser = SettingsParser.STRICT
    assertEquals(pp.toString, "ParseStrict(true)")
  }

  test("test_toString_lenient") {
    val pp: TTBPDateTimeFormatterBuilder.SettingsParser = SettingsParser.LENIENT
    assertEquals(pp.toString, "ParseStrict(false)")
  }
}
