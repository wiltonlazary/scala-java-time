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

import java.io._

import org.scalatest.funsuite.AnyFunSuite

/** Test. */
object TestPeriod {
  def pymd(y: Int, m: Int, d: Int): Period =
    Period.of(y, m, d)

  def date(y: Int, m: Int, d: Int): LocalDate =
    LocalDate.of(y, m, d)
}

class TestPeriod extends AnyFunSuite with AssertionsHelper {
  test("interfaces") {
    assertTrue(classOf[Serializable].isAssignableFrom(classOf[Period]))
  }

  test("factory_zeroSingleton") {
    assertSame(Period.ZERO, Period.ZERO)
    assertSame(Period.of(0, 0, 0), Period.ZERO)
    assertSame(Period.ofYears(0), Period.ZERO)
    assertSame(Period.ofMonths(0), Period.ZERO)
    assertSame(Period.ofDays(0), Period.ZERO)
  }

  test("factory_of_ints") {
    assertPeriod(Period.of(1, 2, 3), 1, 2, 3)
    assertPeriod(Period.of(0, 2, 3), 0, 2, 3)
    assertPeriod(Period.of(1, 0, 0), 1, 0, 0)
    assertPeriod(Period.of(0, 0, 0), 0, 0, 0)
    assertPeriod(Period.of(-1, -2, -3), -1, -2, -3)
  }

  test("factory_ofYears") {
    assertPeriod(Period.ofYears(1), 1, 0, 0)
    assertPeriod(Period.ofYears(0), 0, 0, 0)
    assertPeriod(Period.ofYears(-1), -1, 0, 0)
    assertPeriod(Period.ofYears(Int.MaxValue), Int.MaxValue, 0, 0)
    assertPeriod(Period.ofYears(Int.MinValue), Int.MinValue, 0, 0)
  }

  test("factory_ofMonths") {
    assertPeriod(Period.ofMonths(1), 0, 1, 0)
    assertPeriod(Period.ofMonths(0), 0, 0, 0)
    assertPeriod(Period.ofMonths(-1), 0, -1, 0)
    assertPeriod(Period.ofMonths(Int.MaxValue), 0, Int.MaxValue, 0)
    assertPeriod(Period.ofMonths(Int.MinValue), 0, Int.MinValue, 0)
  }

  test("factory_ofDays") {
    assertPeriod(Period.ofDays(1), 0, 0, 1)
    assertPeriod(Period.ofDays(0), 0, 0, 0)
    assertPeriod(Period.ofDays(-1), 0, 0, -1)
    assertPeriod(Period.ofDays(Int.MaxValue), 0, 0, Int.MaxValue)
    assertPeriod(Period.ofDays(Int.MinValue), 0, 0, Int.MinValue)
  }

