package day7.second

import tools.timeSolution

/**
 * @author verwoerd
 * @since 7-12-2018
 */
fun main() = timeSolution {
  val stepsBefore = mutableMapOf<Char, Int>()
  val releases = mutableMapOf<Char, MutableList<Char>>()
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
  var total = 0L
  var workers = mutableListOf<Pair<Char, Int>>()

  while (stepsBefore.keys.isNotEmpty()) {
    val freeWorkers = WORKERS - workers.size
    stepsBefore
      // find available jobs
      .filterValues { it == 0 }
      .keys.sorted().take(freeWorkers)
      // assign jobs
      .forEach { task ->
        workers.add(Pair(task, calculateTime(task)))
        // update available work
        stepsBefore.remove(task)
      }
    // find the lowest time and increase time spend by it
    val timeSkip = workers.minBy { it.second }!!
    // update dependency
    releases[timeSkip.first]!!.forEach {
      stepsBefore[it] = stepsBefore[it]!! - 1
    }
    total += timeSkip.second
    // Update time table and free workers
    workers = workers.map { Pair(it.first, it.second - timeSkip.second) }.filter { it.second != 0 }.toMutableList()
  }
  println(total.toString())
}

val regex = Regex("Step (\\w) must be finished before step (\\w) can begin.")

fun parseLine(line: String) =
  regex.matchEntire(line).let { Pair(it!!.groupValues[1][0], it.groupValues[2][0]) }

const val WORKERS = 6
const val BASE_TIME = 60
const val DELTA = 'A'.toInt() - 1

fun calculateTime(task: Char) = BASE_TIME + task.toInt() - DELTA
