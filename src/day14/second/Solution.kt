package day14.second

import tools.timeSolution

/**
 * @author verwoerd
 * @since 14-12-2018
 */
fun main() = timeSolution {
  val rawRecipes = readLine()!!
  val size = rawRecipes.length
  val recipes = rawRecipes.toInt()
  val recipeBoard = mutableListOf(3, 7)
  var position1 = 0
  var position2 = 1
  var found = false
  while (!found) {
    var newRecipe = recipeBoard[position1] + recipeBoard[position2]
    if (newRecipe > 9) {
      recipeBoard.add(1)
      newRecipe %= 10
      // Corner case, don't forget to check here
      if (recipeBoard.size > size && recipeBoard.takeLast(size).mapIndexed { index, i ->
        i * Math.pow(10.0, size.toDouble() - index - 1.0)
      }.sum().toInt() == recipes) {
        break
      }
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
    found = recipeBoard.size > size && recipeBoard.takeLast(size).mapIndexed { index, i ->
      i * Math.pow(10.0, size.toDouble() - index - 1.0)
    }.sum().toInt() == recipes
  }
  println("Solution ${recipeBoard.size - size}")
}