  def data_between: List[List[Int]] =
    List(
      List(2010, 1, 1, 2010, 1, 1, 0, 0, 0),
      List(2010, 1, 1, 2010, 1, 2, 0, 0, 1),
      List(2010, 1, 1, 2010, 1, 31, 0, 0, 30),
      List(2010, 1, 1, 2010, 2, 1, 0, 1, 0),
      List(2010, 1, 1, 2010, 2, 28, 0, 1, 27),
      List(2010, 1, 1, 2010, 3, 1, 0, 2, 0),
      List(2010, 1, 1, 2010, 12, 31, 0, 11, 30),
      List(2010, 1, 1, 2011, 1, 1, 1, 0, 0),
      List(2010, 1, 1, 2011, 12, 31, 1, 11, 30),
      List(2010, 1, 1, 2012, 1, 1, 2, 0, 0),
      List(2010, 1, 10, 2010, 1, 1, 0, 0, -9),
      List(2010, 1, 10, 2010, 1, 2, 0, 0, -8),
      List(2010, 1, 10, 2010, 1, 9, 0, 0, -1),
      List(2010, 1, 10, 2010, 1, 10, 0, 0, 0),
      List(2010, 1, 10, 2010, 1, 11, 0, 0, 1),
      List(2010, 1, 10, 2010, 1, 31, 0, 0, 21),
      List(2010, 1, 10, 2010, 2, 1, 0, 0, 22),
      List(2010, 1, 10, 2010, 2, 9, 0, 0, 30),
      List(2010, 1, 10, 2010, 2, 10, 0, 1, 0),
      List(2010, 1, 10, 2010, 2, 28, 0, 1, 18),
      List(2010, 1, 10, 2010, 3, 1, 0, 1, 19),
      List(2010, 1, 10, 2010, 3, 9, 0, 1, 27),
      List(2010, 1, 10, 2010, 3, 10, 0, 2, 0),
      List(2010, 1, 10, 2010, 12, 31, 0, 11, 21),
      List(2010, 1, 10, 2011, 1, 1, 0, 11, 22),
      List(2010, 1, 10, 2011, 1, 9, 0, 11, 30),
      List(2010, 1, 10, 2011, 1, 10, 1, 0, 0),
      List(2010, 3, 30, 2011, 5, 1, 1, 1, 1),
      List(2010, 4, 30, 2011, 5, 1, 1, 0, 1),
      List(2010, 2, 28, 2012, 2, 27, 1, 11, 30),
      List(2010, 2, 28, 2012, 2, 28, 2, 0, 0),
      List(2010, 2, 28, 2012, 2, 29, 2, 0, 1),
      List(2012, 2, 28, 2014, 2, 27, 1, 11, 30),
      List(2012, 2, 28, 2014, 2, 28, 2, 0, 0),
      List(2012, 2, 28, 2014, 3, 1, 2, 0, 1),
      List(2012, 2, 29, 2014, 2, 28, 1, 11, 30),
      List(2012, 2, 29, 2014, 3, 1, 2, 0, 1),
      List(2012, 2, 29, 2014, 3, 2, 2, 0, 2),
      List(2012, 2, 29, 2016, 2, 28, 3, 11, 30),
      List(2012, 2, 29, 2016, 2, 29, 4, 0, 0),
      List(2012, 2, 29, 2016, 3, 1, 4, 0, 1),
      List(2010, 1, 1, 2009, 12, 31, 0, 0, -1),
      List(2010, 1, 1, 2009, 12, 30, 0, 0, -2),
      List(2010, 1, 1, 2009, 12, 2, 0, 0, -30),
      List(2010, 1, 1, 2009, 12, 1, 0, -1, 0),
      List(2010, 1, 1, 2009, 11, 30, 0, -1, -1),
      List(2010, 1, 1, 2009, 11, 2, 0, -1, -29),
      List(2010, 1, 1, 2009, 11, 1, 0, -2, 0),
      List(2010, 1, 1, 2009, 1, 2, 0, -11, -30),
      List(2010, 1, 1, 2009, 1, 1, -1, 0, 0),
      List(2010, 1, 15, 2010, 1, 15, 0, 0, 0),
      List(2010, 1, 15, 2010, 1, 14, 0, 0, -1),
      List(2010, 1, 15, 2010, 1, 1, 0, 0, -14),
      List(2010, 1, 15, 2009, 12, 31, 0, 0, -15),
      List(2010, 1, 15, 2009, 12, 16, 0, 0, -30),
      List(2010, 1, 15, 2009, 12, 15, 0, -1, 0),
      List(2010, 1, 15, 2009, 12, 14, 0, -1, -1),
      List(2010, 2, 28, 2009, 3, 1, 0, -11, -27),
      List(2010, 2, 28, 2009, 2, 28, -1, 0, 0),
      List(2010, 2, 28, 2009, 2, 27, -1, 0, -1),
      List(2010, 2, 28, 2008, 2, 29, -1, -11, -28),
      List(2010, 2, 28, 2008, 2, 28, -2, 0, 0),
      List(2010, 2, 28, 2008, 2, 27, -2, 0, -1),
      List(2012, 2, 29, 2009, 3, 1, -2, -11, -28),
      List(2012, 2, 29, 2009, 2, 28, -3, 0, -1),
      List(2012, 2, 29, 2009, 2, 27, -3, 0, -2),
      List(2012, 2, 29, 2008, 3, 1, -3, -11, -28),
      List(2012, 2, 29, 2008, 2, 29, -4, 0, 0),
      List(2012, 2, 29, 2008, 2, 28, -4, 0, -1)
    )

  test("factory_between_LocalDate") {
    data_between.foreach {
      case y1 :: m1 :: d1 :: y2 :: m2 :: d2 :: ye :: me :: de :: Nil =>
        val start: LocalDate = LocalDate.of(y1, m1, d1)
        val end: LocalDate   = LocalDate.of(y2, m2, d2)
        val test: Period     = Period.between(start, end)
        assertPeriod(test, ye, me, de)
      case _                                                         =>
        fail()
    }
  }

  test("factory_between_LocalDate_nullFirst") {
    assertThrows[NullPointerException] {
      Period.between(null.asInstanceOf[LocalDate], LocalDate.of(2010, 1, 1))
    }
  }

  test("factory_between_LocalDate_nullSecond") {
    assertThrows[Platform.NPE] {
      Period.between(LocalDate.of(2010, 1, 1), null.asInstanceOf[LocalDate])
    }
  }

