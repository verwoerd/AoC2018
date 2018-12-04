package day4.first

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
    val mostSleep =
      result.map { entry -> Pair(entry.value!!.first, entry.value!!.second.count { it }) }.groupingBy { it.first }
        .fold(0L) { prev, current -> prev + current.second }.maxBy { it.value }

    println("Found that guard ${mostSleep!!.key} takes the most sleep with  ${mostSleep.value} occurrences")

    val bestTime = result.filter { it.value?.first == mostSleep.key }.map { it.value?.second }
      .map { entry -> entry?.mapIndexed { index, value -> if (value) index else -1 } ?: emptyList() }
      .flatten()
      .filter { it >= 0 }
      .groupingBy { it }.eachCount().maxBy { it.value }

    println("Found that the best time to strike is minute ${bestTime!!.key}")
    println("result: ${mostSleep.key * bestTime.key}")
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
