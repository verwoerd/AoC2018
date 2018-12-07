package day6.first

import tools.timeSolution
import java.lang.Math.pow
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

/**
 * @author verwoerd
 * @since 6-12-2018
 */
fun main() = timeSolution {
  val coordinates = System.`in`.bufferedReader().useLines { seq -> seq.map { toCoordinate(it) }.toList() }
  // Note: Made a wrong assumption first that if a coordinate is not on the convex hull will always be safe, this is not true
  //val edgePoints = convexHull(coordinates)
  val edgePoint = Coordinate(coordinates.maxBy { it.x }!!.x, coordinates.maxBy { it.y }!!.y)
  val distanceMap = Array(edgePoint.x + 1) { Array(edgePoint.y + 1) { Pair(0, 0) } }
  // DP
  coordinates.forEachIndexed { i, coordinate ->
    (0..edgePoint.x).forEach { x ->
      (0..edgePoint.y).forEach { y ->
        val distance = manhattanDistance(coordinate, Coordinate(x, y))
        distanceMap[x][y] = when {
          i == 0 -> Pair(0, distance)
          distanceMap[x][y].second == distance -> Pair(-1, distance)
          distanceMap[x][y].second > distance -> Pair(i, distance)
          else -> distanceMap[x][y]
        }
      }
    }
  }
  val found = distanceMap
    .map { it -> it.filter { it.first != -1 }.groupingBy { it.first }.eachCount() }
    .flatMap { it.entries }
    .groupingBy { it.key }
    .aggregate { _, accumulator: Int?, element, _ -> (accumulator ?: 0) + element.value }
    .filter { entry -> distanceMap[0].all { it.first != entry.key } }
    .filter { entry -> distanceMap[edgePoint.x].all { it.first != entry.key } }
    .filter { entry -> distanceMap.all { it[0].first != entry.key } }
    .filter { entry -> distanceMap.all { it[edgePoint.y].first != entry.key } }
    .maxBy { it.value }

  println("Solution $found")
  println("Coordinate ${coordinates[found!!.key]}")
}

data class Coordinate(val x: Int, val y: Int)

fun toCoordinate(line: String): Coordinate {
  val split = line.split(", ")
  return Coordinate(x = split[0].toInt(), y = split[1].toInt())
}

fun manhattanDistance(a: Coordinate, b: Coordinate) = abs(a.x - b.x) + abs(a.y - b.y)


fun convexHull(input: List<Coordinate>): Pair<Triple<Int, Int, MutableList<Coordinate>>, Int> {
  // all coordinates are unique, so no remove duplicates are added
  val sorted = input.sortedWith(Comparator { o1, o2 ->
    when {
      o1 == null -> Int.MIN_VALUE
      o2 == null -> Int.MAX_VALUE
      o1.x < o2.x -> -1
      o1.x > o2.x -> 1
      o1.y < o2.y -> -1
      o1.y > o2.y -> 1
      else -> 0
    }
  })
  val firstPoint = sorted.first()
  val sortedByAngle = sorted.drop(1).sortedWith(Comparator { o1, o2 ->
    when {
      collinear(firstPoint, o1, o2) -> when {
        distance(firstPoint, o1) <= distance(firstPoint, o2) -> 1
        else -> -1
      }
      counterClockWise(firstPoint, o1, o2) -> 1
      else -> -1
    }
  })
  var result = Triple(0, 0, MutableList(input.size) { it ->
    when (it) {
      0 -> sorted.first()
      1 -> sortedByAngle.first()
      else -> Coordinate(0, 0)
    }
  })
  var top = 1
  // Add safeguard for last point
  val loopMap = sortedByAngle.toMutableList()
  loopMap.add(firstPoint)

  loopMap.forEach { coordinate ->
    if (!result.third.contains(coordinate)) {
      var lastTop: Int
      do {
        lastTop = top
        result = when {
          counterClockWise(result.third[top], result.third[top - 1], coordinate) -> {
            // point is on the edge of the current convex hull
            top++
            updateResult(result, coordinate, top)
          }
          else -> {
            top--
            result
          }
        }
      } while (top <= lastTop)
    }
  }
  return Pair(result, top)
}

const val epsilon = 0.0000001
/**
 * Compute the area of a triangle using lineair algebra by calculating the determinant using the formula of an area of a
 * triangle of points a,b,c as
 * ```
 * | a.x a.y 1 |
 * | b.x b.y 1 |    = 2 * area of the triangle
 * | c.x c.y 1 |
 * ```
 * This number can be negative
 */
fun singedTriangleArea(a: Coordinate, b: Coordinate, c: Coordinate) =
  ((a.x * b.y - a.y * b.x + a.y * c.x - a.x * c.y + b.x * c.y - c.x * b.y) / 2)

/**
 * Checks if point c is to the right of the line ab, e.g. a counter clockwise sweep of 180 degrees will pick up this point.
 */
fun counterClockWise(a: Coordinate, b: Coordinate, c: Coordinate): Boolean =
  singedTriangleArea(a, b, c) > epsilon

fun updateResult(
  result: Triple<Int, Int, MutableList<Coordinate>>,
  coordinate: Coordinate, index: Int
                ): Triple<Int, Int, MutableList<Coordinate>> {
  val ret = Triple(max(result.first, coordinate.x), max(result.second, coordinate.y), result.third)
  ret.third[index] = coordinate
  return ret
}

fun collinear(a: Coordinate, b: Coordinate, c: Coordinate): Boolean = abs(singedTriangleArea(a, b, c)) <= epsilon


fun distance(a: Coordinate, b: Coordinate) =
  sqrt(pow(((b.x - a.x).toDouble()), 2.0) + Math.pow((b.y - a.y).toDouble(), 2.0))


