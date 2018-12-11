package day10.first

import tools.timeSolution

/**
 * @author verwoerd
 * @since 10-12-2018
 */
fun main() = timeSolution {
  val observation = System.`in`.bufferedReader().useLines { sequence -> sequence.map { parseLine(it) }.toList() }

  (1..50000).forEach { index ->
    val coordinates = observation.map { Pair(it.x + it.xDir * index, it.y + it.yDir * index) }
    val maxX = coordinates.maxBy { it.first }!!
    val minX = coordinates.minBy { it.first }!!
    val minY = coordinates.minBy { it.second }!!
    val maxY = coordinates.maxBy { it.second }!!
    // Only consider drawing something if it can be something conhesive, this number is trail and error.
    if (maxY.second - minY.second < 50) {
      // Only consider the area where the markers converge
      val sky = Array(maxY.second - minY.second+1) {CharArray(maxX.first- minX.first+1) {'.'}}
      println("Drawing sky $index in area $minX $minY $maxX $maxY")
      coordinates.map { Pair(it.first - minX.first, it.second - minY.second) }.forEach {
        sky[it.second][it.first] = '#' // note X and Y are reversed
      }
      sky.forEach { line->
        line.forEach { print(it) }
        println()
      }
    }

  }
}

val regex = Regex("position=<([- ]?)(\\d+), ([- ]?)(\\d+)> velocity=<([- ]?)(\\d+), ([- ]?)(\\d+)>")

data class Vector(val x: Int, val y: Int, val xDir: Int, val yDir: Int)

fun parseLine(line: String) = regex.matchEntire(line).let {
  Vector(
    x = parseSign(it!!.groupValues[1]) * it.groupValues[2].toInt(),
    y = parseSign(it.groupValues[3]) * it.groupValues[4].toInt(),
    xDir = parseSign(it.groupValues[5]) * it.groupValues[6].toInt(),
    yDir = parseSign(it.groupValues[7]) * it.groupValues[8].toInt()
        )
}

fun parseSign(value: String) = when (value) {
  "-" -> -1
  else -> 1
}
