package day4.second

import tools.timeSolution
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.fail

/**
 * @author verwoerd
 * @since 4-12-2018
 */
fun main() = timeSolution {
  System.`in`.bufferedReader().useLines { sequence ->
    val result = sequence.sorted().map { toGuardRecord(it) }.groupingBy {
      var date = requireNotNull(it.stamp.toLocalDate())
      if (it.stamp.hour != 0) {
        date = date.plusDays(1)
      }
      date
    }.aggregate { _, value: Pair<Int, BooleanArray>?, current, _ ->
      when (current.state) {
        GuardState.START -> Pair(current.id, BooleanArray(60) { false })
        else -> {
          requireNotNull(value)
          requireNotNull(value.first)
          val updatedMinutes = value.second
          (current.stamp.minute until 60).forEach { updatedMinutes[it] = current.state == GuardState.SLEEP }
          Pair(value.first, updatedMinutes)
        }
      }
    }

    val bestPair = result.entries.groupingBy { it.value!!.first }
      .aggregate { _, accumulator: List<Int>?, element, first ->
        val found = if (first || accumulator == null) mutableListOf() else accumulator.toMutableList()
        element.value!!.second.mapIndexed { index, b -> if (b) index else -1 }
          .filter { it != -1 }.toCollection(found)
        found
      }
        // Find for each worker the minute they sleep the most
      .mapValues { entry -> entry.value.groupingBy { it }.eachCount().maxBy { it.value } }
        // Find the worker which has the most sleep minutes
      .maxBy { entry -> entry.value?.value ?: 0 }

    println("Found that Worker ${bestPair!!.key} sleeps the most with  ${bestPair.value!!.value} times at minute ${bestPair.value!!.key}")
    println("Solution: ${bestPair.key * bestPair.value!!.key}")
  }
}

enum class GuardState {
  START, SLEEP, WAKEUP
}

data class GuardRecord(val stamp: LocalDateTime, val id: Int, val state: GuardState)

val startRegex = Regex("\\[(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2})] Guard #(\\d+) begins shift")
val sleepRegex = Regex("\\[(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2})] falls asleep")
val wakeupRegex = Regex("\\[(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2})] wakes up")
val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")!!

fun toGuardRecord(line: String): GuardRecord {
  when {
    startRegex.matches(line) -> {
      val match = requireNotNull(startRegex.find(line))
      return GuardRecord(
        stamp = requireNotNull(LocalDateTime.parse(match.groupValues[1], formatter)),
        id = match.groupValues[2].toInt(),
        state = GuardState.START
                        )
    }
    sleepRegex.matches(line) -> {
      val match = requireNotNull(sleepRegex.find(line))
      return GuardRecord(
        stamp = requireNotNull(LocalDateTime.parse(match.groupValues[1], formatter)),
        id = 0,
        state = GuardState.SLEEP
                        )
    }
    wakeupRegex.matches(line) -> {
      val match = requireNotNull(wakeupRegex.find(line))
      return GuardRecord(
        stamp = requireNotNull(LocalDateTime.parse(match.groupValues[1], formatter)),
        id = 0,
        state = GuardState.WAKEUP
                        )
    }

  }
  fail("No valid log entry")
}
