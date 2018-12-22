package day21.first

import tools.timeSolution

/**
 * @author verwoerd
 * @since 21-12-2018
 */


fun main() = timeSolution {
  var value = 0L
  val seen = mutableSetOf<Long>()
  println("Solution First: ${nextValue(value)}")
  do {
    value = nextValue(value)
  } while (seen.add(value))
  println(seen)
  println("Solution Second: ${seen.last()}")
}

fun nextValue(previous: Long): Long {
  val remainder = previous or 65536
  var next = 521363L
  next += remainder and 0xFFL
  next = next and 0xFFFFFFL
  next *= 65899
  next = next and 0xFFFFFFL
  next += (remainder shr 8) and 0xFF
  next = next and 0xFFFFFFL
  next *= 65899
  next = next and 0xFFFFFFL
  next += (remainder shr 16) and 0xFF
  next = next and 0xFFFFFFL
  next *= 65899
  next = next and 0xFFFFFFL
  return next
}
