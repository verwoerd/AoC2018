package day5.second

import tools.timeSolution

/**
 * @author verwoerd
 * @since 5-12-2018
 */

fun main() = timeSolution {
  val polymer = requireNotNull(readLine())
  println(('a'..'z')
            .map { polymer.replace(it.toString(), "", true) }
            .map { reaction(it) }
            .min()
         )
}

// Using the old version of solution one ran 22 seconds for all cases
// Optimized algorithm uses 200 ms for all cases
fun reaction(input: String) : Int {
  val polymer = input.toCharArray()
  var i = 0
  var j = 1
  while (i < polymer.size - 1 && j < polymer.size) {
    when {
      polymer[i] == ' ' -> {
        while (i < polymer.size && polymer[i] == ' ') i++
        j = i +1
      }
      polymer[j] == ' ' -> {
        while(j < polymer.size && polymer[j] == ' ') j++
      }
      polymer[i] != polymer[j] && polymer[i].toLowerCase() == polymer[j].toLowerCase() -> {
        polymer[i] = ' '
        polymer[j] = ' '
        while (i > 0 && polymer[i] == ' ') i--
        while (j < polymer.size && polymer[j] == ' ') j++
      }
      else -> {
        i = j
        j++
      }
    }
  }
  return polymer.count { it != ' ' }
}
