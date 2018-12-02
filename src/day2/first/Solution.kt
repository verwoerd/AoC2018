package day2.first

import tools.timeSolution

/**
 * @author verwoerd
 * @since 2-12-2018
 */
fun main() = timeSolution {
  System.`in`.bufferedReader().useLines { sequence ->
    val value = sequence.map { s ->
      s.toCharArray().asSequence().groupingBy { it }.eachCount()
    }
      .map { chars -> Pair(chars.entries.any { it.value == 2 }, chars.entries.any { it.value == 3 }) }
      .fold(Pair(0, 0)) { previous, new ->
        Pair(
          previous.first + boolToInt(new.first),
          previous.second + boolToInt(new.second)
            )
      }
    println(value.first * value.second)
  }

}

fun boolToInt(bool: Boolean) = if (bool) 1 else 0
