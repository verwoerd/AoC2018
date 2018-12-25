package day25.first

import tools.timeSolution
import kotlin.math.abs

/**
 * @author verwoerd
 * @since 25-12-2018
 */
fun main() = timeSolution {
  val coordinates = System.`in`.bufferedReader().useLines { sequence -> sequence.map(::parseLine).toList() }
  val constellations = DisjointUnionSet(coordinates.size)
  val seen = mutableSetOf<Pair<Coordinate, Int>>()
  coordinates.forEachIndexed { index, coordinate ->
    seen
      .filter { manhattanDistance(it.first, coordinate) <= 3 }
      .forEach{constellations.union(index, it.second)}
    seen.add(Pair(coordinate, index))
  }
  val solution = constellations.count
  println("Solution: $solution")
}

data class Coordinate(val w: Int, val x: Int, val y: Int, val z: Int)

fun manhattanDistance(left: Coordinate, right: Coordinate) =
  abs(left.w - right.w) + abs(left.x - right.x) + abs(left.y - right.y) + abs(left.z - right.z)

fun parseLine(line: String) =
  line.split(",").map { it.trim().toInt() }.chunked(4).map { Coordinate(it[0], it[1], it[2], it[3]) }.first()

class DisjointUnionSet(val size: Int) {
  private val parent = IntArray(size)
  private val rank = ByteArray(size)
  var count = size
    private set

  init {
    parent.indices.forEach { parent[it] = it }
  }

  fun connected(v: Int, w:Int) = find(v) == find(w)

  fun find(v: Int): Int {
    var current = v
    while(parent[current] != current) {
      parent[current] = parent[parent[current]]
      current = parent[current]
    }
    return current
  }

  fun union(v: Int, w: Int) {
    val rootV = find(v)
    val rootW = find(w)
    when {
      rootV == rootW -> return
      rank[rootV] > rank[rootW] -> parent[rootW] = rootV
      rank[rootW] > rank[rootV] -> parent[rootV] = rootW
      else -> {
        parent[rootV] = rootW
        rank[rootW]++
      }
    }
    count --
  }
}
