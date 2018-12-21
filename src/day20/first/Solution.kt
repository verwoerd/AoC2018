package day20.first

import day20.first.Token.ALT_BRANCH
import day20.first.Token.BRANCH
import day20.first.Token.EAST
import day20.first.Token.END_BRANCH
import day20.first.Token.NORTH
import day20.first.Token.SOUTH
import day20.first.Token.WEST
import day20.first.Token.values
import tools.timeSolution
import java.util.LinkedList
import java.util.Stack

/**
 * @author verwoerd
 * @since 20-12-2018
 */
fun main() = timeSolution {
  val routes = readLine()!!.toCharArray()
  // reconstruct maze with an adjacency lists
  val adj = mutableMapOf<Room, MutableSet<Room>>()
  val stack = Stack<Room>()
  var current = Room(0, 0)
  adj[current] = mutableSetOf()
  routes.map(::charToToken)
    .forEach {
      when (it) {
        NORTH, EAST, WEST, SOUTH -> {
          val next = it.nextRoom(current)
          adj.putIfAbsent(next, mutableSetOf())
          adj[current]!!.add(next)
          adj[next]!!.add(current)
          current = next
        }
        BRANCH -> {
          stack.push(current)
        }
        ALT_BRANCH -> {
          current = stack.peek()
        }
        END_BRANCH -> {
          current = stack.pop()
        }
        else -> {}
      }
    }

  //BFS
  val seen = mutableSetOf<Room>()
  val queue = LinkedList<Pair<Room, Int>>()
  queue.add(Pair(Room(0,0), 0))
  var maxLength = 0
  var thousandPlus = 0
  while (!queue.isEmpty()) {
    val (room, length) = queue.poll()
    if (seen.add(room)) {
      adj[room]!!.filter { !seen.contains(it) }.map { Pair(it, length+1) }.toCollection(queue)
      maxLength = Math.max(length, maxLength)
      if (length >= 1000) {
        thousandPlus++
      }
    }

  }
  println("Solution: Found the furthest away room with $maxLength doors")
  println("Solution: Found $thousandPlus rooms passing through at least 1000 doors")
}

data class Room(val x: Int, val y: Int)
enum class Token(val char: Char) {
  NORTH('N'), EAST('E'), WEST('W'), SOUTH('S'), BRANCH('('), ALT_BRANCH('|'), END_BRANCH(')'), START('^'), END('$');

  fun nextRoom(room: Room) = when (this) {
    NORTH -> Room(room.x, room.y - 1)
    EAST -> Room(room.x + 1, room.y)
    WEST -> Room(room.x - 1, room.y)
    SOUTH -> Room(room.x, room.y + 1)
    else -> room
  }
}

fun charToToken(char: Char) = values().first { it.char == char }
