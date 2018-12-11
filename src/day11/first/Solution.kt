package day11.first

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
  val squares = Array(301) { IntArray(301) { Int.MIN_VALUE } }
  (1..298).forEach { y ->
    (1..298).forEach { x ->
      squares[y][x] = grid[y][x] + grid[y + 1][x] + grid[y + 2][x] +
          grid[y][x + 1] + grid[y + 1][x + 1] + grid[y + 2][x + 1] +
          grid[y][x + 2] + grid[y + 1][x + 2] + grid[y + 2][x + 2]
    }
  }
  val max =
    squares.mapIndexed { y, line -> line.mapIndexed { x, value -> Triple(x, y, value) } }.flatten().maxBy { it.third }
  println("Solution: $max")
}