  def data_parse: List[List[AnyRef]] =
    List(
      List("P0D", Period.ZERO),
      List("P0W", Period.ZERO),
      List("P0M", Period.ZERO),
      List("P0Y", Period.ZERO),
      List("P0Y0D", Period.ZERO),
      List("P0Y0W", Period.ZERO),
      List("P0Y0M", Period.ZERO),
      List("P0M0D", Period.ZERO),
      List("P0M0W", Period.ZERO),
      List("P0W0D", Period.ZERO),
      List("P1D", Period.ofDays(1)),
      List("P2D", Period.ofDays(2)),
      List("P-2D", Period.ofDays(-2)),
      List("-P2D", Period.ofDays(-2)),
      List("-P-2D", Period.ofDays(2)),
      List("P" + Int.MaxValue + "D", Period.ofDays(Int.MaxValue)),
      List("P" + Int.MinValue + "D", Period.ofDays(Int.MinValue)),
      List("P1W", Period.ofDays(7)),
      List("P2W", Period.ofDays(14)),
      List("P-2W", Period.ofDays(-14)),
      List("-P2W", Period.ofDays(-14)),
      List("-P-2W", Period.ofDays(14)),
      List("P1M", Period.ofMonths(1)),
      List("P2M", Period.ofMonths(2)),
      List("P-2M", Period.ofMonths(-2)),
      List("-P2M", Period.ofMonths(-2)),
      List("-P-2M", Period.ofMonths(2)),
      List("P" + Int.MaxValue + "M", Period.ofMonths(Int.MaxValue)),
      List("P" + Int.MinValue + "M", Period.ofMonths(Int.MinValue)),
      List("P1Y", Period.ofYears(1)),
      List("P2Y", Period.ofYears(2)),
      List("P-2Y", Period.ofYears(-2)),
      List("-P2Y", Period.ofYears(-2)),
      List("-P-2Y", Period.ofYears(2)),
      List("P" + Int.MaxValue + "Y", Period.ofYears(Int.MaxValue)),
      List("P" + Int.MinValue + "Y", Period.ofYears(Int.MinValue)),
      List("P1Y2M3W4D", Period.of(1, 2, 3 * 7 + 4))
    )

  test("parse") {
    data_parse.foreach {
      case (text: String) :: (expected: Period) :: Nil =>
        assertEquals(Period.parse(text), expected)
      case _                                           =>
        fail()
    }
  }

  test("parse_toString") {
    data_toString.foreach {
      case (test: Period) :: (expected: String) :: Nil =>
        assertEquals(test, Period.parse(expected))
      case _                                           =>
        fail()
    }
  }

  test("parse_nullText") {
    assertThrows[NullPointerException] {
      Period.parse(null.asInstanceOf[String])
    }
  }

  test("isZero") {
    assertEquals(Period.of(1, 2, 3).isZero, false)
    assertEquals(Period.of(1, 0, 0).isZero, false)
    assertEquals(Period.of(0, 2, 0).isZero, false)
    assertEquals(Period.of(0, 0, 3).isZero, false)
    assertEquals(Period.of(0, 0, 0).isZero, true)
  }

  test("isNegative") {
    assertEquals(Period.of(0, 0, 0).isNegative, false)
    assertEquals(Period.of(1, 2, 3).isNegative, false)
    assertEquals(Period.of(1, 0, 0).isNegative, false)
    assertEquals(Period.of(0, 2, 0).isNegative, false)
    assertEquals(Period.of(0, 0, 3).isNegative, false)
    assertEquals(Period.of(-1, -2, -3).isNegative, true)
    assertEquals(Period.of(-1, 0, 0).isNegative, true)
    assertEquals(Period.of(0, -2, 0).isNegative, true)
    assertEquals(Period.of(0, 0, -3).isNegative, true)
    assertEquals(Period.of(-1, 2, 3).isNegative, true)
    assertEquals(Period.of(1, -2, 3).isNegative, true)
    assertEquals(Period.of(1, 2, -3).isNegative, true)
  }

  test("withYears") {
    val test: Period = Period.of(1, 2, 3)
    assertPeriod(test.withYears(10), 10, 2, 3)
  }

  test("withYears_noChange") {
    val test: Period = Period.of(1, 2, 3)
    assertSame(test.withYears(1), test)
  }

  test("withYears_toZero") {
    val test: Period = Period.ofYears(1)
    assertSame(test.withYears(0), Period.ZERO)
  }

  test("withMonths") {
    val test: Period = Period.of(1, 2, 3)
    assertPeriod(test.withMonths(10), 1, 10, 3)
  }

  test("withMonths_noChange") {
    val test: Period = Period.of(1, 2, 3)
    assertSame(test.withMonths(2), test)
  }

  test("withMonths_toZero") {
    val test: Period = Period.ofMonths(1)
    assertSame(test.withMonths(0), Period.ZERO)
  }

  test("withDays") {
    val test: Period = Period.of(1, 2, 3)
    assertPeriod(test.withDays(10), 1, 2, 10)
  }

  test("withDays_noChange") {
    val test: Period = Period.of(1, 2, 3)
    assertSame(test.withDays(3), test)
  }

