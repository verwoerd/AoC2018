package day23.first

import tools.timeSolution
import kotlin.math.abs

/**
 * @author verwoerd
 * @since 23-12-2018
 */
fun main() = timeSolution {
  val coordinates = System.`in`.bufferedReader().useLines { it.map(::toCoordinate).toList() }
  val highestRadius = coordinates.maxBy { it.radius } ?: error("Invalid Input")
  val inRange = coordinates.filter { abs(it.x - highestRadius.x) + abs(it.y - highestRadius.y) + abs(it.z - highestRadius.z) <= highestRadius.radius }
    .count()
  println("Solution $inRange")
}

val regex = Regex("pos=<(-?\\d+),(-?\\d+),(-?\\d+)>, r=(\\d+)")
data class Coordinate(val x:Int, val y: Int, val z: Int, val radius:Int)
fun toCoordinate(line: String) = regex.matchEntire(line)!!.groupValues.let { Coordinate(it[1].toInt(), it[2].toInt(), it[3].toInt(), it[4].toInt()) }


