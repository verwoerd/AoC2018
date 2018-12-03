package day3.first

import tools.timeSolution

/**
 * @author verwoerd
 * @since 3-12-2018
 */
fun main()  = timeSolution {
  val square = Array(1001) { IntArray(1001) { 0 } }
  System.`in`.bufferedReader().useLines { line ->
    line
      .map { fabricClaimFromString(it) }
      .forEach {
        (it.left until it.left + it.width).forEach { x ->
          (it.right until it.right + it.height).forEach { y ->
            val current = square[x][y]
            when {
              current > 0 -> square[x][y] = -1
              current == 0 -> square[x][y] = it.id
            }
          }
        }
      }
  }
//  square.forEach { inner ->
//    inner.forEach { print(" $it ") }
//    println()
//  }
  println(square.fold(0) { prev, current -> prev + current.filter { it < 0 }.count() })
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
