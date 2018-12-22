package day22.second

import day22.second.LocationType.NARROW
import day22.second.LocationType.ROCKY
import day22.second.LocationType.WET
import day22.second.LocationType.values
import day22.second.Tool.CLIMBING
import day22.second.Tool.NEITHER
import day22.second.Tool.TORCH
import tools.timeSolution
import java.util.PriorityQueue

/**
 * @author verwoerd
 * @since 22-12-2018
 */
fun main() = timeSolution {
  val depth = readLine()!!.substring(7).toInt()
  val target = regex.matchEntire(readLine()!!)!!.groupValues.let { Coordinate(it[1].toInt(), it[2].toInt()) }
  // maxim additional path has length 7, using 10 as a safety margin
  val maxY = target.y + 10
  val maxX = target.x + 10
  val indexes = Array(maxY + 1) { LongArray(maxX + 1) { 0L } }
  // precalculate values
  (0..maxY).forEach { y ->
    (0..maxX).forEach { x ->
      indexes[y][x] = erosionLevel(
        when {
          x == 0 && y == 0 -> 0L
          x == target.x && y == target.y -> 0L
          y == 0 -> x * 16807L
          x == 0 -> y * 48271L
          else -> indexes[y][x - 1] * indexes[y - 1][x]
        }, depth
                                  )
    }
  }

  // Dijkstra, each vertex is either move or change tool and move
  val queue = PriorityQueue<Location>(Comparator { o1, o2 -> o1.length.compareTo(o2.length) })
  queue.add(Location(0, Coordinate(0, 0), TORCH))
  // The first to reach the location with a tool is always the shortest
  val seen = mutableSetOf<Pair<Coordinate, Tool>>()
  while (queue.peek().location != target || queue.peek().tool != TORCH) {
    val current = queue.poll()
    when (current.location) {
      target -> queue.offer(Location(current.length + SWAP_TOOL, current.location, TORCH))
      else -> {
        if (!seen.contains(current.toObservation())) {
          seen.add(current.toObservation())
          val currentTerrain = (indexes[current.location.y][current.location.x] % 3).toLocation()
          nearbyCoordinates(current.location)
            .filter { it.x >= 0 && it.y >= 0 && it.x <= maxX && it.y <= maxY }
            .map { Pair(it, (indexes[it.y][it.x] % 3).toLocation()) }
            .flatMap {
              when (currentTerrain) {
                // ensure that equipping in current terrain and moving in the next terrain has valid equipment
                ROCKY -> when (it.second) {
                  ROCKY -> sequenceOf(newLocation(current, CLIMBING, it.first), newLocation(current, TORCH, it.first))
                  WET -> sequenceOf(newLocation(current, CLIMBING, it.first))
                  NARROW -> sequenceOf(newLocation(current, TORCH, it.first))
                }
                WET -> when (it.second) {
                  WET -> sequenceOf(newLocation(current, CLIMBING, it.first), newLocation(current, NEITHER, it.first))
                  ROCKY -> sequenceOf(newLocation(current, CLIMBING, it.first))
                  NARROW -> sequenceOf(newLocation(current, NEITHER, it.first))

                }
                NARROW -> when (it.second) {
                  NARROW -> sequenceOf(newLocation(current, NEITHER, it.first), newLocation(current, TORCH, it.first))
                  ROCKY -> sequenceOf(newLocation(current, TORCH, it.first))
                  WET -> sequenceOf(newLocation(current, NEITHER, it.first))
                }
              }
            }
            .filter { !seen.contains(it.toObservation()) }.toCollection(queue)
        }
      }
    }
  }
  val solution = queue.poll()
  println("Found solution: $solution")
}

fun erosionLevel(long: Long, depth: Int) = (long + depth) % 20183

data class Coordinate(val x: Int, val y: Int)

val regex = Regex("target: (\\d+),(\\d+)")

enum class Tool {
  CLIMBING, TORCH, NEITHER
}

enum class LocationType(val encode: kotlin.Long) {
  ROCKY(0L), WET(1L), NARROW(2L);
}

fun Long.toLocation() = values().first { it.encode == this }

data class Location(val length: Int, val location: Coordinate, val tool: Tool) {
  fun toObservation() = Pair(location, tool)
}

const val SWAP_TOOL = 7
const val MOVE = 1
const val MOVE_SWAP = SWAP_TOOL + MOVE

fun nearbyCoordinates(coordinate: Coordinate) = sequenceOf(
  Coordinate(coordinate.x, coordinate.y - 1),
  Coordinate(coordinate.x - 1, coordinate.y),
  Coordinate(coordinate.x + 1, coordinate.y),
  Coordinate(coordinate.x, coordinate.y + 1)
                                                          )

fun newLocation(current: Location, targetTool: Tool, target: Coordinate) = Location(
  current.length + when (current.tool) {
    targetTool -> MOVE
    else -> MOVE_SWAP
  }, target, targetTool
                                                                                   )
