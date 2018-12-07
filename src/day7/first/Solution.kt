package day7.first

import tools.timeSolution

/**
 * @author verwoerd
 * @since 7-12-2018
 */
fun main() = timeSolution {
  val stepsBefore = mutableMapOf<Char, Int>()
  val releases = mutableMapOf<Char, MutableList<Char>>()
  val result = StringBuilder("")
  System.`in`.bufferedReader().useLines { sequence ->
    sequence.map { parseLine(it) }
      .forEach {
        stepsBefore[it.second] = (stepsBefore[it.second] ?: 0) + 1
        stepsBefore[it.first] = stepsBefore[it.first] ?: 0
        if (releases.containsKey(it.first)) {
          releases[it.first]!!.add(it.second)
        } else {
          releases[it.first] = mutableListOf(it.second)
        }
        releases[it.second] = releases[it.second] ?: mutableListOf()
      }
  }
  while (stepsBefore.keys.isNotEmpty()) {
    stepsBefore
      .filterValues { it == 0 }
      .keys.sorted().first()
      // have to do this one at a time since lexographical order or freed tasks
      .also { task ->
        result.append(task)
        stepsBefore.remove(task)
        releases[task]!!.forEach {
          stepsBefore[it] = stepsBefore[it]!! - 1
        }
      }
  }
  println(result.toString())
}

val regex = Regex("Step (\\w) must be finished before step (\\w) can begin.")

fun parseLine(line: String) =
  regex.matchEntire(line).let { Pair(it!!.groupValues[1][0], it.groupValues[2][0]) }
