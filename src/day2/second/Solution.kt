package day2.second

import tools.timeSolution

/**
 * @author verwoerd
 * @since 2-12-2018
 */
fun main() = timeSolution {
  val list = System.`in`.bufferedReader().useLines { it.toList() }
  var result = ""
  // Generate all permutations avoiding duplicates by using symmetry
  list.forEachIndexed { index, value ->
    result = list
      .drop(index + 1) // skip already considered pairs
      .map { diff(value, it) } // calculate the diff string
      .filter { it.length > result.length } // only consider strings that are longer that our best answer
      .maxWith(Comparator.comparing(String::length)) ?:  result // find the longest substring or keep the result
  }
  println(result)
}

fun diff(left: CharSequence, right: CharSequence) =
  left.foldIndexed("") { index, prev, char -> prev + if (right[index] == char) char else "" }
