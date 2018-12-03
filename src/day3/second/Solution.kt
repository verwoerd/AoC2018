package day3.second

import tools.timeSolution

/**
 * @author verwoerd
 * @since 3-12-2018
 */
fun main() = timeSolution {
  val square = Array(1001) { IntArray(1001) { 0 } }
  val safe = mutableSetOf<Int>()
  System.`in`.bufferedReader().useLines { line ->
    line
      .map { fabricClaimFromString(it) }
      .forEach {
        var selfSave = true
        (it.left until it.left + it.width).forEach { x ->
          (it.right until it.right + it.height).forEach { y ->
            val current = square[x][y]
            when {
              current != 0 -> {
                safe.remove(current)
                square[x][y] = -1
                selfSave = false
              }
              else -> square[x][y] = it.id
            }
          } // End Y loop
        }// End X loop
        if (selfSave) safe.add(it.id)
      }
  }
//  square.forEach { inner ->
//    inner.forEach { print(" $it ") }
//    println()
//  }
  println(safe)
}

val regex = Regex("#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)")

data class FabricClaim(val id: Int, val left: Int, val right: Int, val width: Int, val height: Int)

fun fabricClaimFromString(line: String): FabricClaim {
  val result = requireNotNull(regex.find(line)).groupValues
  return FabricClaim(
    id = result[1].toInt(),
    left = result[2].toInt(),
    right = result[3].toInt(),
    width = result[4].toInt(),
    height = result[5].toInt()
                    )
}
