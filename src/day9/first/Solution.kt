package day9.first

import tools.timeSolution
import java.util.LinkedList

/**
 * @author verwoerd
 * @since 9-12-2018
 */
fun main() = timeSolution {
  val game = parseLine(readLine()!!)
  // Using DeQueue to keep track of current, which is always on the head of the queue
  val state = LinkedList<Int>()
  state.add(0)
  val points = LongArray(game.players + 1) { 0L }
  (1..game.marbles).forEach {
    when {
      it % 23 == 0 -> {
        val currentPlayer = it % game.players
        repeat(6) {state.offerFirst(state.pollLast())}
        points[currentPlayer] = points[currentPlayer] + it + state.pollLast()
      }
      else -> {
        repeat(2) { state.offerLast(state.pollFirst()) }
        state.offerFirst(it)
      }
    }
  }
  println("Solution: ${points.max()}")
}

val regex = Regex("(\\d+) players; last marble is worth (\\d+) points")

data class Game(val players: Int, val marbles: Int)

fun parseLine(line: String) =
  regex.matchEntire(line).let { Game(it!!.groupValues[1].toInt(), it.groupValues[2].toInt()) }
