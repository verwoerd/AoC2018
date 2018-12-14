package day14.first

import tools.timeSolution

/**
 * @author verwoerd
 * @since 14-12-2018
 */
fun main() = timeSolution {
  val recipes = readLine()!!.toInt()
  val recipeBoard = mutableListOf(3, 7)
  var position1 = 0
  var position2 = 1
  while (recipeBoard.size <= recipes + 10) {
    var newRecipe = recipeBoard[position1] + recipeBoard[position2]
    if (newRecipe > 9) {
      recipeBoard.add(1)
      newRecipe %= 10
    }
    recipeBoard.add(newRecipe)
    position1 += (recipeBoard[position1] + 1)
    position1 %= recipeBoard.size
    position2 += (recipeBoard[position2] + 1)
    position2 %= recipeBoard.size
    if (position1 == position2) {
      position2 += 1
      position2 %= recipeBoard.size
    }
  }
  print("Solution ")
  (recipes until recipes + 10).forEach { print(recipeBoard[it])}
  println()
}