  test("withDays_toZero") {
    val test: Period = Period.ofDays(1)
    assertSame(test.withDays(0), Period.ZERO)
  }

  def data_plus: List[List[Period]] =
    List(
      List(TestPeriod.pymd(0, 0, 0), TestPeriod.pymd(0, 0, 0), TestPeriod.pymd(0, 0, 0)),
      List(TestPeriod.pymd(0, 0, 0), TestPeriod.pymd(5, 0, 0), TestPeriod.pymd(5, 0, 0)),
      List(TestPeriod.pymd(0, 0, 0), TestPeriod.pymd(-5, 0, 0), TestPeriod.pymd(-5, 0, 0)),
      List(TestPeriod.pymd(0, 0, 0), TestPeriod.pymd(0, 5, 0), TestPeriod.pymd(0, 5, 0)),
      List(TestPeriod.pymd(0, 0, 0), TestPeriod.pymd(0, -5, 0), TestPeriod.pymd(0, -5, 0)),
      List(TestPeriod.pymd(0, 0, 0), TestPeriod.pymd(0, 0, 5), TestPeriod.pymd(0, 0, 5)),
      List(TestPeriod.pymd(0, 0, 0), TestPeriod.pymd(0, 0, -5), TestPeriod.pymd(0, 0, -5)),
      List(TestPeriod.pymd(0, 0, 0), TestPeriod.pymd(2, 3, 4), TestPeriod.pymd(2, 3, 4)),
      List(TestPeriod.pymd(0, 0, 0), TestPeriod.pymd(-2, -3, -4), TestPeriod.pymd(-2, -3, -4)),
      List(TestPeriod.pymd(4, 5, 6), TestPeriod.pymd(2, 3, 4), TestPeriod.pymd(6, 8, 10)),
      List(TestPeriod.pymd(4, 5, 6), TestPeriod.pymd(-2, -3, -4), TestPeriod.pymd(2, 2, 2))
    )

  test("plus") {
    data_plus.foreach {
      case base :: add :: expected :: Nil =>
        assertEquals(base.plus(add), expected)
      case _                              =>
        fail()
    }
  }

  test("plusYears") {
    val test: Period = Period.of(1, 2, 3)
    assertPeriod(test.plusYears(10), 11, 2, 3)
    assertPeriod(test.plus(Period.ofYears(10)), 11, 2, 3)
  }

  test("plusYears_noChange") {
    val test: Period = Period.of(1, 2, 3)
    assertSame(test.plusYears(0), test)
    assertPeriod(test.plus(Period.ofYears(0)), 1, 2, 3)
  }

  test("plusYears_toZero") {
    val test: Period = Period.ofYears(-1)
    assertSame(test.plusYears(1), Period.ZERO)
    assertSame(test.plus(Period.ofYears(1)), Period.ZERO)
  }

  test("plusYears_overflowTooBig") {
    assertThrows[ArithmeticException] {
      val test: Period = Period.ofYears(Int.MaxValue)
      test.plusYears(1)
    }
  }

  test("plusYears_overflowTooSmall") {
    assertThrows[ArithmeticException] {
      val test: Period = Period.ofYears(Int.MinValue)
      test.plusYears(-1)
    }
  }

  test("plusMonths") {
    val test: Period = Period.of(1, 2, 3)
    assertPeriod(test.plusMonths(10), 1, 12, 3)
    assertPeriod(test.plus(Period.ofMonths(10)), 1, 12, 3)
  }

  test("plusMonths_noChange") {
    val test: Period = Period.of(1, 2, 3)
    assertSame(test.plusMonths(0), test)
    assertEquals(test.plus(Period.ofMonths(0)), test)
  }

  test("plusMonths_toZero") {
    val test: Period = Period.ofMonths(-1)
    assertSame(test.plusMonths(1), Period.ZERO)
    assertSame(test.plus(Period.ofMonths(1)), Period.ZERO)
  }

  test("plusMonths_overflowTooBig") {
    assertThrows[ArithmeticException] {
      val test: Period = Period.ofMonths(Int.MaxValue)
      test.plusMonths(1)
    }
  }

  test("plusMonths_overflowTooSmall") {
    assertThrows[ArithmeticException] {
      val test: Period = Period.ofMonths(Int.MinValue)
      test.plusMonths(-1)
    }
  }

  test("plusDays") {
    val test: Period = Period.of(1, 2, 3)
    assertPeriod(test.plusDays(10), 1, 2, 13)
  }

  test("plusDays_noChange") {
    val test: Period = Period.of(1, 2, 3)
    assertSame(test.plusDays(0), test)
  }

  test("plusDays_toZero") {
    val test: Period = Period.ofDays(-1)
    assertSame(test.plusDays(1), Period.ZERO)
  }

