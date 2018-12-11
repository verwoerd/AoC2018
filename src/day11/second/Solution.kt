package day11.second

import tools.timeSolution

/**
 * @author verwoerd
 * @since 11-12-2018
 */
fun main() = timeSolution {
  val serial = readLine()!!.toInt()
  val grid = Array(301) { IntArray(301) { 0 } }
  grid.forEachIndexed { y, line ->
    line.forEachIndexed { x, _ ->
      val rackId = x + 10
      val powerLevel = ((rackId * y + serial) * rackId) / 100 % 10 - 5
      grid[y][x] = powerLevel
    }
  }
  val squares = Array(301) { Array(301) { LongArray(301) { Long.MIN_VALUE } } }
  (1..300).forEach { y ->
    (1..300).forEach { x ->
      // Calculating all possible squares, since there is no guarantee after the next square has lower size all other will too.
      (1..(Math.min(300 - x, 300 - y))).forEach { size ->
        squares[y][x][size] = when (size) {
          1 -> grid[y][x].toLong()
          else -> {
            val xSum = (0 until size).map { grid[y + size - 1][x + it] }.sum()
            // Don't count the bottom right corner double
            val ySum = (0 until size - 1).map { grid[y + it][x + size - 1] }.sum()
            squares[y][x][size - 1] + xSum + ySum
          }
        }
      }
    }
  }

  val max =
    squares.mapIndexed { y, line ->
      line.mapIndexed { x, values ->
        values.mapIndexed { size, value ->
          Triple(x, y, Pair(size, value))
        }
      }
    }.flatten().flatten().maxBy { it.third.second }
  println("Solution: $max")
}
