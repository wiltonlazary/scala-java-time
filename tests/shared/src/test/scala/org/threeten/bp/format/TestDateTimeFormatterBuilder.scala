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

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfterEach
import org.threeten.bp.AssertionsHelper
import org.threeten.bp.temporal.ChronoField.DAY_OF_MONTH
import org.threeten.bp.temporal.ChronoField.DAY_OF_WEEK
import org.threeten.bp.temporal.ChronoField.MINUTE_OF_HOUR
import org.threeten.bp.temporal.ChronoField.MONTH_OF_YEAR
import org.threeten.bp.temporal.ChronoField.YEAR
import org.threeten.bp.temporal.TemporalAccessor
import org.threeten.bp.Platform

import scala.language.implicitConversions
import scala.collection.JavaConverters._

import java.text.ParsePosition

/** Test DateTimeFormatterBuilder. */
class TestDateTimeFormatterBuilder extends FunSuite with AssertionsHelper with BeforeAndAfterEach {
  // We need these ugly converters to fit the signatures in everycase
  implicit def convLongMap(al: Map[Long, String]): java.util.Map[java.lang.Long, String] =
    al.map(k => (Long.box(k._1), k._2)).asJava
  implicit def convSLongMap(al: Map[Long, String]): java.util.Map[Long, String] =
    al.map(k => (k._1, k._2)).asJava

  private var builder: DateTimeFormatterBuilder = null

  override def beforeEach() {
    builder = new DateTimeFormatterBuilder
  }