  test("plusDays_overflowTooBig") {
    assertThrows[ArithmeticException] {
      val test: Period = Period.ofDays(Int.MaxValue)
      test.plusDays(1)
    }
  }

  test("plusDays_overflowTooSmall") {
    assertThrows[ArithmeticException] {
      val test: Period = Period.ofDays(Int.MinValue)
      test.plusDays(-1)
    }
  }

  def data_minus: List[List[Period]] =
    List(
      List(TestPeriod.pymd(0, 0, 0), TestPeriod.pymd(0, 0, 0), TestPeriod.pymd(0, 0, 0)),
      List(TestPeriod.pymd(0, 0, 0), TestPeriod.pymd(5, 0, 0), TestPeriod.pymd(-5, 0, 0)),
      List(TestPeriod.pymd(0, 0, 0), TestPeriod.pymd(-5, 0, 0), TestPeriod.pymd(5, 0, 0)),
      List(TestPeriod.pymd(0, 0, 0), TestPeriod.pymd(0, 5, 0), TestPeriod.pymd(0, -5, 0)),
      List(TestPeriod.pymd(0, 0, 0), TestPeriod.pymd(0, -5, 0), TestPeriod.pymd(0, 5, 0)),
      List(TestPeriod.pymd(0, 0, 0), TestPeriod.pymd(0, 0, 5), TestPeriod.pymd(0, 0, -5)),
      List(TestPeriod.pymd(0, 0, 0), TestPeriod.pymd(0, 0, -5), TestPeriod.pymd(0, 0, 5)),
      List(TestPeriod.pymd(0, 0, 0), TestPeriod.pymd(2, 3, 4), TestPeriod.pymd(-2, -3, -4)),
      List(TestPeriod.pymd(0, 0, 0), TestPeriod.pymd(-2, -3, -4), TestPeriod.pymd(2, 3, 4)),
      List(TestPeriod.pymd(4, 5, 6), TestPeriod.pymd(2, 3, 4), TestPeriod.pymd(2, 2, 2)),
      List(TestPeriod.pymd(4, 5, 6), TestPeriod.pymd(-2, -3, -4), TestPeriod.pymd(6, 8, 10))
    )

  test("minus") {
    data_minus.foreach {
      case base :: subtract :: expected :: Nil =>
        assertEquals(base.minus(subtract), expected)
      case _                                   =>
        fail()
    }
  }

  test("minusYears") {
    val test: Period = Period.of(1, 2, 3)
    assertPeriod(test.minusYears(10), -9, 2, 3)
  }

  test("minusYears_noChange") {
    val test: Period = Period.of(1, 2, 3)
    assertSame(test.minusYears(0), test)
  }

  test("minusYears_toZero") {
    val test: Period = Period.ofYears(1)
    assertSame(test.minusYears(1), Period.ZERO)
  }

  test("minusYears_overflowTooBig") {
    assertThrows[ArithmeticException] {
      val test: Period = Period.ofYears(Int.MaxValue)
      test.minusYears(-1)
    }
  }

  test("minusYears_overflowTooSmall") {
    assertThrows[ArithmeticException] {
      val test: Period = Period.ofYears(Int.MinValue)
      test.minusYears(1)
    }
  }

  test("minusMonths") {
    val test: Period = Period.of(1, 2, 3)
    assertPeriod(test.minusMonths(10), 1, -8, 3)
  }

  test("minusMonths_noChange") {
    val test: Period = Period.of(1, 2, 3)
    assertSame(test.minusMonths(0), test)
  }

  test("minusMonths_toZero") {
    val test: Period = Period.ofMonths(1)
    assertSame(test.minusMonths(1), Period.ZERO)
  }

  test("minusMonths_overflowTooBig") {
    assertThrows[ArithmeticException] {
      val test: Period = Period.ofMonths(Int.MaxValue)
      test.minusMonths(-1)
    }
  }

  test("minusMonths_overflowTooSmall") {
    assertThrows[ArithmeticException] {
      val test: Period = Period.ofMonths(Int.MinValue)
      test.minusMonths(1)
    }
  }

  test("minusDays") {
    val test: Period = Period.of(1, 2, 3)
    assertPeriod(test.minusDays(10), 1, 2, -7)
  }

  test("minusDays_noChange") {
    val test: Period = Period.of(1, 2, 3)
    assertSame(test.minusDays(0), test)
  }

  test("minusDays_toZero") {
    val test: Period = Period.ofDays(1)
    assertSame(test.minusDays(1), Period.ZERO)
  }

  test("minusDays_overflowTooBig") {
    assertThrows[ArithmeticException] {
      val test: Period = Period.ofDays(Int.MaxValue)
      test.minusDays(-1)
    }
  }

  test("minusDays_overflowTooSmall") {
    assertThrows[ArithmeticException] {
      val test: Period = Period.ofDays(Int.MinValue)
      test.minusDays(1)
    }
  }

