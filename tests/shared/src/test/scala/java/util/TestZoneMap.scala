package org.threeten.bp

import org.scalatest.funsuite.AnyFunSuite
import org.threeten.bp.Platform.zoneMap
import scala.collection.immutable.TreeMap
import java.util.AbstractMap.SimpleEntry

class TestZoneMap extends AnyFunSuite with AssertionsHelper {
  val m = TreeMap(0 -> "0", 1 -> "1", 3 -> "3", 2 -> "2")
  val r = TreeMap(0 -> "0", 1 -> "1", 3 -> "3", 2 -> "2")(implicitly[Ordering[Int]].reverse)

  test("creation") {
    assertNotNull(zoneMap(m))
    assertEquals(4, zoneMap(m).size)
    val n       = TreeMap(0 -> "0", 1 -> "1", 3 -> "3", 2 -> "2")
    val cleared = zoneMap(n)
    cleared.clear()
    assertEquals(0, cleared.size)
  }

  test("firstKey") {
    assertEquals(0, zoneMap(m).firstKey)
  }

  test("lastKey") {
    assertEquals(3, zoneMap(m).lastKey)
  }

  test("firstEntry") {
    assertEquals(new SimpleEntry(0, "0"), zoneMap(m).firstEntry)
  }

  test("lastEntry") {
    assertEquals(new SimpleEntry(3, "3"), zoneMap(m).lastEntry)
  }

  test("descendingMap") {
    assertEquals(zoneMap(r), zoneMap(m).descendingMap)
  }

  test("lowerKey") {
    assertEquals(0, zoneMap(m).lowerKey(1))
    assertEquals(2, zoneMap(m).lowerKey(3))
    assertNull(zoneMap(m).lowerKey(-1))
  }

  test("lowerEntry") {
    assertEquals(new SimpleEntry(0, "0"), zoneMap(m).lowerEntry(1))
    assertEquals(new SimpleEntry(2, "2"), zoneMap(m).lowerEntry(3))
    assertNull(zoneMap(m).lowerEntry(-1))
  }

  test("higherKey") {
    assertEquals(2, zoneMap(m).higherKey(1))
    assertNull(zoneMap(m).higherKey(3))
    assertEquals(0, zoneMap(m).higherKey(-1))
  }

  test("higherEntry") {
    assertEquals(new SimpleEntry(2, "2"), zoneMap(m).higherEntry(1))
    assertNull(zoneMap(m).higherEntry(3))
    assertEquals(new SimpleEntry(0, "0"), zoneMap(m).higherEntry(-1))
  }

  test("floorKey") {
    assertEquals(1, zoneMap(m).floorKey(1))
    assertEquals(3, zoneMap(m).floorKey(3))
    assertNull(zoneMap(m).floorKey(-1))
  }

  test("floorEntry") {
    assertEquals(new SimpleEntry(1, "1"), zoneMap(m).floorEntry(1))
    assertEquals(new SimpleEntry(3, "3"), zoneMap(m).floorEntry(3))
    assertNull(zoneMap(m).floorEntry(-1))
  }

  test("ceilingKey") {
    assertEquals(1, zoneMap(m).ceilingKey(1))
    assertEquals(3, zoneMap(m).ceilingKey(3))
    assertEquals(0, zoneMap(m).ceilingKey(-1))
    assertNull(zoneMap(m).ceilingKey(4))
  }

  test("ceilingEntry") {
    assertEquals(new SimpleEntry(1, "1"), zoneMap(m).ceilingEntry(1))
    assertEquals(new SimpleEntry(3, "3"), zoneMap(m).ceilingEntry(3))
    assertEquals(new SimpleEntry(0, "0"), zoneMap(m).ceilingEntry(-1))
    assertNull(zoneMap(m).ceilingEntry(4))
  }

  test("pollFirstEntry") {
    val map = zoneMap(m)
    assertEquals(new SimpleEntry(0, "0"), map.pollFirstEntry)
    assertEquals(3, map.size)
  }

  test("pollLastEntry") {
    val map = zoneMap(m)
    assertEquals(new SimpleEntry(3, "3"), map.pollLastEntry)
    assertEquals(3, map.size)
  }

  test("put") {
    val map = zoneMap(m)
    assertEquals("0", map.put(0, "5"))
    assertEquals(4, map.size)
    assertNull(map.put(5, "5"))
    assertEquals(5, map.size)
  }

  test("tailMap") {
    val map = zoneMap(m)
    assertEquals(4, map.tailMap(0).size)
    assertEquals(0, map.tailMap(5).size)
    assertEquals(map, map.tailMap(-1))
  }

  test("tailMapI") {
    val map = zoneMap(m)
    assertEquals(4, map.tailMap(0, true).size)
    assertEquals(3, map.tailMap(0, false).size)
    assertEquals(0, map.tailMap(5, true).size)
    assertEquals(0, map.tailMap(5, false).size)
    assertEquals(map, map.tailMap(-1, true))
    assertEquals(map, map.tailMap(-1, false))
  }

  test("headMap") {
    val map = zoneMap(m)
    assertEquals(0, map.headMap(0).size)
    assertEquals(4, map.headMap(4).size)
    assertEquals(map, map.headMap(6))
  }

  test("headMapI") {
    val map = zoneMap(m)
    assertEquals(0, map.headMap(0, false).size)
    assertEquals(1, map.headMap(0, true).size)
    assertEquals(4, map.headMap(4, false).size)
    assertEquals(4, map.headMap(4, true).size)
    assertEquals(map, map.headMap(6, false))
    assertEquals(map, map.headMap(6, true))
  }

  test("subMap") {
    val map = zoneMap(m)
    assertEquals(4, map.subMap(0, 5).size)
    assertEquals(0, map.subMap(4, 4).size)
    assertEquals(map, map.subMap(0, 6))
  }

}
