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

import java.util.Objects
import org.threeten.bp.temporal.ChronoField.INSTANT_SECONDS
import org.threeten.bp.temporal.ChronoField.NANO_OF_SECOND
import org.threeten.bp.temporal.ChronoField.OFFSET_SECONDS
import java.io.Serializable
import org.threeten.bp.chrono.ChronoZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoField
import org.threeten.bp.temporal.ChronoUnit
import org.threeten.bp.temporal.Temporal
import org.threeten.bp.temporal.TemporalAccessor
import org.threeten.bp.temporal.TemporalAdjuster
import org.threeten.bp.temporal.TemporalAmount
import org.threeten.bp.temporal.TemporalField
import org.threeten.bp.temporal.TemporalQueries
import org.threeten.bp.temporal.TemporalQuery
import org.threeten.bp.temporal.TemporalUnit
import org.threeten.bp.temporal.ValueRange
import org.threeten.bp.zone.ZoneOffsetTransition
import org.threeten.bp.zone.ZoneRules

object ZonedDateTime {

  /**
   * Obtains the current date-time from the system clock in the default time-zone.
   *
   * This will query the {@link Clock#systemDefaultZone() system clock} in the default time-zone to
   * obtain the current date-time. The zone and offset will be set based on the time-zone in the
   * clock.
   *
   * Using this method will prevent the ability to use an alternate clock for testing because the
   * clock is hard-coded.
   *
   * @return
   *   the current date-time using the system clock, not null
   */
  def now: ZonedDateTime = now(Clock.systemDefaultZone)

  /**
   * Obtains the current date-time from the system clock in the specified time-zone.
   *
   * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date-time.
   * Specifying the time-zone avoids dependence on the default time-zone. The offset will be
   * calculated from the specified time-zone.
   *
   * Using this method will prevent the ability to use an alternate clock for testing because the
   * clock is hard-coded.
   *
   * @param zone
   *   the zone ID to use, not null
   * @return
   *   the current date-time using the system clock, not null
   */
  def now(zone: ZoneId): ZonedDateTime = now(Clock.system(zone))

  /**
   * Obtains the current date-time from the specified clock.
   *
   * This will query the specified clock to obtain the current date-time. The zone and offset will
   * be set based on the time-zone in the clock.
   *
   * Using this method allows the use of an alternate clock for testing. The alternate clock may be
   * introduced using {@link Clock dependency injection}.
   *
   * @param clock
   *   the clock to use, not null
   * @return
   *   the current date-time, not null
   */
  def now(clock: Clock): ZonedDateTime = {
    Objects.requireNonNull(clock, "clock")
    val now: Instant = clock.instant
    ofInstant(now, clock.getZone)
  }

  /**
   * Obtains an instance of {@code ZonedDateTime} from a local date and time.
   *
   * This creates a zoned date-time matching the input local date and time as closely as possible.
   * Time-zone rules, such as daylight savings, mean that not every local date-time is valid for the
   * specified zone, thus the local date-time may be adjusted.
   *
   * The local date time and first combined to form a local date-time. The local date-time is then
   * resolved to a single instant on the time-line. This is achieved by finding a valid offset from
   * UTC/Greenwich for the local date-time as defined by the {@link ZoneRules rules} of the zone ID.
   *
   * In most cases, there is only one valid offset for a local date-time. In the case of an overlap,
   * when clocks are set back, there are two valid offsets. This method uses the earlier offset
   * typically corresponding to "summer".
   *
   * In the case of a gap, when clocks jump forward, there is no valid offset. Instead, the local
   * date-time is adjusted to be later by the length of the gap. For a typical one hour daylight
   * savings change, the local date-time will be moved one hour later into the offset typically
   * corresponding to "summer".
   *
   * @param date
   *   the local date, not null
   * @param time
   *   the local time, not null
   * @param zone
   *   the time-zone, not null
   * @return
   *   the offset date-time, not null
   */
  def of(date: LocalDate, time: LocalTime, zone: ZoneId): ZonedDateTime =
    of(LocalDateTime.of(date, time), zone)

  /**
   * Obtains an instance of {@code ZonedDateTime} from a local date-time.
   *
   * This creates a zoned date-time matching the input local date-time as closely as possible.
   * Time-zone rules, such as daylight savings, mean that not every local date-time is valid for the
   * specified zone, thus the local date-time may be adjusted.
   *
   * The local date-time is resolved to a single instant on the time-line. This is achieved by
   * finding a valid offset from UTC/Greenwich for the local date-time as defined by the {@link
   * ZoneRules rules} of the zone ID.
   *
   * In most cases, there is only one valid offset for a local date-time. In the case of an overlap,
   * when clocks are set back, there are two valid offsets. This method uses the earlier offset
   * typically corresponding to "summer".
   *
   * In the case of a gap, when clocks jump forward, there is no valid offset. Instead, the local
   * date-time is adjusted to be later by the length of the gap. For a typical one hour daylight
   * savings change, the local date-time will be moved one hour later into the offset typically
   * corresponding to "summer".
   *
   * @param localDateTime
   *   the local date-time, not null
   * @param zone
   *   the time-zone, not null
   * @return
   *   the zoned date-time, not null
   */
  def of(localDateTime: LocalDateTime, zone: ZoneId): ZonedDateTime =
    ofLocal(localDateTime, zone, null)

