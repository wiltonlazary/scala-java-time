package org.threeten.bp.format

private[format] object CasePlatformHelper {
  // Scala Native forwards the call to the native API in which toLowerCase is locale-independent.
  def toLocaleIndependentLowerCase(string: String): String = string.toLowerCase
}
