package day5.first

import tools.timeSolution

/**
 * @author verwoerd
 * @since 5-12-2018
 */
fun main() = timeSolution {
  val polymer = requireNotNull(readLine()).toCharArray()
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
  println(polymer.count { it != ' ' })
}

// This runs Significantly slower 1000ms versus 60ms with the code above
fun mainOld() = timeSolution {
  var polymer = requireNotNull(readLine())
  var i = 0
  while (i < polymer.length - 1) {
    when {
      polymer[i] != polymer[i + 1] && polymer[i].toLowerCase() == polymer[i + 1].toLowerCase() -> {
        polymer = polymer.removeRange(i, i + 2) // the runtime of this method is EVIL!!
        if (i > 0) i--
      }
      else -> i++
    }
  }
  println(polymer.length)
}