  /**
   * Obtains an instance of {@code ZonedDateTime} from a year, month, day, hour, minute, second,
   * nanosecond and time-zone.
   *
   * This creates a zoned date-time matching the local date-time of the seven specified fields as
   * closely as possible. Time-zone rules, such as daylight savings, mean that not every local
   * date-time is valid for the specified zone, thus the local date-time may be adjusted.
   *
   * The local date-time is resolved to a single instant on the time-line. This is achieved by
   * finding a valid offset from UTC/Greenwich for the local date-time as defined by the {@link
   * ZoneRules rules} of the zone ID.
   *
   * In most cases, there is only one valid offset for a local date-time. In the case of an overlap,
   * when clocks are set back, there are two valid offsets. This method uses the earlier offset
   * typically corresponding to "summer".
   *
   * In the case of a gap, when clocks jump forward, there is no valid offset. Instead, the local
   * date-time is adjusted to be later by the length of the gap. For a typical one hour daylight
   * savings change, the local date-time will be moved one hour later into the offset typically
   * corresponding to "summer".
   *
   * This method exists primarily for writing test cases. Non test-code will typically use other
   * methods to create an offset time. {@code LocalDateTime} has five additional convenience
   * variants of the equivalent factory method taking fewer arguments. They are not provided here to
   * reduce the footprint of the API.
   *
   * @param year
   *   the year to represent, from MIN_YEAR to MAX_YEAR
   * @param month
   *   the month-of-year to represent, from 1 (January) to 12 (December)
   * @param dayOfMonth
   *   the day-of-month to represent, from 1 to 31
   * @param hour
   *   the hour-of-day to represent, from 0 to 23
   * @param minute
   *   the minute-of-hour to represent, from 0 to 59
   * @param second
   *   the second-of-minute to represent, from 0 to 59
   * @param nanoOfSecond
   *   the nano-of-second to represent, from 0 to 999,999,999
   * @param zone
   *   the time-zone, not null
   * @return
   *   the offset date-time, not null
   * @throws DateTimeException
   *   if the value of any field is out of range, or if the day-of-month is invalid for the
   *   month-year
   */
  def of(
    year:         Int,
    month:        Int,
    dayOfMonth:   Int,
    hour:         Int,
    minute:       Int,
    second:       Int,
    nanoOfSecond: Int,
    zone:         ZoneId
  ): ZonedDateTime = {
    val dt: LocalDateTime =
      LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond)
    ofLocal(dt, zone, null)
  }

  /**
   * Obtains an instance of {@code ZonedDateTime} from a local date-time using the preferred offset
   * if possible.
   *
   * The local date-time is resolved to a single instant on the time-line. This is achieved by
   * finding a valid offset from UTC/Greenwich for the local date-time as defined by the {@link
   * ZoneRules rules} of the zone ID.
   *
   * In most cases, there is only one valid offset for a local date-time. In the case of an overlap,
   * where clocks are set back, there are two valid offsets. If the preferred offset is one of the
   * valid offsets then it is used. Otherwise the earlier valid offset is used, typically
   * corresponding to "summer".
   *
   * In the case of a gap, where clocks jump forward, there is no valid offset. Instead, the local
   * date-time is adjusted to be later by the length of the gap. For a typical one hour daylight
   * savings change, the local date-time will be moved one hour later into the offset typically
   * corresponding to "summer".
   *
   * @param localDateTime
   *   the local date-time, not null
   * @param zone
   *   the time-zone, not null
   * @param preferredOffset
   *   the zone offset, null if no preference
   * @return
   *   the zoned date-time, not null
   */
  def ofLocal(
    localDateTime:   LocalDateTime,
    zone:            ZoneId,
    preferredOffset: ZoneOffset
  ): ZonedDateTime = {
    Objects.requireNonNull(localDateTime, "localDateTime")
    Objects.requireNonNull(zone, "zone")
    var _localDateTime = localDateTime
    zone match {
      case offset: ZoneOffset => new ZonedDateTime(_localDateTime, offset, zone)
      case _                  =>
        val rules: ZoneRules                         = zone.getRules
        val validOffsets: java.util.List[ZoneOffset] = rules.getValidOffsets(_localDateTime)
        var offset: ZoneOffset                       = null
        if (validOffsets.size == 1)
          offset = validOffsets.get(0)
        else if (validOffsets.size == 0) {
          val trans: ZoneOffsetTransition = rules.getTransition(_localDateTime)
          _localDateTime = _localDateTime.plusSeconds(trans.getDuration.getSeconds)
          offset = trans.getOffsetAfter
        } else if (preferredOffset != null && validOffsets.contains(preferredOffset))
          offset = preferredOffset
        else
          offset = Objects.requireNonNull(validOffsets.get(0), "offset")
        new ZonedDateTime(_localDateTime, offset, zone)
    }
  }

  /**
   * Obtains an instance of {@code ZonedDateTime} from an {@code Instant}.
   *
   * This creates a zoned date-time with the same instant as that specified. Calling {@link
   * #toInstant()} will return an instant equal to the one used here.
   *
   * Converting an instant to a zoned date-time is simple as there is only one valid offset for each
   * instant.
   *
   * @param instant
   *   the instant to create the date-time from, not null
   * @param zone
   *   the time-zone, not null
   * @return
   *   the zoned date-time, not null
   * @throws DateTimeException
   *   if the result exceeds the supported range
   */
  def ofInstant(instant: Instant, zone: ZoneId): ZonedDateTime = {
    Objects.requireNonNull(instant, "instant")
    Objects.requireNonNull(zone, "zone")
    create(instant.getEpochSecond, instant.getNano, zone)
  }

  /**
   * Obtains an instance of {@code ZonedDateTime} from the instant formed by combining the local
   * date-time and offset.
   *
   * This creates a zoned date-time by {@link LocalDateTime#toInstant(ZoneOffset) combining} the
   * {@code LocalDateTime} and {@code ZoneOffset}. This combination uniquely specifies an instant
   * without ambiguity.
   *
   * Converting an instant to a zoned date-time is simple as there is only one valid offset for each
   * instant. If the valid offset is different to the offset specified, the the date-time and offset
   * of the zoned date-time will differ from those specified.
   *
   * If the {@code ZoneId} to be used is a {@code ZoneOffset}, this method is equivalent to {@link
   * #of(LocalDateTime, ZoneId)}.
   *
   * @param localDateTime
   *   the local date-time, not null
   * @param offset
   *   the zone offset, not null
   * @param zone
   *   the time-zone, not null
   * @return
   *   the zoned date-time, not null
   */
  def ofInstant(localDateTime: LocalDateTime, offset: ZoneOffset, zone: ZoneId): ZonedDateTime = {
    Objects.requireNonNull(localDateTime, "localDateTime")
    Objects.requireNonNull(offset, "offset")
    Objects.requireNonNull(zone, "zone")
    create(localDateTime.toEpochSecond(offset), localDateTime.getNano, zone)
  }

  /**
   * Obtains an instance of {@code ZonedDateTime} using seconds from the epoch of
   * 1970-01-01T00:00:00Z.
   *
   * @param epochSecond
   *   the number of seconds from the epoch of 1970-01-01T00:00:00Z
   * @param nanoOfSecond
   *   the nanosecond within the second, from 0 to 999,999,999
   * @param zone
   *   the time-zone, not null
   * @return
   *   the zoned date-time, not null
   * @throws DateTimeException
   *   if the result exceeds the supported range
   */
  private def create(epochSecond: Long, nanoOfSecond: Int, zone: ZoneId): ZonedDateTime = {
    val rules: ZoneRules   = zone.getRules
    val instant: Instant   = Instant.ofEpochSecond(epochSecond, nanoOfSecond.toLong)
    val offset: ZoneOffset = rules.getOffset(instant)
    new ZonedDateTime(LocalDateTime.ofEpochSecond(epochSecond, nanoOfSecond, offset), offset, zone)
  }

  /**
   * Obtains an instance of {@code ZonedDateTime} strictly validating the combination of local
   * date-time, offset and zone ID.
   *
   * This creates a zoned date-time ensuring that the offset is valid for the local date-time
   * according to the rules of the specified zone. If the offset is invalid, an exception is thrown.
   *
   * @param localDateTime
   *   the local date-time, not null
   * @param offset
   *   the zone offset, not null
   * @param zone
   *   the time-zone, not null
   * @return
   *   the zoned date-time, not null
   */
  def ofStrict(localDateTime: LocalDateTime, offset: ZoneOffset, zone: ZoneId): ZonedDateTime = {
    Objects.requireNonNull(localDateTime, "localDateTime")
    Objects.requireNonNull(offset, "offset")
    Objects.requireNonNull(zone, "zone")
    val rules: ZoneRules = zone.getRules
    if (!rules.isValidOffset(localDateTime, offset)) {
      val trans: ZoneOffsetTransition = rules.getTransition(localDateTime)
      if (trans != null && trans.isGap)
        throw new DateTimeException(
          s"LocalDateTime '$localDateTime' does not exist in zone '$zone' due to a gap in the local time-line, typically caused by daylight savings"
        )
      throw new DateTimeException(
        s"ZoneOffset '$offset' is not valid for LocalDateTime '$localDateTime' in zone '$zone'"
      )
    } else
      new ZonedDateTime(localDateTime, offset, zone)
  }

  /**
   * Obtains an instance of {@code ZonedDateTime} from a temporal object.
   *
   * A {@code TemporalAccessor} represents some form of date and time information. This factory
   * converts the arbitrary temporal object to an instance of {@code ZonedDateTime}.
   *
   * The conversion will first obtain a {@code ZoneId}. It will then try to obtain an instant. If
   * that fails it will try to obtain a local date-time. The zoned date time will either be a
   * combination of {@code ZoneId} and instant, or {@code ZoneId} and local date-time.
   *
   * This method matches the signature of the functional interface {@link TemporalQuery} allowing it
   * to be used in queries via method reference, {@code ZonedDateTime::from}.
   *
   * @param temporal
   *   the temporal object to convert, not null
   * @return
   *   the zoned date-time, not null
   * @throws DateTimeException
   *   if unable to convert to an { @code ZonedDateTime}
   */
  def from(temporal: TemporalAccessor): ZonedDateTime =
    temporal match {
      case time: ZonedDateTime => time
      case _                   =>
        try {
          val zone: ZoneId       = ZoneId.from(temporal)
          if (temporal.isSupported(INSTANT_SECONDS))
            try {
              val epochSecond: Long = temporal.getLong(INSTANT_SECONDS)
              val nanoOfSecond: Int = temporal.get(NANO_OF_SECOND)
              return create(epochSecond, nanoOfSecond, zone)
            } catch {
              case _: DateTimeException =>
            }
          val ldt: LocalDateTime = LocalDateTime.from(temporal)
          of(ldt, zone)
        } catch {
          case _: DateTimeException =>
            throw new DateTimeException(
              s"Unable to obtain ZonedDateTime from TemporalAccessor: $temporal, type ${temporal.getClass.getName}"
            )
        }
    }

  /**
   * Obtains an instance of {@code ZonedDateTime} from a text string such as {@code
   * 2007-12-03T10:15:30+01:00[Europe/Paris]}.
   *
   * The string must represent a valid date-time and is parsed using {@link
   * org.threeten.bp.format.DateTimeFormatter#ISO_ZONED_DATE_TIME}.
   *
   * @param text
   *   the text to parse such as "2007-12-03T10:15:30+01:00[Europe/Paris]", not null
   * @return
   *   the parsed zoned date-time, not null
   * @throws DateTimeParseException
   *   if the text cannot be parsed
   */
  def parse(text: CharSequence): ZonedDateTime = parse(text, DateTimeFormatter.ISO_ZONED_DATE_TIME)

  /**
   * Obtains an instance of {@code ZonedDateTime} from a text string using a specific formatter.
   *
   * The text is parsed using the formatter, returning a date-time.
   *
   * @param text
   *   the text to parse, not null
   * @param formatter
   *   the formatter to use, not null
   * @return
   *   the parsed zoned date-time, not null
   * @throws DateTimeParseException
   *   if the text cannot be parsed
   */
  def parse(text: CharSequence, formatter: DateTimeFormatter): ZonedDateTime = {
    Objects.requireNonNull(formatter, "formatter")
    formatter.parse(text,
                    new TemporalQuery[ZonedDateTime] {
                      override def queryFrom(temporal: TemporalAccessor): ZonedDateTime =
                        ZonedDateTime.from(temporal)
                    }
    )
  }

}