  test("multipliedBy") {
    val test: Period = Period.of(1, 2, 3)
    assertPeriod(test.multipliedBy(2), 2, 4, 6)
    assertPeriod(test.multipliedBy(-3), -3, -6, -9)
  }

  test("multipliedBy_zeroBase") {
    assertSame(Period.ZERO.multipliedBy(2), Period.ZERO)
  }

  test("multipliedBy_zero") {
    val test: Period = Period.of(1, 2, 3)
    assertSame(test.multipliedBy(0), Period.ZERO)
  }

  test("multipliedBy_one") {
    val test: Period = Period.of(1, 2, 3)
    assertSame(test.multipliedBy(1), test)
  }

  test("multipliedBy_overflowTooBig") {
    assertThrows[ArithmeticException] {
      val test: Period = Period.ofYears(Int.MaxValue / 2 + 1)
      test.multipliedBy(2)
    }
  }

  test("multipliedBy_overflowTooSmall") {
    assertThrows[ArithmeticException] {
      val test: Period = Period.ofYears(Int.MinValue / 2 - 1)
      test.multipliedBy(2)
    }
  }

  test("negated") {
    val test: Period = Period.of(1, 2, 3)
    assertPeriod(test.negated, -1, -2, -3)
  }

  test("negated_zero") {
    assertSame(Period.ZERO.negated, Period.ZERO)
  }

  test("negated_max") {
    assertPeriod(Period.ofYears(Int.MaxValue).negated, -Int.MaxValue, 0, 0)
  }

  test("negated_overflow") {
    assertThrows[ArithmeticException] {
      Period.ofYears(Int.MinValue).negated
    }
  }

  def data_normalized: List[List[Int]] =
    List(
      List(0, 0, 0, 0),
      List(1, 0, 1, 0),
      List(-1, 0, -1, 0),
      List(1, 1, 1, 1),
      List(1, 2, 1, 2),
      List(1, 11, 1, 11),
      List(1, 12, 2, 0),
      List(1, 13, 2, 1),
      List(1, 23, 2, 11),
      List(1, 24, 3, 0),
      List(1, 25, 3, 1),
      List(1, -1, 0, 11),
      List(1, -2, 0, 10),
      List(1, -11, 0, 1),
      List(1, -12, 0, 0),
      List(1, -13, 0, -1),
      List(1, -23, 0, -11),
      List(1, -24, -1, 0),
      List(1, -25, -1, -1),
      List(1, -35, -1, -11),
      List(1, -36, -2, 0),
      List(1, -37, -2, -1),
      List(-1, 1, 0, -11),
      List(-1, 11, 0, -1),
      List(-1, 12, 0, 0),
      List(-1, 13, 0, 1),
      List(-1, 23, 0, 11),
      List(-1, 24, 1, 0),
      List(-1, 25, 1, 1),
      List(-1, -1, -1, -1),
      List(-1, -11, -1, -11),
      List(-1, -12, -2, 0),
      List(-1, -13, -2, -1)
    )

  test("normalized") {
    data_normalized.foreach {
      case inputYears :: inputMonths :: expectedYears :: expectedMonths :: Nil =>
        assertPeriod(Period.of(inputYears, inputMonths, 0).normalized,
                     expectedYears,
                     expectedMonths,
                     0
        )
      case _                                                                   =>
        fail()
    }
  }

  test("normalizedMonthsISO_min") {
    assertThrows[ArithmeticException] {
      val base: Period = Period.of(Int.MinValue, -12, 0)
      base.normalized
    }
  }

  test("normalizedMonthsISO_max") {
    assertThrows[ArithmeticException] {
      val base: Period = Period.of(Int.MaxValue, 12, 0)
      base.normalized
    }
  }

