package day17.first

import tools.timeSolution
import java.util.Collections
import java.util.LinkedList

/**
 * @author verwoerd
 * @since 17-12-2018
 */
fun main() = timeSolution {
  val coordinates = System.`in`.bufferedReader().useLines { sequence -> sequence.map { parseLine(it) }.toList() }
  val startX = 500
  val lowestY = coordinates.map { it.yMin }.min()!!
  val highestY = coordinates.map { it.yMax }.max()!!
  // Start coordinate x value should be included
  val lowestX = coordinates.map { it.xMin }.min()!!
  val highestX = coordinates.map { it.xMax }.max()!!
  val xLength = highestX - lowestX + 4 // keep drip space at both edges

  val visualization = Array(highestY - lowestY + 1) { CharArray(xLength + 1) { '.' } }
  coordinates.forEach { rangedCoordinate ->
    (rangedCoordinate.xMin..rangedCoordinate.xMax).forEach { x ->
      (rangedCoordinate.yMin..rangedCoordinate.yMax).forEach { y ->
        visualization[y - lowestY][x - lowestX + 2] = '#'
      }
    }
  }

  val queue = LinkedList(Collections.singleton(Coordinate(startX - lowestX + 2, 0, Direction.DOWN)))
  val reached = mutableSetOf<Coordinate>()
  while (!queue.isEmpty()) {
    val current = queue.pop()
    if (current.y + 1 < 0 || current.y + 1 > (highestY - lowestY)) {
      // mark current coordinate and don't follow up
      reached.add(current.copy(direction = Direction.FILL))
      visualization[current.y][current.x] = '|'
      continue
    }

    if ((current.direction == Direction.DOWN && visualization[current.y + 1][current.x] == '#') || current.direction == Direction.UP) {
      // we need to fill
      val xLeft = getLeftBorder(visualization, current.x - 1, 0, current.y)
      // find the rightmost Wall
      val xRight = getRightBorder(visualization, current.x + 1, xLength, current.y)

      if (visualization[current.y][xLeft - 1] != '*' && visualization[current.y][xRight + 1] != '*') {
        (xLeft..xRight).asSequence().mapTo(reached) {
          visualization[current.y][it] = '~'
          Coordinate(it, current.y, Direction.FILL)
        }
      } else {
        // we don't need no water let the motherfucker burn
        continue
      }


      when {

        blocked(visualization[current.y][xLeft - 1]) && blocked(visualization[current.y][xRight + 1]) -> // we can go up, ignoring check of out of bounds
          if (free(visualization[current.y - 1][current.x])) {
            queue.add(Coordinate(current.x, current.y - 1, Direction.UP))
          }
        else -> {
          if (free(visualization[current.y][xLeft - 1])) {
            queue.add(Coordinate(xLeft, current.y + 1, Direction.DOWN)) // we can go down on the left
            visualization[current.y][xLeft] = '*'
            (xLeft + 1 .. xRight).asSequence().map {
              visualization[current.y][it] = '|'
            }.last()
          }
          if (free(visualization[current.y][xRight + 1])) {
            queue.add(Coordinate(xRight, current.y + 1, Direction.DOWN)) // we down on the right
            visualization[current.y][xRight] = '*'
            (xLeft until  xRight).asSequence().map {
              visualization[current.y][it] = '|'
            }.last()
          }
        }
      }
    } else {
      reached.add(Coordinate(current.x, current.y, Direction.FILL))
      queue.add(Coordinate(current.x, current.y + 1, Direction.DOWN))
      visualization[current.y][current.x] = '|'
    }


  }
    visualization.print()
  println("Found solution: ${reached.size}")



  val resting  = visualization.map{ line -> line.count { it == '~' } }.sum()
  println("Found resting: $resting")
}

private fun Array<CharArray>.print(
  maxY: Int = Int.MAX_VALUE,
  coordinate: Coordinate = Coordinate(-1, -1, Direction.FILL)
                                  ) {
  this.forEachIndexed { y, line ->
    if (maxY > 0 && y <= maxY) {
//      print("$y\t")
      line.forEachIndexed { x, char ->
        when {
          coordinate.x == x && coordinate.y == y -> print(coordinate.direction.self)
          else -> print(char)
        }
      }
      println()
    }
  }
}

fun blocked(char: Char) = char == '#' || char == '~' || char == '*'
fun solidBlocked(char: Char) = char == '#'
fun free(char: Char) = char == '.' || char == '|' || char == '*'
fun passable(char: Char) = char == '.' || char == '|'

fun getLeftBorder(visualization: Array<CharArray>, start: Int, end: Int, y: Int): Int {
  val result = (start downTo end).firstOrNull { solidBlocked(visualization[y][it]) || passable(visualization[y + 1][it]) }
  return when {
    result == null -> 0
    blocked(visualization[y][result]) -> result + 1
    else -> result
  }

}


fun getRightBorder(visualization: Array<CharArray>, start: Int, end: Int, y: Int): Int {
  val result = (start..end).first { solidBlocked(visualization[y][it]) || passable(visualization[y + 1][it]) }
  return when {
    blocked(visualization[y][result]) -> result - 1
    else -> result
  }
}


val regex = Regex("([xy])=(\\d+), ([xy])=(\\d+)..(\\d+)")

data class RangedCoordinate(val xMin: Int, val xMax: Int, val yMin: Int, val yMax: Int)
enum class Direction(val self: Char) { UP('^'), DOWN('v'), FILL('X') }
data class Coordinate(val x: Int, val y: Int, val direction: Direction)

fun parseLine(line: String): RangedCoordinate {
  val result = regex.matchEntire(line)!!.groupValues
  return when (result[1]) {
    "x" -> RangedCoordinate(
      xMin = result[2].toInt(),
      xMax = result[2].toInt(),
      yMin = result[4].toInt(),
      yMax = result[5].toInt()
                           )
    "y" -> RangedCoordinate(
      xMin = result[4].toInt(),
      xMax = result[5].toInt(),
      yMin = result[2].toInt(),
      yMax = result[2].toInt()
                           )
    else -> error("Invalid input line found, $line")
  }
}