/**
 * A date-time with a time-zone in the ISO-8601 calendar system, such as {@code
 * 2007-12-03T10:15:30+01:00 Europe/Paris}.
 *
 * {@code ZonedDateTime} is an immutable representation of a date-time with a time-zone. This class
 * stores all date and time fields, to a precision of nanoseconds, and a time-zone, with a zone
 * offset used to handle ambiguous local date-times. For example, the value "2nd October 2007 at
 * 13:45.30.123456789 +02:00 in the Europe/Paris time-zone" can be stored in a {@code
 * ZonedDateTime}.
 *
 * This class handles conversion from the local time-line of {@code LocalDateTime} to the instant
 * time-line of {@code Instant}. The difference between the two time-lines is the offset from
 * UTC/Greenwich, represented by a {@code ZoneOffset}.
 *
 * Converting between the two time-lines involves calculating the offset using the {@link ZoneRules
 * rules} accessed from the {@code ZoneId}. Obtaining the offset for an instant is simple, as there
 * is exactly one valid offset for each instant. By contrast, obtaining the offset for a local
 * date-time is not straightforward. There are three cases: <ul> <li>Normal, with one valid offset.
 * For the vast majority of the year, the normal case applies, where there is a single valid offset
 * for the local date-time.</li> <li>Gap, with zero valid offsets. This is when clocks jump forward
 * typically due to the spring daylight savings change from "winter" to "summer". In a gap there are
 * local date-time values with no valid offset.</li> <li>Overlap, with two valid offsets. This is
 * when clocks are set back typically due to the autumn daylight savings change from "summer" to
 * "winter". In an overlap there are local date-time values with two valid offsets.</li> </ul><p>
 *
 * Any method that converts directly or implicitly from a local date-time to an instant by obtaining
 * the offset has the potential to be complicated.
 *
 * For Gaps, the general strategy is that if the local date-time falls in the middle of a Gap, then
 * the resulting zoned date-time will have a local date-time shifted forwards by the length of the
 * Gap, resulting in a date-time in the later offset, typically "summer" time.
 *
 * For Overlaps, the general strategy is that if the local date-time falls in the middle of an
 * Overlap, then the previous offset will be retained. If there is no previous offset, or the
 * previous offset is invalid, then the earlier offset is used, typically "summer" time.. Two
 * additional methods, {@link #withEarlierOffsetAtOverlap()} and {@link
 * #withLaterOffsetAtOverlap()}, help manage the case of an overlap.
 *
 * <h3>Specification for implementors</h3> A {@code ZonedDateTime} holds state equivalent to three
 * separate objects, a {@code LocalDateTime}, a {@code ZoneId} and the resolved {@code ZoneOffset}.
 * The offset and local date-time are used to define an instant when necessary. The zone ID is used
 * to obtain the rules for how and when the offset changes. The offset cannot be freely set, as the
 * zone controls which offsets are valid.
 *
 * This class is immutable and thread-safe.
 *
 * @constructor
 * @param dateTime
 *   the date-time, validated as not null
 * @param offset
 *   the zone offset, validated as not null
 * @param zone
 *   the time-zone, validated as not null
 */
