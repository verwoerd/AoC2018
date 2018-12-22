package day22.first

import tools.timeSolution

/**
 * @author verwoerd
 * @since 22-12-2018
 */
fun main() = timeSolution {
  val depth = readLine()!!.substring(7).toInt()
  val target = regex.matchEntire(readLine()!!)!!.groupValues.let { Coordinate(it[1].toInt(), it[2].toInt()) }
  val indexes = Array(target.y + 1) { LongArray(target.x + 1) { 0L } }

  (0..target.y).forEach { y ->
    (0..target.x).forEach { x ->
      indexes[y][x] = erosionLevel(
        when {
          x == 0 && y == 0 -> 0L
          x == target.x && y == target.y -> 0L
          y == 0 -> x * 16807L
          x == 0 -> y * 48271L
          else -> indexes[y][x - 1] * indexes[y - 1][x]
        }, depth
                                  )
    }
  }
  val riskLevel = indexes.fold(0L) { acc, longs -> acc + longs.map { it % 3 }.sum() }
  println("Solution $riskLevel")
}

fun erosionLevel(long: Long, depth: Int) = (long + depth) % 20183

data class Coordinate(val x: Int, val y: Int)

val regex = Regex("target: (\\d+),(\\d+)")

