package demo

import java.time._
import java.time.zone.ZoneRulesProvider
import java.time.format._
// import java.util.Locale

object DemoApp {
  def main(args: Array[String]): Unit = {
    val fixedClock = Clock.fixed(Instant.ofEpochSecond(1234567890L), ZoneOffset.ofHours(0))

    val dateClock = LocalDateTime.now(fixedClock)

    val tomorrow = dateClock.plusDays(1)

    val duration = Duration.between(dateClock, tomorrow)

    Instant.now(fixedClock)
    Instant.parse("2007-12-03T10:15:30.00Z")

    val period = Period.between(dateClock.toLocalDate, tomorrow.toLocalDate)
    assert(period.get(temporal.ChronoUnit.DAYS) == 1L)

    val date1 = LocalDate.of(2001, 1, 31)
    val date2 = LocalDate.of(2001, 2, 28)
    assert(date1.plusMonths(1) == date2)
    val date3 = date1.`with`(temporal.TemporalAdjusters.next(DayOfWeek.SUNDAY))
    val date4 = LocalDate.of(2001, 2, 4)
    assert(date3 == date4)

    val offsetTime = OffsetTime.of(dateClock.toLocalTime, ZoneOffset.ofHours(1))

    println(LocalDateTime.ofInstant(Instant.now, ZoneId.systemDefault).toString())
    println(ZoneRulesProvider.getAvailableZoneIds)
    // Locale.setDefault(Locale.forLanguageTag("fi-FI"))
    val instant   = Instant.ofEpochMilli(0)
    val datetime  = LocalDateTime.ofInstant(Instant.now, ZoneId.of("Europe/Helsinki"))
    val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
    val odt =
      OffsetDateTime.parse("2011-12-03T10:15:30+01:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    val strRepl = odt.format(formatter)

    println(fixedClock)
    println(dateClock)
    println(tomorrow)
    println(duration)
    println(period)
    println(offsetTime)
    println(instant)
    println(datetime)
    println(strRepl)
    println(odt)
  }
}
