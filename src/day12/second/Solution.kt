package day12.second

import tools.FinishedSignal
import tools.timeSolution
import java.math.BigInteger
import java.math.BigInteger.ONE
import java.math.BigInteger.ZERO

/**
 * Since the first solution has to many array operations, rewrote code for to use bitmask detection. Note that using
 * longs might overflow, therefore BigInteger is used.
 *
 * @author verwoerd
 * @since 12-12-2018
 */

fun main() = timeSolution {
  val initialState = readLine()!!.substring(15)
  val transformMap = mutableMapOf<Long, Long>()
  System.`in`.bufferedReader()
    .useLines { sequence -> sequence.filter { it.isNotEmpty() }.associateTo(transformMap, ::parseLine) }
  var currentLine = ZERO
  initialState.forEach {
    currentLine = (currentLine shl 1) + charToLong(it).toBigInteger()
  }
  var rightIndex = initialState.length - 1
  val mask = 0x1FL.toBigInteger()
  repeat(50000000) { i ->
    repeat(1000) { j ->
      var nextLine = ZERO
      val previous = currentLine
      val previousEnd = rightIndex
      // Ensure three empty pots at the end of the line, based on input.
      while (currentLine and 7L.toBigInteger() != ZERO) {
        currentLine = currentLine shl 1
        rightIndex++
      }
      var index = 0
      while (currentLine != ZERO) {
        val hash = currentLine and mask
        nextLine = (BigInteger.valueOf(transformMap[hash.longValueExact()] ?: 0L) shl index) or nextLine
        index++
        currentLine = currentLine shr 1
      }
      // last two values of right are always discarded
      rightIndex -= 2
      currentLine = nextLine
      // Observed that the results converged to the same pattern, with some minor movement of right index. Once convergence is achieved,
      // the final result calculation is easy.
      if (currentLine == previous) {
        println("Solution converged in iteration ${i + 1 * j + 1}")
        val remainingSteps =
          BigInteger.valueOf(50000000) * BigInteger.valueOf(1000) - (i + 1L).toBigInteger() * (j + 1L).toBigInteger()
        val delta = rightIndex - previousEnd
        println("Current right index: $rightIndex previous right index: $previousEnd delta: $delta")
        val endRight = remainingSteps * delta.toBigInteger() + rightIndex.toBigInteger()
        val result = calculateResult(currentLine, endRight)
        println("Result is $result")
        throw FinishedSignal()
      }
    }
  }
  println("Solution ${calculateResult(currentLine, rightIndex.toBigInteger())}")
}

fun calculateResult(currentLine: BigInteger, rightIndex: BigInteger): BigInteger {
  var finger = currentLine
  var i = rightIndex
  var sum = ZERO
  while (finger > ZERO) {
    sum += (finger and ONE) * i
    i--
    finger = finger shr 1
  }
  return sum
}

fun parseLine(line: String) = Pair(hashString(line.substring(0, 5)), charToLong(line[9]))

fun hashString(line: String) =
  16 * charToLong(line[0]) + 8 * charToLong(line[1]) + 4 * charToLong(line[2]) + 2 * charToLong(line[3]) + charToLong(
    line[4]
                                                                                                                     )

fun charToLong(char: Char) = when (char) {
  '#' -> 1L
  else -> 0L
}

// Debug function for displaying results
fun displayLine(value: BigInteger) {
  val string = StringBuffer()
  var hash = value
  while (hash != ZERO) {
    val char = when (hash and ONE) {
      ONE -> '#'
      else -> '.'
    }
    string.insert(0, char)
    hash = hash shr 1
  }
  println(string.toString())
}
