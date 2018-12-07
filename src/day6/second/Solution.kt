package day6.second

import tools.timeSolution
import kotlin.math.abs

/**
 * @author verwoerd
 * @since 6-12-2018
 */
fun main() = timeSolution {
  val coordinates = System.`in`.bufferedReader().useLines { seq -> seq.map { toCoordinate(it) }.toList() }
  val edgePoint = Coordinate(coordinates.maxBy { it.x }!!.x, coordinates.maxBy { it.y }!!.y)
  val distanceMap = Array(edgePoint.x + 1) { Array(edgePoint.y + 1) { 0 } }
  // DP
  coordinates.forEach { coordinate ->
    (0..edgePoint.x).forEach { x ->
      (0..edgePoint.y).forEach { y ->
        distanceMap[x][y] = distanceMap[x][y] + manhattanDistance(coordinate, Coordinate(x, y))
      }
    }
  }
  // all values are always connected to their neighbour coordinates(e.g. no gaps in between, so ignoring region detection since its not clear what a region is)
  val total = distanceMap.mapIndexed { x, values ->
    values.mapIndexed { y, distance -> Pair(Coordinate(x, y), distance) }
  }.flatten()
    .filter { it.second < 10000 }
    .count()
  println("Solution: $total")

}

data class Coordinate(val x: Int, val y: Int)

fun toCoordinate(line: String): Coordinate {
  val split = line.split(", ")
  return Coordinate(x = split[0].toInt(), y = split[1].toInt())
}

fun manhattanDistance(a: Coordinate, b: Coordinate) = abs(a.x - b.x) + abs(a.y - b.y)