  def data_addTo: List[List[AnyRef]] =
    List(
      List(TestPeriod.pymd(0, 0, 0), TestPeriod.date(2012, 6, 30), TestPeriod.date(2012, 6, 30)),
      List(TestPeriod.pymd(1, 0, 0), TestPeriod.date(2012, 6, 10), TestPeriod.date(2013, 6, 10)),
      List(TestPeriod.pymd(0, 1, 0), TestPeriod.date(2012, 6, 10), TestPeriod.date(2012, 7, 10)),
      List(TestPeriod.pymd(0, 0, 1), TestPeriod.date(2012, 6, 10), TestPeriod.date(2012, 6, 11)),
      List(TestPeriod.pymd(-1, 0, 0), TestPeriod.date(2012, 6, 10), TestPeriod.date(2011, 6, 10)),
      List(TestPeriod.pymd(0, -1, 0), TestPeriod.date(2012, 6, 10), TestPeriod.date(2012, 5, 10)),
      List(TestPeriod.pymd(0, 0, -1), TestPeriod.date(2012, 6, 10), TestPeriod.date(2012, 6, 9)),
      List(TestPeriod.pymd(1, 2, 3), TestPeriod.date(2012, 6, 27), TestPeriod.date(2013, 8, 30)),
      List(TestPeriod.pymd(1, 2, 3), TestPeriod.date(2012, 6, 28), TestPeriod.date(2013, 8, 31)),
      List(TestPeriod.pymd(1, 2, 3), TestPeriod.date(2012, 6, 29), TestPeriod.date(2013, 9, 1)),
      List(TestPeriod.pymd(1, 2, 3), TestPeriod.date(2012, 6, 30), TestPeriod.date(2013, 9, 2)),
      List(TestPeriod.pymd(1, 2, 3), TestPeriod.date(2012, 7, 1), TestPeriod.date(2013, 9, 4)),
      List(TestPeriod.pymd(1, 0, 0), TestPeriod.date(2011, 2, 28), TestPeriod.date(2012, 2, 28)),
      List(TestPeriod.pymd(4, 0, 0), TestPeriod.date(2011, 2, 28), TestPeriod.date(2015, 2, 28)),
      List(TestPeriod.pymd(1, 0, 0), TestPeriod.date(2012, 2, 29), TestPeriod.date(2013, 2, 28)),
      List(TestPeriod.pymd(4, 0, 0), TestPeriod.date(2012, 2, 29), TestPeriod.date(2016, 2, 29)),
      List(TestPeriod.pymd(1, 1, 0), TestPeriod.date(2011, 1, 29), TestPeriod.date(2012, 2, 29)),
      List(TestPeriod.pymd(1, 2, 0), TestPeriod.date(2012, 2, 29), TestPeriod.date(2013, 4, 29))
    )

  test("addTo") {
    data_addTo.foreach {
      case (period: Period) :: (baseDate: LocalDate) :: (expected: LocalDate) :: Nil =>
        assertEquals(period.addTo(baseDate), expected)
      case _                                                                         =>
        fail()
    }
  }

  test("addTo_usingLocalDatePlus(period: Period, baseDate: LocalDate, expected: LocalDate") {
    data_addTo.foreach {
      case (period: Period) :: (baseDate: LocalDate) :: (expected: LocalDate) :: Nil =>
        assertEquals(baseDate.plus(period), expected)
      case _                                                                         =>
        fail()
    }
  }

  test("addTo_nullZero") {
    assertThrows[NullPointerException] {
      Period.ZERO.addTo(null)
    }
  }

  test("addTo_nullNonZero") {
    assertThrows[NullPointerException] {
      Period.ofDays(2).addTo(null)
    }
  }

  def data_subtractFrom: List[List[AnyRef]] =
    List(
      List(TestPeriod.pymd(0, 0, 0), TestPeriod.date(2012, 6, 30), TestPeriod.date(2012, 6, 30)),
      List(TestPeriod.pymd(1, 0, 0), TestPeriod.date(2012, 6, 10), TestPeriod.date(2011, 6, 10)),
      List(TestPeriod.pymd(0, 1, 0), TestPeriod.date(2012, 6, 10), TestPeriod.date(2012, 5, 10)),
      List(TestPeriod.pymd(0, 0, 1), TestPeriod.date(2012, 6, 10), TestPeriod.date(2012, 6, 9)),
      List(TestPeriod.pymd(-1, 0, 0), TestPeriod.date(2012, 6, 10), TestPeriod.date(2013, 6, 10)),
      List(TestPeriod.pymd(0, -1, 0), TestPeriod.date(2012, 6, 10), TestPeriod.date(2012, 7, 10)),
      List(TestPeriod.pymd(0, 0, -1), TestPeriod.date(2012, 6, 10), TestPeriod.date(2012, 6, 11)),
      List(TestPeriod.pymd(1, 2, 3), TestPeriod.date(2012, 8, 30), TestPeriod.date(2011, 6, 27)),
      List(TestPeriod.pymd(1, 2, 3), TestPeriod.date(2012, 8, 31), TestPeriod.date(2011, 6, 27)),
      List(TestPeriod.pymd(1, 2, 3), TestPeriod.date(2012, 9, 1), TestPeriod.date(2011, 6, 28)),
      List(TestPeriod.pymd(1, 2, 3), TestPeriod.date(2012, 9, 2), TestPeriod.date(2011, 6, 29)),
      List(TestPeriod.pymd(1, 2, 3), TestPeriod.date(2012, 9, 3), TestPeriod.date(2011, 6, 30)),
      List(TestPeriod.pymd(1, 2, 3), TestPeriod.date(2012, 9, 4), TestPeriod.date(2011, 7, 1)),
      List(TestPeriod.pymd(1, 0, 0), TestPeriod.date(2011, 2, 28), TestPeriod.date(2010, 2, 28)),
      List(TestPeriod.pymd(4, 0, 0), TestPeriod.date(2011, 2, 28), TestPeriod.date(2007, 2, 28)),
      List(TestPeriod.pymd(1, 0, 0), TestPeriod.date(2012, 2, 29), TestPeriod.date(2011, 2, 28)),
      List(TestPeriod.pymd(4, 0, 0), TestPeriod.date(2012, 2, 29), TestPeriod.date(2008, 2, 29)),
      List(TestPeriod.pymd(1, 1, 0), TestPeriod.date(2013, 3, 29), TestPeriod.date(2012, 2, 29)),
      List(TestPeriod.pymd(1, 2, 0), TestPeriod.date(2012, 2, 29), TestPeriod.date(2010, 12, 29))
    )

