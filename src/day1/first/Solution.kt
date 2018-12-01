package day1.first

import tools.timeSolution
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * @author verwoerd
 * @since 1-12-2018
 */

fun main() = timeSolution {
  System.`in`.bufferedReader()
    .useLines { sequence -> sequence.fold(0L) { prev, incr -> prev + incr.toLong() }.also { println(it) } }
}

fun mainOld() = timeSolution {
  val reader = BufferedReader(InputStreamReader(System.`in`))
  var value = 0L
  var line = reader.readLine()
  do {
    value += line.toLong()
    line = reader.readLine()
  } while (line != null && line != "")
  println(value)
}
