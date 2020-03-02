package org.threeten.bp

import java.util.NavigableMap
import org.threeten.bp.zone.ZoneMap

object Platform {
  type NPE = scala.scalajs.js.JavaScriptException
  type DFE = scala.scalajs.runtime.UndefinedBehaviorError
  type CCE = scala.scalajs.runtime.UndefinedBehaviorError

  /** Returns `true` if and only if the code is executing on a JVM.
   *  Note: Returns `false` when executing on any JS VM.
   */
  final val executingInJVM = true

  def setupLocales(): Unit = {
  }

  def zoneMap(m: scala.collection.immutable.TreeMap[Int, String]): NavigableMap[Int, String] = ZoneMap(m)
}
