package org.threeten.bp.chrono

import java.io.IOException
import java.text.ParseException

object HijrahDateConfigurator {
  @throws[IOException]
  @throws[ParseException]
  private[chrono] def readDeviationConfig(): Unit = {
    // The Javascript side doesn't support the deviation configuration
  }
}
