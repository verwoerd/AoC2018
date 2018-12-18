package day18.first

import day18.first.Node.LUMBER
import day18.first.Node.OPEN
import day18.first.Node.TREE
import tools.timeSolution

/**
 * @author verwoerd
 * @since 18-12-2018
 */
fun main() = timeSolution {
  val field = System.`in`.bufferedReader().useLines { sequence ->
    sequence.map { it.toCharArray().map { charToNode(it) }.toList() }.toList()
  }
  val yMax = field.size
  val xMax = field.first().size
  val iteration = Array(TIME + 1) { Array(yMax) { Array(xMax) { OPEN } } }

  field.forEachIndexed { y, list ->
    list.forEachIndexed { x, node ->
      iteration[0][y][x] = node
    }
  }

  for (i in 1..TIME) {
    (0 until yMax).forEach { y ->
      (0 until xMax).forEach { x ->
        val adj = adjacentNodes(x, y).filter { it.first >= 0 && it.second >= 0 && it.first < xMax && it.second < yMax }
          .map { iteration[i - 1][it.second][it.first] }
        iteration[i][y][x] = when (iteration[i - 1][y][x]) {
          OPEN -> when {
            adj.count { it == TREE } >= 3 -> TREE
            else -> OPEN
          }
          TREE -> when {
            adj.count { it == LUMBER } >= 3 -> LUMBER
            else -> TREE
          }
          LUMBER -> when {
            adj.distinct().filter { it == TREE || it == LUMBER }.count() == 2 -> LUMBER
            else -> OPEN
          }
        }
      }
    }
  }
  val wood = iteration[TIME].fold(0) { acc, line -> acc + line.count { it == TREE } }
  val lumber = iteration[TIME].fold(0) { acc, line -> acc + line.count { it == LUMBER } }
  println("Solution: ${wood * lumber} (wood=$wood, lumber=$lumber)")
}

const val TIME = 10

fun adjacentNodes(x: Int, y: Int) = sequenceOf(
  Pair(x - 1, y - 1),
  Pair(x, y - 1),
  Pair(x + 1, y - 1),
  Pair(x - 1, y),
  Pair(x + 1, y),
  Pair(x - 1, y + 1),
  Pair(x, y + 1),
  Pair(x + 1, y + 1)
                                              )

enum class Node(val char: Char) {
  OPEN('.'), TREE('|'), LUMBER('#')
}

fun charToNode(char: Char) = when (char) {
  '.' -> OPEN
  '|' -> TREE
  '#' -> LUMBER
  else -> error("Invalid field found")
}