  test("subtractFrom(period: Period, baseDate: LocalDate, expected: LocalDate") {
    data_subtractFrom.foreach {
      case (period: Period) :: (baseDate: LocalDate) :: (expected: LocalDate) :: Nil =>
        assertEquals(period.subtractFrom(baseDate), expected)
      case _                                                                         =>
        fail()
    }
  }

  test(
    "subtractFrom_usingLocalDateMinus(period: Period, baseDate: LocalDate, expected: LocalDate"
  ) {
    data_subtractFrom.foreach {
      case (period: Period) :: (baseDate: LocalDate) :: (expected: LocalDate) :: Nil =>
        assertEquals(baseDate.minus(period), expected)
      case _                                                                         =>
        fail()
    }
  }

  test("subtractFrom_nullZero") {
    assertThrows[NullPointerException] {
      Period.ZERO.subtractFrom(null)
    }
  }

  test("subtractFrom_nullNonZero") {
    assertThrows[NullPointerException] {
      Period.ofDays(2).subtractFrom(null)
    }
  }

  test("equals") {
    assertEquals(Period.of(1, 0, 0) == Period.ofYears(1), true)
    assertEquals(Period.of(0, 1, 0) == Period.ofMonths(1), true)
    assertEquals(Period.of(0, 0, 1) == Period.ofDays(1), true)
    assertEquals(Period.of(1, 2, 3) == Period.of(1, 2, 3), true)
    assertEquals(Period.ofYears(1) == Period.ofYears(1), true)
    assertEquals(Period.ofYears(1) == Period.ofYears(2), false)
    assertEquals(Period.ofMonths(1) == Period.ofMonths(1), true)
    assertEquals(Period.ofMonths(1) == Period.ofMonths(2), false)
    assertEquals(Period.ofDays(1) == Period.ofDays(1), true)
    assertEquals(Period.ofDays(1) == Period.ofDays(2), false)
    assertEquals(Period.of(1, 2, 3) == Period.of(1, 2, 3), true)
    assertEquals(Period.of(1, 2, 3) == Period.of(0, 2, 3), false)
    assertEquals(Period.of(1, 2, 3) == Period.of(1, 0, 3), false)
    assertEquals(Period.of(1, 2, 3) == Period.of(1, 2, 0), false)
  }

  test("equals_self") {
    val test: Period = Period.of(1, 2, 3)
    assertEquals(test == test, true)
  }

  test("equals_null") {
    val test: Period = Period.of(1, 2, 3)
    assertEquals(test == null, false)
  }

  test("equals_otherClass") {
    val test: Period = Period.of(1, 2, 3)
    assertNotEquals(test, "")
  }

  test("hashCode") {
    val test5: Period  = Period.ofDays(5)
    val test6: Period  = Period.ofDays(6)
    val test5M: Period = Period.ofMonths(5)
    val test5Y: Period = Period.ofYears(5)
    assertEquals(test5.hashCode == test5.hashCode, true)
    assertEquals(test5.hashCode == test6.hashCode, false)
    assertEquals(test5.hashCode == test5M.hashCode, false)
    assertEquals(test5.hashCode == test5Y.hashCode, false)
  }

  def data_toString: List[List[AnyRef]] =
    List(
      List(Period.ZERO, "P0D"),
      List(Period.ofDays(0), "P0D"),
      List(Period.ofYears(1), "P1Y"),
      List(Period.ofMonths(1), "P1M"),
      List(Period.ofDays(1), "P1D"),
      List(Period.of(1, 2, 3), "P1Y2M3D")
    )

  test("toString") {
    data_toString.foreach {
      case (input: Period) :: (expected: String) :: Nil =>
        assertEquals(input.toString, expected)
      case _                                            =>
        fail()
    }
  }

  private def assertPeriod(test: Period, y: Int, mo: Int, d: Int): Unit = {
    assertEquals(test.getYears, y, "years")
    assertEquals(test.getMonths, mo, "months")
    assertEquals(test.getDays, d, "days")
  }
}