  test("test_toFormatter_empty") {
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "")
  }

  test("test_parseCaseSensitive") {
    builder.parseCaseSensitive
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "ParseCaseSensitive(true)")
  }

  test("test_parseCaseInsensitive") {
    builder.parseCaseInsensitive
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "ParseCaseSensitive(false)")
  }

  test("test_parseStrict") {
    builder.parseStrict
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "ParseStrict(true)")
  }

  test("test_parseLenient") {
    builder.parseLenient
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "ParseStrict(false)")
  }

  test("test_appendValue_1arg") {
    builder.appendValue(DAY_OF_MONTH)
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Value(DayOfMonth)")
  }

  test("test_appendValue_1arg_null") {
    assertThrows[NullPointerException] {
      builder.appendValue(null)
    }
  }

  test("test_appendValue_2arg") {
    builder.appendValue(DAY_OF_MONTH, 3)
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Value(DayOfMonth,3)")
  }

  test("test_appendValue_2arg_null") {
    assertThrows[NullPointerException] {
      builder.appendValue(null, 3)
    }
  }

  test("test_appendValue_2arg_widthTooSmall") {
    assertThrows[IllegalArgumentException] {
      builder.appendValue(DAY_OF_MONTH, 0)
    }
  }

  test("test_appendValue_2arg_widthTooBig") {
    assertThrows[IllegalArgumentException] {
      builder.appendValue(DAY_OF_MONTH, 20)
    }
  }

  test("test_appendValue_3arg") {
    builder.appendValue(DAY_OF_MONTH, 2, 3, SignStyle.NORMAL)
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Value(DayOfMonth,2,3,NORMAL)")
  }

  test("test_appendValue_3arg_nullField") {
    assertThrows[NullPointerException] {
      builder.appendValue(null, 2, 3, SignStyle.NORMAL)
    }
  }

  test("test_appendValue_3arg_minWidthTooSmall") {
    assertThrows[IllegalArgumentException] {
      builder.appendValue(DAY_OF_MONTH, 0, 2, SignStyle.NORMAL)
    }
  }

  test("test_appendValue_3arg_minWidthTooBig") {
    assertThrows[IllegalArgumentException] {
      builder.appendValue(DAY_OF_MONTH, 20, 2, SignStyle.NORMAL)
    }
  }

  test("test_appendValue_3arg_maxWidthTooSmall") {
    assertThrows[IllegalArgumentException] {
      builder.appendValue(DAY_OF_MONTH, 2, 0, SignStyle.NORMAL)
    }
  }

  test("test_appendValue_3arg_maxWidthTooBig") {
    assertThrows[IllegalArgumentException] {
      builder.appendValue(DAY_OF_MONTH, 2, 20, SignStyle.NORMAL)
    }
  }

  test("test_appendValue_3arg_maxWidthMinWidth") {
    assertThrows[IllegalArgumentException] {
      builder.appendValue(DAY_OF_MONTH, 4, 2, SignStyle.NORMAL)
    }
  }

  test("test_appendValue_3arg_nullSignStyle") {
    assertThrows[NullPointerException] {
      builder.appendValue(DAY_OF_MONTH, 2, 3, null)
    }
  }

  test("test_appendValue_subsequent2_parse3") {
    builder.appendValue(MONTH_OF_YEAR, 1, 2, SignStyle.NORMAL).appendValue(DAY_OF_MONTH, 2)
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Value(MonthOfYear,1,2,NORMAL)Value(DayOfMonth,2)")
    val cal: TemporalAccessor = f.parseUnresolved("123", new ParsePosition(0))
    assertEquals(cal.get(MONTH_OF_YEAR), 1)
    assertEquals(cal.get(DAY_OF_MONTH), 23)
  }

  test("test_appendValue_subsequent2_parse4") {
    builder.appendValue(MONTH_OF_YEAR, 1, 2, SignStyle.NORMAL).appendValue(DAY_OF_MONTH, 2)
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Value(MonthOfYear,1,2,NORMAL)Value(DayOfMonth,2)")
    val cal: TemporalAccessor = f.parseUnresolved("0123", new ParsePosition(0))
    assertEquals(cal.get(MONTH_OF_YEAR), 1)
    assertEquals(cal.get(DAY_OF_MONTH), 23)
  }

  test("test_appendValue_subsequent2_parse5") {
    builder
      .appendValue(MONTH_OF_YEAR, 1, 2, SignStyle.NORMAL)
      .appendValue(DAY_OF_MONTH, 2)
      .appendLiteral('4')
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Value(MonthOfYear,1,2,NORMAL)Value(DayOfMonth,2)'4'")
    val cal: TemporalAccessor = f.parseUnresolved("01234", new ParsePosition(0))
    assertEquals(cal.get(MONTH_OF_YEAR), 1)
    assertEquals(cal.get(DAY_OF_MONTH), 23)
  }

  test("test_appendValue_subsequent3_parse6") {
    builder
      .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
      .appendValue(MONTH_OF_YEAR, 2)
      .appendValue(DAY_OF_MONTH, 2)
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Value(Year,4,10,EXCEEDS_PAD)Value(MonthOfYear,2)Value(DayOfMonth,2)")
    val cal: TemporalAccessor = f.parseUnresolved("20090630", new ParsePosition(0))
    assertEquals(cal.get(YEAR), 2009)
    assertEquals(cal.get(MONTH_OF_YEAR), 6)
    assertEquals(cal.get(DAY_OF_MONTH), 30)
  }

  test("test_appendValueReduced_null") {
    assertThrows[NullPointerException] {
      builder.appendValueReduced(null, 2, 2, 2000)
    }
  }

  test("test_appendValueReduced") {
    builder.appendValueReduced(YEAR, 2, 2, 2000)
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "ReducedValue(Year,2,2,2000)")
    val cal: TemporalAccessor = f.parseUnresolved("12", new ParsePosition(0))
    assertEquals(cal.get(YEAR), 2012)
  }

  test("test_appendValueReduced_subsequent_parse") {
    builder.appendValue(MONTH_OF_YEAR, 1, 2, SignStyle.NORMAL).appendValueReduced(YEAR, 2, 2, 2000)
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Value(MonthOfYear,1,2,NORMAL)ReducedValue(Year,2,2,2000)")
    val cal: TemporalAccessor = f.parseUnresolved("123", new ParsePosition(0))
    assertEquals(cal.get(MONTH_OF_YEAR), 1)
    assertEquals(cal.get(YEAR), 2023)
  }

  test("test_appendFraction_4arg") {
    builder.appendFraction(MINUTE_OF_HOUR, 1, 9, false)
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Fraction(MinuteOfHour,1,9)")
  }

  test("test_appendFraction_4arg_nullRule") {
    assertThrows[NullPointerException] {
      builder.appendFraction(null, 1, 9, false)
    }
  }

  test("test_appendFraction_4arg_invalidRuleNotFixedSet") {
    assertThrows[IllegalArgumentException] {
      builder.appendFraction(DAY_OF_MONTH, 1, 9, false)
    }
  }

  test("test_appendFraction_4arg_minTooSmall") {
    assertThrows[IllegalArgumentException] {
      builder.appendFraction(MINUTE_OF_HOUR, -1, 9, false)
    }
  }

  test("test_appendFraction_4arg_minTooBig") {
    assertThrows[IllegalArgumentException] {
      builder.appendFraction(MINUTE_OF_HOUR, 10, 9, false)
    }
  }

  test("test_appendFraction_4arg_maxTooSmall") {
    assertThrows[IllegalArgumentException] {
      builder.appendFraction(MINUTE_OF_HOUR, 0, -1, false)
    }
  }

  test("test_appendFraction_4arg_maxWidthMinWidth") {
    assertThrows[IllegalArgumentException] {
      builder.appendFraction(MINUTE_OF_HOUR, 9, 3, false)
    }
  }

  test("test_appendText_1arg") {
    builder.appendText(MONTH_OF_YEAR)
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Text(MonthOfYear)")
  }

  test("test_appendText_1arg_null") {
    assertThrows[NullPointerException] {
      builder.appendText(null)
    }
  }

  test("test_appendText_2arg") {
    builder.appendText(MONTH_OF_YEAR, TextStyle.SHORT)
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Text(MonthOfYear,SHORT)")
  }

  test("test_appendText_2arg_nullRule") {
    assertThrows[NullPointerException] {
      builder.appendText(null, TextStyle.SHORT)
    }
  }

  test("test_appendText_2arg_nullStyle") {
    assertThrows[NullPointerException] {
      builder.appendText(MONTH_OF_YEAR, null.asInstanceOf[TextStyle])
    }
  }

  test("test_appendTextMap") {
    val map = Map(1L -> "JNY",
                  2L -> "FBY",
                  3L -> "MCH",
                  4L -> "APL",
                  5L -> "MAY",
                  6L -> "JUN",
                  7L -> "JLY",
                  8L -> "AGT",
                  9L -> "SPT",
                  10L -> "OBR",
                  11L -> "NVR",
                  12L -> "DBR")
    builder.appendText(MONTH_OF_YEAR, map)
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Text(MonthOfYear)")
  }

  test("test_appendTextMap_nullRule") {
    assertThrows[NullPointerException] {
      builder.appendText(null, Map.empty[Long, String])
    }
  }

  test("test_appendTextMap_nullStyle") {
    assertThrows[Platform.NPE] {
      builder.appendText(MONTH_OF_YEAR, null: Map[Long, String])
    }
  }

  test("test_appendOffsetId") {
    builder.appendOffsetId
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Offset(+HH:MM:ss,'Z')")
  }

  val data_offsetPatterns: List[String] = {
    List("+HH", "+HHMM", "+HH:MM", "+HHMMss", "+HH:MM:ss", "+HHMMSS", "+HH:MM:SS")
  }

  test("test_appendOffset") {
    data_offsetPatterns.foreach { pattern =>
      beforeEach()
      builder.appendOffset(pattern, "Z")
      val f: DateTimeFormatter = builder.toFormatter
      assertEquals(f.toString, "Offset(" + pattern + ",'Z')")
    }
  }

  val data_badOffsetPatterns: List[String] =
    List("HH",
         "HHMM",
         "HH:MM",
         "HHMMss",
         "HH:MM:ss",
         "HHMMSS",
         "HH:MM:SS",
         "+H",
         "+HMM",
         "+HHM",
         "+A")

  test("test_appendOffset_badPattern") {
    data_badOffsetPatterns.foreach { pattern =>
      assertThrows[IllegalArgumentException] {
        builder.appendOffset(pattern, "Z")
      }
    }
  }

  test("test_appendOffset_3arg_nullText") {
    assertThrows[NullPointerException] {
      builder.appendOffset("+HH:MM", null)
    }
  }

  test("test_appendOffset_3arg_nullPattern") {
    assertThrows[NullPointerException] {
      builder.appendOffset(null, "Z")
    }
  }

  test("test_appendZoneId") {
    builder.appendZoneId
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "ZoneId()")
  }

  test("test_appendZoneText_1arg") {
    builder.appendZoneText(TextStyle.FULL)
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "ZoneText(FULL)")
  }

  test("test_appendZoneText_1arg_nullText") {
    assertThrows[NullPointerException] {
      builder.appendZoneText(null)
    }
  }

  test("test_padNext_1arg") {
    builder.appendValue(MONTH_OF_YEAR).padNext(2).appendValue(DAY_OF_MONTH).appendValue(DAY_OF_WEEK)
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Value(MonthOfYear)Pad(Value(DayOfMonth),2)Value(DayOfWeek)")
  }

  test("test_padNext_1arg_invalidWidth") {
    assertThrows[IllegalArgumentException] {
      builder.padNext(0)
    }
  }

  test("test_padNext_2arg_dash") {
    builder
      .appendValue(MONTH_OF_YEAR)
      .padNext(2, '-')
      .appendValue(DAY_OF_MONTH)
      .appendValue(DAY_OF_WEEK)
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Value(MonthOfYear)Pad(Value(DayOfMonth),2,'-')Value(DayOfWeek)")
  }

  test("test_padNext_2arg_invalidWidth") {
    assertThrows[IllegalArgumentException] {
      builder.padNext(0, '-')
    }
  }

  test("test_padOptional") {
    builder
      .appendValue(MONTH_OF_YEAR)
      .padNext(5)
      .optionalStart
      .appendValue(DAY_OF_MONTH)
      .optionalEnd
      .appendValue(DAY_OF_WEEK)
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Value(MonthOfYear)Pad([Value(DayOfMonth)],5)Value(DayOfWeek)")
  }

  test("test_optionalStart_noEnd") {
    builder
      .appendValue(MONTH_OF_YEAR)
      .optionalStart
      .appendValue(DAY_OF_MONTH)
      .appendValue(DAY_OF_WEEK)
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Value(MonthOfYear)[Value(DayOfMonth)Value(DayOfWeek)]")
  }

  test("test_optionalStart2_noEnd") {
    builder
      .appendValue(MONTH_OF_YEAR)
      .optionalStart
      .appendValue(DAY_OF_MONTH)
      .optionalStart
      .appendValue(DAY_OF_WEEK)
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Value(MonthOfYear)[Value(DayOfMonth)[Value(DayOfWeek)]]")
  }

  test("test_optionalStart_doubleStart") {
    builder.appendValue(MONTH_OF_YEAR).optionalStart.optionalStart.appendValue(DAY_OF_MONTH)
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Value(MonthOfYear)[[Value(DayOfMonth)]]")
  }

  test("test_optionalEnd") {
    builder
      .appendValue(MONTH_OF_YEAR)
      .optionalStart
      .appendValue(DAY_OF_MONTH)
      .optionalEnd
      .appendValue(DAY_OF_WEEK)
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Value(MonthOfYear)[Value(DayOfMonth)]Value(DayOfWeek)")
  }

  test("test_optionalEnd2") {
    builder
      .appendValue(MONTH_OF_YEAR)
      .optionalStart
      .appendValue(DAY_OF_MONTH)
      .optionalStart
      .appendValue(DAY_OF_WEEK)
      .optionalEnd
      .appendValue(DAY_OF_MONTH)
      .optionalEnd
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString,
                 "Value(MonthOfYear)[Value(DayOfMonth)[Value(DayOfWeek)]Value(DayOfMonth)]")
  }

  test("test_optionalEnd_doubleStartSingleEnd") {
    builder
      .appendValue(MONTH_OF_YEAR)
      .optionalStart
      .optionalStart
      .appendValue(DAY_OF_MONTH)
      .optionalEnd
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Value(MonthOfYear)[[Value(DayOfMonth)]]")
  }

  test("test_optionalEnd_doubleStartDoubleEnd") {
    builder
      .appendValue(MONTH_OF_YEAR)
      .optionalStart
      .optionalStart
      .appendValue(DAY_OF_MONTH)
      .optionalEnd
      .optionalEnd
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Value(MonthOfYear)[[Value(DayOfMonth)]]")
  }

  test("test_optionalStartEnd_immediateStartEnd") {
    builder.appendValue(MONTH_OF_YEAR).optionalStart.optionalEnd.appendValue(DAY_OF_MONTH)
    val f: DateTimeFormatter = builder.toFormatter
    assertEquals(f.toString, "Value(MonthOfYear)Value(DayOfMonth)")
  }

  test("test_optionalEnd_noStart") {
    assertThrows[IllegalStateException] {
      builder.optionalEnd
    }
  }

  def dataValid: List[(String, String)] =
    List(
      ("'a'", "'a'"),
      ("''", "''"),
      ("'!'", "'!'"),
      ("!", "'!'"),
      ("'hello_people,][)('", "'hello_people,][)('"),
      ("'hi'", "'hi'"),
      ("'yyyy'", "'yyyy'"),
      ("''''", "''"),
      ("'o''clock'", "'o''clock'"),
      ("G", "Text(Era,SHORT)"),
      ("GG", "Text(Era,SHORT)"),
      ("GGG", "Text(Era,SHORT)"),
      ("GGGG", "Text(Era)"),
      ("GGGGG", "Text(Era,NARROW)"),
      ("u", "Value(Year)"),
      ("uu", "ReducedValue(Year,2,2,2000-01-01)"),
      ("uuu", "Value(Year,3,19,NORMAL)"),
      ("uuuu", "Value(Year,4,19,EXCEEDS_PAD)"),
      ("uuuuu", "Value(Year,5,19,EXCEEDS_PAD)"),
      ("y", "Value(YearOfEra)"),
      ("yy", "ReducedValue(YearOfEra,2,2,2000-01-01)"),
      ("yyy", "Value(YearOfEra,3,19,NORMAL)"),
      ("yyyy", "Value(YearOfEra,4,19,EXCEEDS_PAD)"),
      ("yyyyy", "Value(YearOfEra,5,19,EXCEEDS_PAD)"),
      ("M", "Value(MonthOfYear)"),
      ("MM", "Value(MonthOfYear,2)"),
      ("MMM", "Text(MonthOfYear,SHORT)"),
      ("MMMM", "Text(MonthOfYear)"),
      ("MMMMM", "Text(MonthOfYear,NARROW)"),
      ("D", "Value(DayOfYear)"),
      ("DD", "Value(DayOfYear,2)"),
      ("DDD", "Value(DayOfYear,3)"),
      ("d", "Value(DayOfMonth)"),
      ("dd", "Value(DayOfMonth,2)"),
      ("F", "Value(AlignedDayOfWeekInMonth)"),
      ("E", "Text(DayOfWeek,SHORT)"),
      ("EE", "Text(DayOfWeek,SHORT)"),
      ("EEE", "Text(DayOfWeek,SHORT)"),
      ("EEEE", "Text(DayOfWeek)"),
      ("EEEEE", "Text(DayOfWeek,NARROW)"),
      ("a", "Text(AmPmOfDay,SHORT)"),
      ("H", "Value(HourOfDay)"),
      ("HH", "Value(HourOfDay,2)"),
      ("K", "Value(HourOfAmPm)"),
      ("KK", "Value(HourOfAmPm,2)"),
      ("k", "Value(ClockHourOfDay)"),
      ("kk", "Value(ClockHourOfDay,2)"),
      ("h", "Value(ClockHourOfAmPm)"),
      ("hh", "Value(ClockHourOfAmPm,2)"),
      ("m", "Value(MinuteOfHour)"),
      ("mm", "Value(MinuteOfHour,2)"),
      ("s", "Value(SecondOfMinute)"),
      ("ss", "Value(SecondOfMinute,2)"),
      ("S", "Fraction(NanoOfSecond,1,1)"),
      ("SS", "Fraction(NanoOfSecond,2,2)"),
      ("SSS", "Fraction(NanoOfSecond,3,3)"),
      ("SSSSSSSSS", "Fraction(NanoOfSecond,9,9)"),
      ("A", "Value(MilliOfDay)"),
      ("AA", "Value(MilliOfDay,2)"),
      ("AAA", "Value(MilliOfDay,3)"),
      ("n", "Value(NanoOfSecond)"),
      ("nn", "Value(NanoOfSecond,2)"),
      ("nnn", "Value(NanoOfSecond,3)"),
      ("N", "Value(NanoOfDay)"),
      ("NN", "Value(NanoOfDay,2)"),
      ("NNN", "Value(NanoOfDay,3)"),
      ("z", "ZoneText(SHORT)"),
      ("zz", "ZoneText(SHORT)"),
      ("zzz", "ZoneText(SHORT)"),
      ("zzzz", "ZoneText(FULL)"),
      ("VV", "ZoneId()"),
      ("Z", "Offset(+HHMM,'+0000')"),
      ("ZZ", "Offset(+HHMM,'+0000')"),
      ("ZZZ", "Offset(+HHMM,'+0000')"),
      ("X", "Offset(+HHmm,'Z')"),
      ("XX", "Offset(+HHMM,'Z')"),
      ("XXX", "Offset(+HH:MM,'Z')"),
      ("XXXX", "Offset(+HHMMss,'Z')"),
      ("XXXXX", "Offset(+HH:MM:ss,'Z')"),
      ("x", "Offset(+HHmm,'+00')"),
      ("xx", "Offset(+HHMM,'+0000')"),
      ("xxx", "Offset(+HH:MM,'+00:00')"),
      ("xxxx", "Offset(+HHMMss,'+0000')"),
      ("xxxxx", "Offset(+HH:MM:ss,'+00:00')"),
      ("ppH", "Pad(Value(HourOfDay),2)"),
      ("pppDD", "Pad(Value(DayOfYear,2),3)"),
      ("uuuu[-MM[-dd",
       "Value(Year,4,19,EXCEEDS_PAD)['-'Value(MonthOfYear,2)['-'Value(DayOfMonth,2)]]"),
      ("uuuu[-MM[-dd]]",
       "Value(Year,4,19,EXCEEDS_PAD)['-'Value(MonthOfYear,2)['-'Value(DayOfMonth,2)]]"),
      ("uuuu[-MM[]-dd]",
       "Value(Year,4,19,EXCEEDS_PAD)['-'Value(MonthOfYear,2)'-'Value(DayOfMonth,2)]"),
      ("uuuu-MM-dd'T'HH:mm:ss.SSS",
       "Value(Year,4,19,EXCEEDS_PAD)'-'Value(MonthOfYear,2)'-'Value(DayOfMonth,2)" + "'T'Value(HourOfDay,2)':'Value(MinuteOfHour,2)':'Value(SecondOfMinute,2)'.'Fraction(NanoOfSecond,3,3)")
    )

  test("test_appendPattern_valid") {
    dataValid.foreach {
      case (input, expected) =>
        beforeEach()
        builder.appendPattern(input)
        val f: DateTimeFormatter = builder.toFormatter
        assertEquals(f.toString, expected)
      case _ =>
        fail()
    }
  }

  val dataInvalid: List[String] =
    List(
      "'",
      "'hello",
      "'hel''lo",
      "'hello''",
      "]",
      "{",
      "}",
      "#",
      "yyyy]",
      "yyyy]MM",
      "yyyy[MM]]",
      "MMMMMM",
      "QQQQQQ",
      "EEEEEE",
      "aaaaaa",
      "XXXXXX",
      "RO",
      "p",
      "pp",
      "p:",
      "f",
      "ff",
      "f:",
      "fy",
      "fa",
      "fM",
      "ddd",
      "FF",
      "FFF",
      "aa",
      "aaa",
      "aaaa",
      "aaaaa",
      "HHH",
      "KKK",
      "kkk",
      "hhh",
      "mmm",
      "sss"
    )

  def test_appendPattern_invalid(input: String): Unit =
    dataInvalid.foreach { input =>
      assertThrows[IllegalArgumentException] {
        builder.appendPattern(input)
      }
    }
}
