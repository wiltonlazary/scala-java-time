package demo

import java.time.{ Instant, LocalDateTime, OffsetDateTime, ZoneId }
import java.time.zone.ZoneRulesProvider
import java.time.format._
// import java.util.Locale

object DemoApp {
  def main(args: Array[String]): Unit = {
    println(LocalDateTime.ofInstant(Instant.now, ZoneId.systemDefault).toString())
    println(ZoneRulesProvider.getAvailableZoneIds)
    // Locale.setDefault(Locale.forLanguageTag("fi-FI"))
    val instant   = Instant.ofEpochMilli(0)
    val datetime  = LocalDateTime.ofInstant(Instant.now, ZoneId.of("Europe/Helsinki"))
    val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
    val odt =
      OffsetDateTime.parse("2011-12-03T10:15:30+01:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    val strRepl = odt.format(formatter)
    println(instant)
    println(datetime)
    println(strRepl)
    println(odt)
  }
}
