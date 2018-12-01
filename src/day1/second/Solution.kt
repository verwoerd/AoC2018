package day1.second

import tools.timeSolution
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.LinkedList

/**
 * @author verwoerd
 * @since 1-12-2018
 */
fun main() = timeSolution {
  val order = LinkedList<Long>()
  val seen = mutableSetOf<Long>()
  System.`in`.bufferedReader().useLines { seq ->
     seq.map { it.toLong() }.toCollection(order)
  }
  sequence {
    var current = 0L
    while(true) {
      current = order.fold(current) { previous, increment ->
        yield(previous)
        previous + increment
      }
    }
  }.find { !seen.add(it) }.also { println(it) }
}

// Slightly faster
fun mainOld() = timeSolution {
  val reader = BufferedReader(InputStreamReader(System.`in`))
  val procedure = mutableListOf<Long>()
  var line = reader.readLine()
  do {
    procedure.add(line.toLong())
    line = reader.readLine()
  } while (line != null && line != "")

  val seen = mutableSetOf(0L)
  var current = 0L
  while (true) {
    procedure.forEach {
      current += it
      if (!seen.add(current)) {
        println(current)
        throw RuntimeException()
      }
    }
  }
}
