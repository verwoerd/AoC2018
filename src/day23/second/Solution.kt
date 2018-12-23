package day23.second

import tools.timeSolution
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.pow

/**
 * @author verwoerd
 * @since 23-12-2018
 */
fun main() = timeSolution {
  val coordinates = System.`in`.bufferedReader().useLines { it.map(::toCoordinate).toList() }
  // binary range search the solution
  var xMin = coordinates.minBy { it.x }!!.x
  var xMax = coordinates.maxBy { it.x }!!.x
  var yMin = coordinates.minBy { it.y }!!.y
  var yMax = coordinates.maxBy { it.y }!!.y
  var zMin = coordinates.minBy { it.z }!!.z
  var zMax = coordinates.maxBy { it.z }!!.z
  // The initial step is the middle of the x-range that is rounded up to a power of 2
  var step = 2.0.pow(ceil(log2((xMax - xMin) /2.0))).toInt()
  var solution = Coordinate(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, 0)
  while (step > 1) {
    var bestInRange = 0
    var best = Coordinate(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, 0)
    var bestDistance = Int.MAX_VALUE
    (xMin..xMax step step).forEach { x ->
      (yMin..yMax step step).forEach { y ->
        (zMin..zMax step step).forEach { z ->
          // find the number of coordinates that lie in range with a margin of step
          val inRange =
            coordinates.filter { (abs(x - it.x) + abs(y - it.y) + abs(z - it.z) - it.radius) < step }.count()
          val coordinate = Coordinate(x, y, z, 0)
          val distance = abs(x) + abs(y) + abs(z)
          when {
            inRange > bestInRange -> {
              bestInRange = inRange
              bestDistance = distance
              best = coordinate
            }
            inRange == bestInRange && distance < bestDistance -> {
              // we found a coordinate closer to base
              bestDistance = distance
              best = coordinate
            }
          }
        }
      }
    }
    xMin = best.x - step
    xMax = best.x + step
    yMin = best.y - step
    yMax = best.y + step
    zMin = best.z - step
    zMax = best.z + step
    step /= 2
    solution = best
  }
  println("Solution: ${abs(solution.x) + abs(solution.y) + abs(solution.z)} ($solution)")
}

val regex = Regex("pos=<(-?\\d+),(-?\\d+),(-?\\d+)>, r=(\\d+)")

data class Coordinate(val x: Int, val y: Int, val z: Int, val radius: Int)

fun toCoordinate(line: String) =
  regex.matchEntire(line)!!.groupValues.let { Coordinate(it[1].toInt(), it[2].toInt(), it[3].toInt(), it[4].toInt()) }