@SerialVersionUID(-6260982410461394882L)
final class ZonedDateTime(
  private val dateTime: LocalDateTime,
  private val offset:   ZoneOffset,
  private val zone:     ZoneId
) extends ChronoZonedDateTime[LocalDate]
    with Temporal
    with Serializable {

  /**
   * Resolves the new local date-time using this zone ID, retaining the offset if possible.
   *
   * @param newDateTime
   *   the new local date-time, not null
   * @return
   *   the zoned date-time, not null
   */
  private def resolveLocal(newDateTime: LocalDateTime): ZonedDateTime =
    ZonedDateTime.ofLocal(newDateTime, zone, offset)

  /**
   * Resolves the new local date-time using the offset to identify the instant.
   *
   * @param newDateTime
   *   the new local date-time, not null
   * @return
   *   the zoned date-time, not null
   */
  private def resolveInstant(newDateTime: LocalDateTime): ZonedDateTime =
    ZonedDateTime.ofInstant(newDateTime, offset, zone)

  /**
   * Resolves the offset into this zoned date-time.
   *
   * This ignores the offset, unless it can be used in an overlap.
   *
   * @param offset
   *   the offset, not null
   * @return
   *   the zoned date-time, not null
   */
  private def resolveOffset(offset: ZoneOffset): ZonedDateTime =
    if (offset != this.offset && zone.getRules.isValidOffset(dateTime, offset))
      new ZonedDateTime(dateTime, offset, zone)
    else this

  /**
   * Checks if the specified field is supported.
   *
   * This checks if this date-time can be queried for the specified field. If false, then calling
   * the {@link #range(TemporalField) range} and {@link #get(TemporalField) get} methods will throw
   * an exception.
   *
   * If the field is a {@link ChronoField} then the query is implemented here. The supported fields
   * are: <ul> <li>{@code NANO_OF_SECOND} <li>{@code NANO_OF_DAY} <li>{@code MICRO_OF_SECOND}
   * <li>{@code MICRO_OF_DAY} <li>{@code MILLI_OF_SECOND} <li>{@code MILLI_OF_DAY} <li>{@code
   * SECOND_OF_MINUTE} <li>{@code SECOND_OF_DAY} <li>{@code MINUTE_OF_HOUR} <li>{@code
   * MINUTE_OF_DAY} <li>{@code HOUR_OF_AMPM} <li>{@code CLOCK_HOUR_OF_AMPM} <li>{@code HOUR_OF_DAY}
   * <li>{@code CLOCK_HOUR_OF_DAY} <li>{@code AMPM_OF_DAY} <li>{@code DAY_OF_WEEK} <li>{@code
   * ALIGNED_DAY_OF_WEEK_IN_MONTH} <li>{@code ALIGNED_DAY_OF_WEEK_IN_YEAR} <li>{@code DAY_OF_MONTH}
   * <li>{@code DAY_OF_YEAR} <li>{@code EPOCH_DAY} <li>{@code ALIGNED_WEEK_OF_MONTH} <li>{@code
   * ALIGNED_WEEK_OF_YEAR} <li>{@code MONTH_OF_YEAR} <li>{@code EPOCH_MONTH} <li>{@code YEAR_OF_ERA}
   * <li>{@code YEAR} <li>{@code ERA} <li>{@code INSTANT_SECONDS} <li>{@code OFFSET_SECONDS} </ul>
   * All other {@code ChronoField} instances will return false.
   *
   * If the field is not a {@code ChronoField}, then the result of this method is obtained by
   * invoking {@code TemporalField.isSupportedBy(TemporalAccessor)} passing {@code this} as the
   * argument. Whether the field is supported is determined by the field.
   *
   * @param field
   *   the field to check, null returns false
   * @return
   *   true if the field is supported on this date-time, false if not
   */
  def isSupported(field: TemporalField): Boolean =
    field match {
      case _: ChronoField => true
      case _              =>
        (field != null && field.isSupportedBy(this))
    }

  def isSupported(unit: TemporalUnit): Boolean =
    unit match {
      case _: ChronoUnit =>
        unit.isDateBased || unit.isTimeBased
      case _             =>
        unit != null && unit.isSupportedBy(this)
    }

  /**
   * Gets the range of valid values for the specified field.
   *
   * The range object expresses the minimum and maximum valid values for a field. This date-time is
   * used to enhance the accuracy of the returned range. If it is not possible to return the range,
   * because the field is not supported or for some other reason, an exception is thrown.
   *
   * If the field is a {@link ChronoField} then the query is implemented here. The {@link
   * #isSupported(TemporalField) supported fields} will return appropriate range instances. All
   * other {@code ChronoField} instances will throw a {@code DateTimeException}.
   *
   * If the field is not a {@code ChronoField}, then the result of this method is obtained by
   * invoking {@code TemporalField.rangeRefinedBy(TemporalAccessor)} passing {@code this} as the
   * argument. Whether the range can be obtained is determined by the field.
   *
   * @param field
   *   the field to query the range for, not null
   * @return
   *   the range of valid values for the field, not null
   * @throws DateTimeException
   *   if the range for the field cannot be obtained
   */
  override def range(field: TemporalField): ValueRange =
    if (field.isInstanceOf[ChronoField])
      if ((field eq INSTANT_SECONDS) || (field eq OFFSET_SECONDS))
        field.range
      else
        dateTime.range(field)
    else
      field.rangeRefinedBy(this)

  /**
   * Gets the value of the specified field from this date-time as an {@code int}.
   *
   * This queries this date-time for the value for the specified field. The returned value will
   * always be within the valid range of values for the field. If it is not possible to return the
   * value, because the field is not supported or for some other reason, an exception is thrown.
   *
   * If the field is a {@link ChronoField} then the query is implemented here. The {@link
   * #isSupported(TemporalField) supported fields} will return valid values based on this date-time,
   * except {@code NANO_OF_DAY}, {@code MICRO_OF_DAY}, {@code EPOCH_DAY}, {@code EPOCH_MONTH} and
   * {@code INSTANT_SECONDS} which are too large to fit in an {@code int} and throw a {@code
   * DateTimeException}. All other {@code ChronoField} instances will throw a {@code
   * DateTimeException}.
   *
   * If the field is not a {@code ChronoField}, then the result of this method is obtained by
   * invoking {@code TemporalField.getFrom(TemporalAccessor)} passing {@code this} as the argument.
   * Whether the value can be obtained, and what the value represents, is determined by the field.
   *
   * @param field
   *   the field to get, not null
   * @return
   *   the value for the field
   * @throws DateTimeException
   *   if a value for the field cannot be obtained
   * @throws ArithmeticException
   *   if numeric overflow occurs
   */
  override def get(field: TemporalField): Int =
    field match {
      case f: ChronoField =>
        f match {
          case INSTANT_SECONDS => throw new DateTimeException(s"Field too large for an int: $field")
          case OFFSET_SECONDS  => getOffset.getTotalSeconds
          case _               => dateTime.get(field)
        }
      case _              =>
        super.get(field)
    }

  /**
   * Gets the value of the specified field from this date-time as a {@code long}.
   *
   * This queries this date-time for the value for the specified field. If it is not possible to
   * return the value, because the field is not supported or for some other reason, an exception is
   * thrown.
   *
   * If the field is a {@link ChronoField} then the query is implemented here. The {@link
   * #isSupported(TemporalField) supported fields} will return valid values based on this date-time.
   * All other {@code ChronoField} instances will throw a {@code DateTimeException}.
   *
   * If the field is not a {@code ChronoField}, then the result of this method is obtained by
   * invoking {@code TemporalField.getFrom(TemporalAccessor)} passing {@code this} as the argument.
   * Whether the value can be obtained, and what the value represents, is determined by the field.
   *
   * @param field
   *   the field to get, not null
   * @return
   *   the value for the field
   * @throws DateTimeException
   *   if a value for the field cannot be obtained
   * @throws ArithmeticException
   *   if numeric overflow occurs
   */
  override def getLong(field: TemporalField): Long =
    field match {
      case f: ChronoField =>
        f match {
          case INSTANT_SECONDS => toEpochSecond
          case OFFSET_SECONDS  => getOffset.getTotalSeconds.toLong
          case _               => dateTime.getLong(field)
        }
      case _              =>
        field.getFrom(this)
    }

  /**
   * Gets the zone offset, such as '+01:00'.
   *
   * This is the offset of the local date-time from UTC/Greenwich.
   *
   * @return
   *   the zone offset, not null
   */
  def getOffset: ZoneOffset = offset

  /**
   * Returns a copy of this date-time changing the zone offset to the earlier of the two valid
   * offsets at a local time-line overlap.
   *
   * This method only has any effect when the local time-line overlaps, such as at an autumn
   * daylight savings cutover. In this scenario, there are two valid offsets for the local
   * date-time. Calling this method will return a zoned date-time with the earlier of the two
   * selected.
   *
   * If this method is called when it is not an overlap, {@code this} is returned.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the earlier offset, not null
   */
  def withEarlierOffsetAtOverlap: ZonedDateTime = {
    val trans: ZoneOffsetTransition = getZone.getRules.getTransition(dateTime)
    if (trans != null && trans.isOverlap) {
      val earlierOffset: ZoneOffset = trans.getOffsetBefore
      if (!(earlierOffset == offset))
        return new ZonedDateTime(dateTime, earlierOffset, zone)
    }
    this
  }

  /**
   * Returns a copy of this date-time changing the zone offset to the later of the two valid offsets
   * at a local time-line overlap.
   *
   * This method only has any effect when the local time-line overlaps, such as at an autumn
   * daylight savings cutover. In this scenario, there are two valid offsets for the local
   * date-time. Calling this method will return a zoned date-time with the later of the two
   * selected.
   *
   * If this method is called when it is not an overlap, {@code this} is returned.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the later offset, not null
   */
  def withLaterOffsetAtOverlap: ZonedDateTime = {
    val trans: ZoneOffsetTransition = getZone.getRules.getTransition(toLocalDateTime)
    if (trans != null) {
      val laterOffset: ZoneOffset = trans.getOffsetAfter
      if (!(laterOffset == offset))
        return new ZonedDateTime(dateTime, laterOffset, zone)
    }
    this
  }

  override def compareTo(other: ChronoZonedDateTime[_]): Int = super.compare(other)

  /**
   * Gets the time-zone, such as 'Europe/Paris'.
   *
   * This returns the zone ID. This identifies the time-zone {@link ZoneRules rules} that determine
   * when and how the offset from UTC/Greenwich changes.
   *
   * The zone ID may be same as the {@link #getOffset() offset}. If this is true, then any future
   * calculations, such as addition or subtraction, have no complex edge cases due to time-zone
   * rules. See also {@link #withFixedOffsetZone()}.
   *
   * @return
   *   the time-zone, not null
   */
  def getZone: ZoneId = zone

  /**
   * Returns a copy of this date-time with a different time-zone, retaining the local date-time if
   * possible.
   *
   * This method changes the time-zone and retains the local date-time. The local date-time is only
   * changed if it is invalid for the new zone, determined using the same approach as {@link
   * #ofLocal(LocalDateTime, ZoneId, ZoneOffset)}.
   *
   * To change the zone and adjust the local date-time, use {@link #withZoneSameInstant(ZoneId)}.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param zone
   *   the time-zone to change to, not null
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the requested zone, not null
   */
  def withZoneSameLocal(zone: ZoneId): ZonedDateTime = {
    Objects.requireNonNull(zone, "zone")
    if (this.zone == zone) this else ZonedDateTime.ofLocal(dateTime, zone, offset)
  }

  /**
   * Returns a copy of this date-time with a different time-zone, retaining the instant.
   *
   * This method changes the time-zone and retains the instant. This normally results in a change to
   * the local date-time.
   *
   * This method is based on retaining the same instant, thus gaps and overlaps in the local
   * time-line have no effect on the result.
   *
   * To change the offset while keeping the local time, use {@link #withZoneSameLocal(ZoneId)}.
   *
   * @param zone
   *   the time-zone to change to, not null
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the requested zone, not null
   * @throws DateTimeException
   *   if the result exceeds the supported date range
   */
  def withZoneSameInstant(zone: ZoneId): ZonedDateTime = {
    Objects.requireNonNull(zone, "zone")
    if (this.zone == zone) this
    else ZonedDateTime.create(dateTime.toEpochSecond(offset), dateTime.getNano, zone)
  }

  /**
   * Returns a copy of this date-time with the zone ID set to the offset.
   *
   * This returns a zoned date-time where the zone ID is the same as {@link #getOffset()}. The local
   * date-time, offset and instant of the result will be the same as in this date-time.
   *
   * Setting the date-time to a fixed single offset means that any future calculations, such as
   * addition or subtraction, have no complex edge cases due to time-zone rules. This might also be
   * useful when sending a zoned date-time across a network, as most protocols, such as ISO-8601,
   * only handle offsets, and not region-based zone IDs.
   *
   * This is equivalent to {@code ZonedDateTime.of(zdt.getDateTime(), zdt.getOffset())}.
   *
   * @return
   *   a { @code ZonedDateTime} with the zone ID set to the offset, not null
   */
  def withFixedOffsetZone: ZonedDateTime =
    if (this.zone == offset) this
    else new ZonedDateTime(dateTime, offset, offset)

  /**
   * Gets the year field.
   *
   * This method returns the primitive {@code int} value for the year.
   *
   * The year returned by this method is proleptic as per {@code get(YEAR)}. To obtain the
   * year-of-era, use {@code get(YEAR_OF_ERA}.
   *
   * @return
   *   the year, from MIN_YEAR to MAX_YEAR
   */
  def getYear: Int = dateTime.getYear

  /**
   * Gets the month-of-year field from 1 to 12.
   *
   * This method returns the month as an {@code int} from 1 to 12. Application code is frequently
   * clearer if the enum {@link Month} is used by calling {@link #getMonth()}.
   *
   * @return
   *   the month-of-year, from 1 to 12
   * @see
   *   #getMonth()
   */
  def getMonthValue: Int = dateTime.getMonthValue

  /**
   * Gets the month-of-year field using the {@code Month} enum.
   *
   * This method returns the enum {@link Month} for the month. This avoids confusion as to what
   * {@code int} values mean. If you need access to the primitive {@code int} value then the enum
   * provides the {@link Month#getValue() int value}.
   *
   * @return
   *   the month-of-year, not null
   * @see
   *   #getMonthValue()
   */
  def getMonth: Month = dateTime.getMonth

  /**
   * Gets the day-of-month field.
   *
   * This method returns the primitive {@code int} value for the day-of-month.
   *
   * @return
   *   the day-of-month, from 1 to 31
   */
  def getDayOfMonth: Int = dateTime.getDayOfMonth

  /**
   * Gets the day-of-year field.
   *
   * This method returns the primitive {@code int} value for the day-of-year.
   *
   * @return
   *   the day-of-year, from 1 to 365, or 366 in a leap year
   */
  def getDayOfYear: Int = dateTime.getDayOfYear

  /**
   * Gets the day-of-week field, which is an enum {@code DayOfWeek}.
   *
   * This method returns the enum {@link DayOfWeek} for the day-of-week. This avoids confusion as to
   * what {@code int} values mean. If you need access to the primitive {@code int} value then the
   * enum provides the {@link DayOfWeek#getValue() int value}.
   *
   * Additional information can be obtained from the {@code DayOfWeek}. This includes textual names
   * of the values.
   *
   * @return
   *   the day-of-week, not null
   */
  def getDayOfWeek: DayOfWeek = dateTime.getDayOfWeek

  /**
   * Gets the hour-of-day field.
   *
   * @return
   *   the hour-of-day, from 0 to 23
   */
  def getHour: Int = dateTime.getHour

  /**
   * Gets the minute-of-hour field.
   *
   * @return
   *   the minute-of-hour, from 0 to 59
   */
  def getMinute: Int = dateTime.getMinute

  /**
   * Gets the second-of-minute field.
   *
   * @return
   *   the second-of-minute, from 0 to 59
   */
  def getSecond: Int = dateTime.getSecond

  /**
   * Gets the nano-of-second field.
   *
   * @return
   *   the nano-of-second, from 0 to 999,999,999
   */
  def getNano: Int = dateTime.getNano

  /**
   * Returns an adjusted copy of this date-time.
   *
   * This returns a new {@code ZonedDateTime}, based on this one, with the date-time adjusted. The
   * adjustment takes place using the specified adjuster strategy object. Read the documentation of
   * the adjuster to understand what adjustment will be made.
   *
   * A simple adjuster might simply set the one of the fields, such as the year field. A more
   * complex adjuster might set the date to the last day of the month. A selection of common
   * adjustments is provided in {@link TemporalAdjusters}. These include finding the "last day of
   * the month" and "next Wednesday". Key date-time classes also implement the {@code
   * TemporalAdjuster} interface, such as {@link Month} and {@link MonthDay}. The adjuster is
   * responsible for handling special cases, such as the varying lengths of month and leap years.
   *
   * For example this code returns a date on the last day of July: <pre> import static
   * org.threeten.bp.Month.*; import static org.threeten.bp.temporal.Adjusters.*;
   *
   * result = zonedDateTime.with(JULY).with(lastDayOfMonth()); </pre>
   *
   * The classes {@link LocalDate} and {@link LocalTime} implement {@code TemporalAdjuster}, thus
   * this method can be used to change the date, time or offset: <pre> result =
   * zonedDateTime.with(date); result = zonedDateTime.with(time); </pre>
   *
   * {@link ZoneOffset} also implements {@code TemporalAdjuster} however it is less likely that
   * setting the offset will have the effect you expect. When an offset is passed in, the local
   * date-time is combined with the new offset to form an {@code Instant}. The instant and original
   * zone are then used to create the result. This algorithm means that it is quite likely that the
   * output has a different offset to the specified offset. It will however work correctly when
   * passing in the offset applicable for the instant of the zoned date-time, and will work
   * correctly if passing one of the two valid offsets during a daylight savings overlap when the
   * same local time occurs twice.
   *
   * The result of this method is obtained by invoking the {@link
   * TemporalAdjuster#adjustInto(Temporal)} method on the specified adjuster passing {@code this} as
   * the argument.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param adjuster
   *   the adjuster to use, not null
   * @return
   *   a { @code ZonedDateTime} based on { @code this} with the adjustment made, not null
   * @throws DateTimeException
   *   if the adjustment cannot be made
   * @throws ArithmeticException
   *   if numeric overflow occurs
   */
  override def `with`(adjuster: TemporalAdjuster): ZonedDateTime =
    adjuster match {
      case date: LocalDate         => resolveLocal(LocalDateTime.of(date, dateTime.toLocalTime))
      case time: LocalTime         => resolveLocal(LocalDateTime.of(dateTime.toLocalDate, time))
      case dateTime: LocalDateTime => resolveLocal(dateTime)
      case instant: Instant        => ZonedDateTime.create(instant.getEpochSecond, instant.getNano, zone)
      case offset: ZoneOffset      => resolveOffset(offset)
      case _                       => adjuster.adjustInto(this).asInstanceOf[ZonedDateTime]
    }

  /**
   * Returns a copy of this date-time with the specified field set to a new value.
   *
   * This returns a {@code ZonedDateTime}, based on this one, with the value for the specified field
   * changed. This can be used to change any supported field, such as the year, month or
   * day-of-month. If it is not possible to set the value, because the field is not supported or for
   * some other reason, an exception is thrown.
   *
   * In some cases, changing the specified field can cause the resulting date-time to become
   * invalid, such as changing the month from 31st January to February would make the day-of-month
   * invalid. In cases like this, the field is responsible for resolving the date. Typically it will
   * choose the previous valid date, which would be the last valid day of February in this example.
   *
   * If the field is a {@link ChronoField} then the adjustment is implemented here.
   *
   * The {@code INSTANT_SECONDS} field will return a date-time with the specified instant. The zone
   * and nano-of-second are unchanged. The result will have an offset derived from the new instant
   * and original zone. If the new instant value is outside the valid range then a {@code
   * DateTimeException} will be thrown.
   *
   * The {@code OFFSET_SECONDS} field will typically be ignored. The offset of a {@code
   * ZonedDateTime} is controlled primarily by the time-zone. As such, changing the offset does not
   * generally make sense, because there is only one valid offset for the local date-time and zone.
   * If the zoned date-time is in a daylight savings overlap, then the offset is used to switch
   * between the two valid offsets. In all other cases, the offset is ignored. If the new offset
   * value is outside the valid range then a {@code DateTimeException} will be thrown.
   *
   * The other {@link #isSupported(TemporalField) supported fields} will behave as per the matching
   * method on {@link LocalDateTime#with(TemporalField, long) LocalDateTime}. The zone is not part
   * of the calculation and will be unchanged. When converting back to {@code ZonedDateTime}, if the
   * local date-time is in an overlap, then the offset will be retained if possible, otherwise the
   * earlier offset will be used. If in a gap, the local date-time will be adjusted forward by the
   * length of the gap.
   *
   * All other {@code ChronoField} instances will throw an {@code UnsupportedTemporalTypeException}.
   *
   * If the field is not a {@code ChronoField}, then the result of this method is obtained by
   * invoking {@code TemporalField.adjustInto(Temporal, long)} passing {@code this} as the argument.
   * In this case, the field determines whether and how to adjust the instant.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param field
   *   the field to set in the result, not null
   * @param newValue
   *   the new value of the field in the result
   * @return
   *   a { @code ZonedDateTime} based on { @code this} with the specified field set, not null
   * @throws DateTimeException
   *   if the field cannot be set
   * @throws UnsupportedTemporalTypeException
   *   if the field is not supported
   * @throws ArithmeticException
   *   if numeric overflow occurs
   */
  def `with`(field: TemporalField, newValue: Long): ZonedDateTime =
    field match {
      case f: ChronoField =>
        f match {
          case INSTANT_SECONDS => ZonedDateTime.create(newValue, getNano, zone)
          case OFFSET_SECONDS  =>
            resolveOffset(ZoneOffset.ofTotalSeconds(f.checkValidIntValue(newValue)))
          case _               => resolveLocal(dateTime.`with`(field, newValue))
        }
      case _              =>
        field.adjustInto(this, newValue)
    }

  /**
   * Returns a copy of this {@code ZonedDateTime} with the year value altered.
   *
   * This operates on the local time-line, {@link LocalDateTime#withYear(int) changing the year} of
   * the local date-time. This is then converted back to a {@code ZonedDateTime}, using the zone ID
   * to obtain the offset.
   *
   * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap, then
   * the offset will be retained if possible, otherwise the earlier offset will be used. If in a
   * gap, the local date-time will be adjusted forward by the length of the gap.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param year
   *   the year to set in the result, from MIN_YEAR to MAX_YEAR
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the requested year, not null
   * @throws DateTimeException
   *   if the year value is invalid
   */
  def withYear(year: Int): ZonedDateTime = resolveLocal(dateTime.withYear(year))

  /**
   * Returns a copy of this {@code ZonedDateTime} with the month-of-year value altered.
   *
   * This operates on the local time-line, {@link LocalDateTime#withMonth(int) changing the month}
   * of the local date-time. This is then converted back to a {@code ZonedDateTime}, using the zone
   * ID to obtain the offset.
   *
   * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap, then
   * the offset will be retained if possible, otherwise the earlier offset will be used. If in a
   * gap, the local date-time will be adjusted forward by the length of the gap.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param month
   *   the month-of-year to set in the result, from 1 (January) to 12 (December)
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the requested month, not null
   * @throws DateTimeException
   *   if the month-of-year value is invalid
   */
  def withMonth(month: Int): ZonedDateTime = resolveLocal(dateTime.withMonth(month))

  /**
   * Returns a copy of this {@code ZonedDateTime} with the day-of-month value altered.
   *
   * This operates on the local time-line, {@link LocalDateTime#withDayOfMonth(int) changing the
   * day-of-month} of the local date-time. This is then converted back to a {@code ZonedDateTime},
   * using the zone ID to obtain the offset.
   *
   * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap, then
   * the offset will be retained if possible, otherwise the earlier offset will be used. If in a
   * gap, the local date-time will be adjusted forward by the length of the gap.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param dayOfMonth
   *   the day-of-month to set in the result, from 1 to 28-31
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the requested day, not null
   * @throws DateTimeException
   *   if the day-of-month value is invalid
   * @throws DateTimeException
   *   if the day-of-month is invalid for the month-year
   */
  def withDayOfMonth(dayOfMonth: Int): ZonedDateTime =
    resolveLocal(dateTime.withDayOfMonth(dayOfMonth))

  /**
   * Returns a copy of this {@code ZonedDateTime} with the day-of-year altered.
   *
   * This operates on the local time-line, {@link LocalDateTime#withDayOfYear(int) changing the
   * day-of-year} of the local date-time. This is then converted back to a {@code ZonedDateTime},
   * using the zone ID to obtain the offset.
   *
   * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap, then
   * the offset will be retained if possible, otherwise the earlier offset will be used. If in a
   * gap, the local date-time will be adjusted forward by the length of the gap.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param dayOfYear
   *   the day-of-year to set in the result, from 1 to 365-366
   * @return
   *   a { @code ZonedDateTime} based on this date with the requested day, not null
   * @throws DateTimeException
   *   if the day-of-year value is invalid
   * @throws DateTimeException
   *   if the day-of-year is invalid for the year
   */
  def withDayOfYear(dayOfYear: Int): ZonedDateTime = resolveLocal(dateTime.withDayOfYear(dayOfYear))

  /**
   * Returns a copy of this {@code ZonedDateTime} with the hour-of-day value altered.
   *
   * This operates on the local time-line, {@link LocalDateTime#withHour(int) changing the time} of
   * the local date-time. This is then converted back to a {@code ZonedDateTime}, using the zone ID
   * to obtain the offset.
   *
   * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap, then
   * the offset will be retained if possible, otherwise the earlier offset will be used. If in a
   * gap, the local date-time will be adjusted forward by the length of the gap.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param hour
   *   the hour-of-day to set in the result, from 0 to 23
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the requested hour, not null
   * @throws DateTimeException
   *   if the hour value is invalid
   */
  def withHour(hour: Int): ZonedDateTime = resolveLocal(dateTime.withHour(hour))

  /**
   * Returns a copy of this {@code ZonedDateTime} with the minute-of-hour value altered.
   *
   * This operates on the local time-line, {@link LocalDateTime#withMinute(int) changing the time}
   * of the local date-time. This is then converted back to a {@code ZonedDateTime}, using the zone
   * ID to obtain the offset.
   *
   * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap, then
   * the offset will be retained if possible, otherwise the earlier offset will be used. If in a
   * gap, the local date-time will be adjusted forward by the length of the gap.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param minute
   *   the minute-of-hour to set in the result, from 0 to 59
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the requested minute, not null
   * @throws DateTimeException
   *   if the minute value is invalid
   */
  def withMinute(minute: Int): ZonedDateTime = resolveLocal(dateTime.withMinute(minute))

  /**
   * Returns a copy of this {@code ZonedDateTime} with the second-of-minute value altered.
   *
   * This operates on the local time-line, {@link LocalDateTime#withSecond(int) changing the time}
   * of the local date-time. This is then converted back to a {@code ZonedDateTime}, using the zone
   * ID to obtain the offset.
   *
   * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap, then
   * the offset will be retained if possible, otherwise the earlier offset will be used. If in a
   * gap, the local date-time will be adjusted forward by the length of the gap.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param second
   *   the second-of-minute to set in the result, from 0 to 59
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the requested second, not null
   * @throws DateTimeException
   *   if the second value is invalid
   */
  def withSecond(second: Int): ZonedDateTime = resolveLocal(dateTime.withSecond(second))

  /**
   * Returns a copy of this {@code ZonedDateTime} with the nano-of-second value altered.
   *
   * This operates on the local time-line, {@link LocalDateTime#withNano(int) changing the time} of
   * the local date-time. This is then converted back to a {@code ZonedDateTime}, using the zone ID
   * to obtain the offset.
   *
   * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap, then
   * the offset will be retained if possible, otherwise the earlier offset will be used. If in a
   * gap, the local date-time will be adjusted forward by the length of the gap.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param nanoOfSecond
   *   the nano-of-second to set in the result, from 0 to 999,999,999
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the requested nanosecond, not null
   * @throws DateTimeException
   *   if the nano value is invalid
   */
  def withNano(nanoOfSecond: Int): ZonedDateTime = resolveLocal(dateTime.withNano(nanoOfSecond))

  /**
   * Returns a copy of this {@code ZonedDateTime} with the time truncated.
   *
   * Truncation returns a copy of the original date-time with fields smaller than the specified unit
   * set to zero. For example, truncating with the {@link ChronoUnit#MINUTES minutes} unit will set
   * the second-of-minute and nano-of-second field to zero.
   *
   * The unit must have a {@linkplain TemporalUnit#getDuration() duration} that divides into the
   * length of a standard day without remainder. This includes all supplied time units on {@link
   * ChronoUnit} and {@link ChronoUnit#DAYS DAYS}. Other units throw an exception.
   *
   * This operates on the local time-line, {@link LocalDateTime#truncatedTo(TemporalUnit)
   * truncating} the underlying local date-time. This is then converted back to a {@code
   * ZonedDateTime}, using the zone ID to obtain the offset.
   *
   * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap, then
   * the offset will be retained if possible, otherwise the earlier offset will be used. If in a
   * gap, the local date-time will be adjusted forward by the length of the gap.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param unit
   *   the unit to truncate to, not null
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the time truncated, not null
   * @throws DateTimeException
   *   if unable to truncate
   */
  def truncatedTo(unit: TemporalUnit): ZonedDateTime = resolveLocal(dateTime.truncatedTo(unit))

  /**
   * Returns a copy of this date-time with the specified period added.
   *
   * This method returns a new date-time based on this time with the specified period added. The
   * amount is typically {@link Period} but may be any other type implementing the {@link
   * TemporalAmount} interface. The calculation is delegated to the specified adjuster, which
   * typically calls back to {@link #plus(long, TemporalUnit)}.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param amount
   *   the amount to add, not null
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the addition made, not null
   * @throws DateTimeException
   *   if the addition cannot be made
   * @throws ArithmeticException
   *   if numeric overflow occurs
   */
  override def plus(amount: TemporalAmount): ZonedDateTime =
    amount.addTo(this).asInstanceOf[ZonedDateTime]

  /**
   * Returns a copy of this date-time with the specified period added.
   *
   * This method returns a new date-time based on this date-time with the specified period added.
   * This can be used to add any period that is defined by a unit, for example to add years, months
   * or days. The unit is responsible for the details of the calculation, including the resolution
   * of any edge cases in the calculation.
   *
   * The calculation for date and time units differ.
   *
   * Date units operate on the local time-line. The period is first added to the local date-time,
   * then converted back to a zoned date-time using the zone ID. The conversion uses {@link
   * #ofLocal(LocalDateTime, ZoneId, ZoneOffset)} with the offset before the addition.
   *
   * Time units operate on the instant time-line. The period is first added to the local date-time,
   * then converted back to a zoned date-time using the zone ID. The conversion uses {@link
   * #ofInstant(LocalDateTime, ZoneOffset, ZoneId)} with the offset before the addition.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param amountToAdd
   *   the amount of the unit to add to the result, may be negative
   * @param unit
   *   the unit of the period to add, not null
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the specified period added, not null
   * @throws DateTimeException
   *   if the unit cannot be added to this type
   */
  def plus(amountToAdd: Long, unit: TemporalUnit): ZonedDateTime =
    if (unit.isInstanceOf[ChronoUnit])
      if (unit.isDateBased) resolveLocal(dateTime.plus(amountToAdd, unit))
      else resolveInstant(dateTime.plus(amountToAdd, unit))
    else
      unit.addTo(this, amountToAdd)

  /**
   * Returns a copy of this {@code ZonedDateTime} with the specified period in years added.
   *
   * This operates on the local time-line, {@link LocalDateTime#plusYears(long) adding years} to the
   * local date-time. This is then converted back to a {@code ZonedDateTime}, using the zone ID to
   * obtain the offset.
   *
   * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap, then
   * the offset will be retained if possible, otherwise the earlier offset will be used. If in a
   * gap, the local date-time will be adjusted forward by the length of the gap.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param years
   *   the years to add, may be negative
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the years added, not null
   * @throws DateTimeException
   *   if the result exceeds the supported date range
   */
  def plusYears(years: Long): ZonedDateTime = resolveLocal(dateTime.plusYears(years))

  /**
   * Returns a copy of this {@code ZonedDateTime} with the specified period in months added.
   *
   * This operates on the local time-line, {@link LocalDateTime#plusMonths(long) adding months} to
   * the local date-time. This is then converted back to a {@code ZonedDateTime}, using the zone ID
   * to obtain the offset.
   *
   * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap, then
   * the offset will be retained if possible, otherwise the earlier offset will be used. If in a
   * gap, the local date-time will be adjusted forward by the length of the gap.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param months
   *   the months to add, may be negative
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the months added, not null
   * @throws DateTimeException
   *   if the result exceeds the supported date range
   */
  def plusMonths(months: Long): ZonedDateTime = resolveLocal(dateTime.plusMonths(months))

  /**
   * Returns a copy of this {@code ZonedDateTime} with the specified period in weeks added.
   *
   * This operates on the local time-line, {@link LocalDateTime#plusWeeks(long) adding weeks} to the
   * local date-time. This is then converted back to a {@code ZonedDateTime}, using the zone ID to
   * obtain the offset.
   *
   * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap, then
   * the offset will be retained if possible, otherwise the earlier offset will be used. If in a
   * gap, the local date-time will be adjusted forward by the length of the gap.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param weeks
   *   the weeks to add, may be negative
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the weeks added, not null
   * @throws DateTimeException
   *   if the result exceeds the supported date range
   */
  def plusWeeks(weeks: Long): ZonedDateTime = resolveLocal(dateTime.plusWeeks(weeks))

  /**
   * Returns a copy of this {@code ZonedDateTime} with the specified period in days added.
   *
   * This operates on the local time-line, {@link LocalDateTime#plusDays(long) adding days} to the
   * local date-time. This is then converted back to a {@code ZonedDateTime}, using the zone ID to
   * obtain the offset.
   *
   * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap, then
   * the offset will be retained if possible, otherwise the earlier offset will be used. If in a
   * gap, the local date-time will be adjusted forward by the length of the gap.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param days
   *   the days to add, may be negative
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the days added, not null
   * @throws DateTimeException
   *   if the result exceeds the supported date range
   */
  def plusDays(days: Long): ZonedDateTime = resolveLocal(dateTime.plusDays(days))

  /**
   * Returns a copy of this {@code ZonedDateTime} with the specified period in hours added.
   *
   * This operates on the instant time-line, such that adding one hour will always be a duration of
   * one hour later. This may cause the local date-time to change by an amount other than one hour.
   * Note that this is a different approach to that used by days, months and years, thus adding one
   * day is not the same as adding 24 hours.
   *
   * For example, consider a time-zone where the spring DST cutover means that the local times 01:00
   * to 01:59 occur twice changing from offset +02:00 to +01:00. <ul> <li>Adding one hour to
   * 00:30+02:00 will result in 01:30+02:00 <li>Adding one hour to 01:30+02:00 will result in
   * 01:30+01:00 <li>Adding one hour to 01:30+01:00 will result in 02:30+01:00 <li>Adding three
   * hours to 00:30+02:00 will result in 02:30+01:00 </ul><p>
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param hours
   *   the hours to add, may be negative
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the hours added, not null
   * @throws DateTimeException
   *   if the result exceeds the supported date range
   */
  def plusHours(hours: Long): ZonedDateTime = resolveInstant(dateTime.plusHours(hours))

  /**
   * Returns a copy of this {@code ZonedDateTime} with the specified period in minutes added.
   *
   * This operates on the instant time-line, such that adding one minute will always be a duration
   * of one minute later. This may cause the local date-time to change by an amount other than one
   * minute. Note that this is a different approach to that used by days, months and years.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param minutes
   *   the minutes to add, may be negative
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the minutes added, not null
   * @throws DateTimeException
   *   if the result exceeds the supported date range
   */
  def plusMinutes(minutes: Long): ZonedDateTime = resolveInstant(dateTime.plusMinutes(minutes))

  /**
   * Returns a copy of this {@code ZonedDateTime} with the specified period in seconds added.
   *
   * This operates on the instant time-line, such that adding one second will always be a duration
   * of one second later. This may cause the local date-time to change by an amount other than one
   * second. Note that this is a different approach to that used by days, months and years.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param seconds
   *   the seconds to add, may be negative
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the seconds added, not null
   * @throws DateTimeException
   *   if the result exceeds the supported date range
   */
  def plusSeconds(seconds: Long): ZonedDateTime = resolveInstant(dateTime.plusSeconds(seconds))

  /**
   * Returns a copy of this {@code ZonedDateTime} with the specified period in nanoseconds added.
   *
   * This operates on the instant time-line, such that adding one nano will always be a duration of
   * one nano later. This may cause the local date-time to change by an amount other than one nano.
   * Note that this is a different approach to that used by days, months and years.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param nanos
   *   the nanos to add, may be negative
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the nanoseconds added, not null
   * @throws DateTimeException
   *   if the result exceeds the supported date range
   */
  def plusNanos(nanos: Long): ZonedDateTime = resolveInstant(dateTime.plusNanos(nanos))

  /**
   * Returns a copy of this date-time with the specified period subtracted.
   *
   * This method returns a new date-time based on this time with the specified period subtracted.
   * The amount is typically {@link Period} but may be any other type implementing the {@link
   * TemporalAmount} interface. The calculation is delegated to the specified adjuster, which
   * typically calls back to {@link #minus(long, TemporalUnit)}.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param amount
   *   the amount to subtract, not null
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the subtraction made, not null
   * @throws DateTimeException
   *   if the subtraction cannot be made
   * @throws ArithmeticException
   *   if numeric overflow occurs
   */
  override def minus(amount: TemporalAmount): ZonedDateTime =
    amount.subtractFrom(this).asInstanceOf[ZonedDateTime]

  /**
   * Returns a copy of this date-time with the specified period subtracted.
   *
   * This method returns a new date-time based on this date-time with the specified period
   * subtracted. This can be used to subtract any period that is defined by a unit, for example to
   * subtract years, months or days. The unit is responsible for the details of the calculation,
   * including the resolution of any edge cases in the calculation.
   *
   * The calculation for date and time units differ.
   *
   * Date units operate on the local time-line. The period is first subtracted from the local
   * date-time, then converted back to a zoned date-time using the zone ID. The conversion uses
   * {@link #ofLocal(LocalDateTime, ZoneId, ZoneOffset)} with the offset before the subtraction.
   *
   * Time units operate on the instant time-line. The period is first subtracted from the local
   * date-time, then converted back to a zoned date-time using the zone ID. The conversion uses
   * {@link #ofInstant(LocalDateTime, ZoneOffset, ZoneId)} with the offset before the subtraction.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param amountToSubtract
   *   the amount of the unit to subtract from the result, may be negative
   * @param unit
   *   the unit of the period to subtract, not null
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the specified period subtracted, not
   *   null
   * @throws DateTimeException
   *   if the unit cannot be added to this type
   */
  override def minus(amountToSubtract: Long, unit: TemporalUnit): ZonedDateTime =
    if (amountToSubtract == Long.MinValue) plus(Long.MaxValue, unit).plus(1, unit)
    else plus(-amountToSubtract, unit)

  /**
   * Returns a copy of this {@code ZonedDateTime} with the specified period in years subtracted.
   *
   * This operates on the local time-line, {@link LocalDateTime#minusYears(long) subtracting years}
   * to the local date-time. This is then converted back to a {@code ZonedDateTime}, using the zone
   * ID to obtain the offset.
   *
   * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap, then
   * the offset will be retained if possible, otherwise the earlier offset will be used. If in a
   * gap, the local date-time will be adjusted forward by the length of the gap.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param years
   *   the years to subtract, may be negative
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the years subtracted, not null
   * @throws DateTimeException
   *   if the result exceeds the supported date range
   */
  def minusYears(years: Long): ZonedDateTime =
    if (years == Long.MinValue) plusYears(Long.MaxValue).plusYears(1)
    else plusYears(-years)

  /**
   * Returns a copy of this {@code ZonedDateTime} with the specified period in months subtracted.
   *
   * This operates on the local time-line, {@link LocalDateTime#minusMonths(long) subtracting
   * months} to the local date-time. This is then converted back to a {@code ZonedDateTime}, using
   * the zone ID to obtain the offset.
   *
   * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap, then
   * the offset will be retained if possible, otherwise the earlier offset will be used. If in a
   * gap, the local date-time will be adjusted forward by the length of the gap.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param months
   *   the months to subtract, may be negative
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the months subtracted, not null
   * @throws DateTimeException
   *   if the result exceeds the supported date range
   */
  def minusMonths(months: Long): ZonedDateTime =
    if (months == Long.MinValue) plusMonths(Long.MaxValue).plusMonths(1)
    else plusMonths(-months)

  /**
   * Returns a copy of this {@code ZonedDateTime} with the specified period in weeks subtracted.
   *
   * This operates on the local time-line, {@link LocalDateTime#minusWeeks(long) subtracting weeks}
   * to the local date-time. This is then converted back to a {@code ZonedDateTime}, using the zone
   * ID to obtain the offset.
   *
   * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap, then
   * the offset will be retained if possible, otherwise the earlier offset will be used. If in a
   * gap, the local date-time will be adjusted forward by the length of the gap.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param weeks
   *   the weeks to subtract, may be negative
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the weeks subtracted, not null
   * @throws DateTimeException
   *   if the result exceeds the supported date range
   */
  def minusWeeks(weeks: Long): ZonedDateTime =
    if (weeks == Long.MinValue) plusWeeks(Long.MaxValue).plusWeeks(1)
    else plusWeeks(-weeks)

  /**
   * Returns a copy of this {@code ZonedDateTime} with the specified period in days subtracted.
   *
   * This operates on the local time-line, {@link LocalDateTime#minusDays(long) subtracting days} to
   * the local date-time. This is then converted back to a {@code ZonedDateTime}, using the zone ID
   * to obtain the offset.
   *
   * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap, then
   * the offset will be retained if possible, otherwise the earlier offset will be used. If in a
   * gap, the local date-time will be adjusted forward by the length of the gap.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param days
   *   the days to subtract, may be negative
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the days subtracted, not null
   * @throws DateTimeException
   *   if the result exceeds the supported date range
   */
  def minusDays(days: Long): ZonedDateTime =
    if (days == Long.MinValue) plusDays(Long.MaxValue).plusDays(1)
    else plusDays(-days)

  /**
   * Returns a copy of this {@code ZonedDateTime} with the specified period in hours subtracted.
   *
   * This operates on the instant time-line, such that subtracting one hour will always be a
   * duration of one hour earlier. This may cause the local date-time to change by an amount other
   * than one hour. Note that this is a different approach to that used by days, months and years,
   * thus subtracting one day is not the same as adding 24 hours.
   *
   * For example, consider a time-zone where the spring DST cutover means that the local times 01:00
   * to 01:59 occur twice changing from offset +02:00 to +01:00. <ul> <li>Subtracting one hour from
   * 02:30+01:00 will result in 01:30+02:00 <li>Subtracting one hour from 01:30+01:00 will result in
   * 01:30+02:00 <li>Subtracting one hour from 01:30+02:00 will result in 00:30+01:00
   * <li>Subtracting three hours from 02:30+01:00 will result in 00:30+02:00 </ul><p>
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param hours
   *   the hours to subtract, may be negative
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the hours subtracted, not null
   * @throws DateTimeException
   *   if the result exceeds the supported date range
   */
  def minusHours(hours: Long): ZonedDateTime =
    if (hours == Long.MinValue) plusHours(Long.MaxValue).plusHours(1)
    else plusHours(-hours)

  /**
   * Returns a copy of this {@code ZonedDateTime} with the specified period in minutes subtracted.
   *
   * This operates on the instant time-line, such that subtracting one minute will always be a
   * duration of one minute earlier. This may cause the local date-time to change by an amount other
   * than one minute. Note that this is a different approach to that used by days, months and years.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param minutes
   *   the minutes to subtract, may be negative
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the minutes subtracted, not null
   * @throws DateTimeException
   *   if the result exceeds the supported date range
   */
  def minusMinutes(minutes: Long): ZonedDateTime =
    if (minutes == Long.MinValue) plusMinutes(Long.MaxValue).plusMinutes(1)
    else plusMinutes(-minutes)

  /**
   * Returns a copy of this {@code ZonedDateTime} with the specified period in seconds subtracted.
   *
   * This operates on the instant time-line, such that subtracting one second will always be a
   * duration of one second earlier. This may cause the local date-time to change by an amount other
   * than one second. Note that this is a different approach to that used by days, months and years.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param seconds
   *   the seconds to subtract, may be negative
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the seconds subtracted, not null
   * @throws DateTimeException
   *   if the result exceeds the supported date range
   */
  def minusSeconds(seconds: Long): ZonedDateTime =
    if (seconds == Long.MinValue) plusSeconds(Long.MaxValue).plusSeconds(1)
    else plusSeconds(-seconds)

  /**
   * Returns a copy of this {@code ZonedDateTime} with the specified period in nanoseconds
   * subtracted.
   *
   * This operates on the instant time-line, such that subtracting one nano will always be a
   * duration of one nano earlier. This may cause the local date-time to change by an amount other
   * than one nano. Note that this is a different approach to that used by days, months and years.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param nanos
   *   the nanos to subtract, may be negative
   * @return
   *   a { @code ZonedDateTime} based on this date-time with the nanoseconds subtracted, not null
   * @throws DateTimeException
   *   if the result exceeds the supported date range
   */
  def minusNanos(nanos: Long): ZonedDateTime =
    if (nanos == Long.MinValue) plusNanos(Long.MaxValue).plusNanos(1)
    else plusNanos(-nanos)

  /**
   * Queries this date-time using the specified query.
   *
   * This queries this date-time using the specified query strategy object. The {@code
   * TemporalQuery} object defines the logic to be used to obtain the result. Read the documentation
   * of the query to understand what the result of this method will be.
   *
   * The result of this method is obtained by invoking the {@link
   * TemporalQuery#queryFrom(TemporalAccessor)} method on the specified query passing {@code this}
   * as the argument.
   *
   * @tparam R
   *   the type of the result
   * @param query
   *   the query to invoke, not null
   * @return
   *   the query result, null may be returned (defined by the query)
   * @throws DateTimeException
   *   if unable to query (defined by the query)
   * @throws ArithmeticException
   *   if numeric overflow occurs (defined by the query)
   */
  override def query[R](query: TemporalQuery[R]): R =
    if (query eq TemporalQueries.localDate) toLocalDate.asInstanceOf[R]
    else super.query(query)

  /**
   * Calculates the period between this date-time and another date-time in terms of the specified
   * unit.
   *
   * This calculates the period between two date-times in terms of a single unit. The start and end
   * points are {@code this} and the specified date-time. The result will be negative if the end is
   * before the start. For example, the period in days between two date-times can be calculated
   * using {@code startDateTime.until(endDateTime, DAYS)}.
   *
   * The {@code Temporal} passed to this method must be a {@code ZonedDateTime}. If the time-zone
   * differs between the two zoned date-times, the specified end date-time is normalized to have the
   * same zone as this date-time.
   *
   * The calculation returns a whole number, representing the number of complete units between the
   * two date-times. For example, the period in months between 2012-06-15T00:00Z and
   * 2012-08-14T23:59Z will only be one month as it is one minute short of two months.
   *
   * This method operates in association with {@link TemporalUnit#between}. The result of this
   * method is a {@code long} representing the amount of the specified unit. By contrast, the result
   * of {@code between} is an object that can be used directly in addition/subtraction: <pre> long
   * period = start.until(end, MONTHS); // this method dateTime.plus(MONTHS.between(start, end)); //
   * use in plus/minus </pre>
   *
   * The calculation is implemented in this method for {@link ChronoUnit}. The units {@code NANOS},
   * {@code MICROS}, {@code MILLIS}, {@code SECONDS}, {@code MINUTES}, {@code HOURS} and {@code
   * HALF_DAYS}, {@code DAYS}, {@code WEEKS}, {@code MONTHS}, {@code YEARS}, {@code DECADES}, {@code
   * CENTURIES}, {@code MILLENNIA} and {@code ERAS} are supported. Other {@code ChronoUnit} values
   * will throw an exception.
   *
   * The calculation for date and time units differ.
   *
   * Date units operate on the local time-line, using the local date-time. For example, the period
   * from noon on day 1 to noon the following day in days will always be counted as exactly one day,
   * irrespective of whether there was a daylight savings change or not.
   *
   * Time units operate on the instant time-line. The calculation effectively converts both zoned
   * date-times to instants and then calculates the period between the instants. For example, the
   * period from noon on day 1 to noon the following day in hours may be 23, 24 or 25 hours (or some
   * other amount) depending on whether there was a daylight savings change or not.
   *
   * If the unit is not a {@code ChronoUnit}, then the result of this method is obtained by invoking
   * {@code TemporalUnit.between(Temporal, Temporal)} passing {@code this} as the first argument and
   * the input temporal as the second argument.
   *
   * This instance is immutable and unaffected by this method call.
   *
   * @param endExclusive
   *   the end date-time, which is converted to a { @code ZonedDateTime}, not null
   * @param unit
   *   the unit to measure the period in, not null
   * @return
   *   the amount of the period between this date-time and the end date-time
   * @throws DateTimeException
   *   if the period cannot be calculated
   * @throws ArithmeticException
   *   if numeric overflow occurs
   */
  def until(endExclusive: Temporal, unit: TemporalUnit): Long = {
    var end: ZonedDateTime = ZonedDateTime.from(endExclusive)
    if (unit.isInstanceOf[ChronoUnit]) {
      end = end.withZoneSameInstant(zone)
      if (unit.isDateBased)
        dateTime.until(end.dateTime, unit)
      else
        toOffsetDateTime.until(end.toOffsetDateTime, unit)
    } else
      unit.between(this, end)
  }

  /**
   * Gets the {@code LocalDateTime} part of this date-time.
   *
   * This returns a {@code LocalDateTime} with the same year, month, day and time as this date-time.
   *
   * @return
   *   the local date-time part of this date-time, not null
   */
  def toLocalDateTime: LocalDateTime = dateTime

  /**
   * Gets the {@code LocalDate} part of this date-time.
   *
   * This returns a {@code LocalDate} with the same year, month and day as this date-time.
   *
   * @return
   *   the date part of this date-time, not null
   */
  override def toLocalDate: LocalDate = dateTime.toLocalDate

  /**
   * Gets the {@code LocalTime} part of this date-time.
   *
   * This returns a {@code LocalTime} with the same hour, minute, second and nanosecond as this
   * date-time.
   *
   * @return
   *   the time part of this date-time, not null
   */
  override def toLocalTime: LocalTime = dateTime.toLocalTime

  /**
   * Converts this date-time to an {@code OffsetDateTime}.
   *
   * This creates an offset date-time using the local date-time and offset. The zone ID is ignored.
   *
   * @return
   *   an offset date-time representing the same local date-time and offset, not null
   */
  def toOffsetDateTime: OffsetDateTime = OffsetDateTime.of(dateTime, offset)

  /**
   * Checks if this date-time is equal to another date-time.
   *
   * The comparison is based on the offset date-time and the zone. Only objects of type {@code
   * ZonedDateTime} are compared, other types return false.
   *
   * @param obj
   *   the object to check, null returns false
   * @return
   *   true if this is equal to the other date-time
   */
  override def equals(obj: Any): Boolean =
    obj match {
      case other: ZonedDateTime =>
        (this eq other) || ((dateTime == other.dateTime) && (offset == other.offset) && (zone == other.zone))
      case _                    => false
    }

  /**
   * A hash code for this date-time.
   *
   * @return
   *   a suitable hash code
   */
  override def hashCode: Int =
    dateTime.hashCode ^ offset.hashCode ^ Integer.rotateLeft(zone.hashCode, 3)

  /**
   * Outputs this date-time as a {@code String}, such as {@code
   * 2007-12-03T10:15:30+01:00[Europe/Paris]}.
   *
   * The format consists of the {@code LocalDateTime} followed by the {@code ZoneOffset}. If the
   * {@code ZoneId} is not the same as the offset, then the ID is output. The output is compatible
   * with ISO-8601 if the offset and ID are the same.
   *
   * @return
   *   a string representation of this date-time, not null
   */
  override def toString: String = {
    var str: String = dateTime.toString + offset.toString
    if (offset ne zone)
      str += s"[$zone]"
    str
  }

  /**
   * Outputs this date-time as a {@code String} using the formatter.
   *
   * This date will be passed to the formatter {@link DateTimeFormatter#format(TemporalAccessor)
   * print method}.
   *
   * @param formatter
   *   the formatter to use, not null
   * @return
   *   the formatted date-time string, not null
   * @throws DateTimeException
   *   if an error occurs during printing
   */
  override def format(formatter: DateTimeFormatter): String = super.format(formatter)

}
