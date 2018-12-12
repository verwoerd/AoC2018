package day12.first

import day2.first.boolToInt
import tools.timeSolution

/**
 * @author verwoerd
 * @since 12-12-2018
 */

fun main() = timeSolution {
  val initialState = readLine()!!.substring(15)
  val transformMap = mutableMapOf<String, Char>()
  System.`in`.bufferedReader().useLines { sequence -> sequence.filter { it.isNotEmpty() }.associateTo(transformMap, ::parseLine) }
  val results = mutableListOf(initialState)
  var firstIndex = 0
  repeat(500) { _ ->
    var currentLine = results.last()
    while (!currentLine.startsWith("...")) {
      currentLine = ".$currentLine"
      firstIndex --
    }
    while (!currentLine.endsWith("...")) currentLine = "$currentLine."
    val nextLine = StringBuffer()
    (0 until currentLine.length).forEach {
      val pattern = when(it) {
        0 -> "..${currentLine.substring(it, it + 3)}"
        1 -> ".${currentLine.substring(it - 1, it + 3)}"
        currentLine.length - 2 -> "${currentLine.substring(it - 2, it + 2)}."
        currentLine.length - 1 -> "${currentLine.substring(it - 2, it + 1)}.."
        else -> currentLine.substring(it-2,it+3)
      }
      nextLine.append(transformMap[pattern]?:'.')
    }
    results.add(nextLine.toString())
  }
  val sum = results.last().mapIndexed {index, c -> (firstIndex + index) * boolToInt(c == '#') }.sum()
  println("Solution $sum")
}

fun parseLine(line: String) = Pair(line.substring(0, 5), line[9])
