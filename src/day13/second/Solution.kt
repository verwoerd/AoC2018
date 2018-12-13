package day13.second

import day13.second.Direction.DOWN
import day13.second.Direction.LEFT
import day13.second.Direction.RIGHT
import day13.second.Direction.UP
import tools.timeSolution
import java.util.PriorityQueue

/**
 * @author verwoerd
 * @since 13-12-2018
 */
fun main() = timeSolution {
  val queue = PriorityQueue<Cart>(Comparator { cart1, cart2 ->
    when {
      cart1 == null -> 1
      cart2 == null -> -1
      cart1.moves < cart2.moves -> -1
      cart1.moves > cart2.moves -> 1
      cart1.y < cart2.y -> -1
      cart1.y > cart2.y -> 1
      cart1.x < cart2.x -> -1
      cart1.x > cart2.x -> 1
      else -> 0
    }
  })
  val circuit = System.`in`.bufferedReader().useLines { sequence ->
    sequence.mapIndexed { index, s -> parseLine(index, s) }
      .map {
        queue.addAll(it.second)
        it.first
      }.toList()
  }
  val cartLocations = Array(circuit.maxBy { it.size }!!.size + 1) { BooleanArray(circuit.size+1) { false } }
  queue.forEach { cartLocations[it.x][it.y] = true }
  var lastMoves = 0
  while (queue.size > 1) {
    val currentCart = queue.poll()
    // clear the current position
    cartLocations[currentCart.x][currentCart.y] = false
    val location = circuit[currentCart.y][currentCart.x]
    val newCart = moveCart(currentCart, location)

    if (cartLocations[newCart.x][newCart.y]) {
      queue.removeIf { it.x == newCart.x && it.y == newCart.y }
      lastMoves = newCart.moves
      // Free up the location
      cartLocations[newCart.x][newCart.y] = false
    } else {
      cartLocations[newCart.x][newCart.y] = true
      queue.offer(newCart)
    }
  }
  var lastCart = queue.poll()
  if (lastCart.moves != lastMoves) {
    // we need to make a move for the new cart
    lastCart = moveCart(lastCart, circuit[lastCart.y][lastCart.x])
  }
  println("Solution $lastCart")
}

fun moveCart(currentCart: Cart, location: Char) : Cart {
  val x = currentCart.x
  val y = currentCart.y
  val direction = currentCart.direction
  val nextTurn = currentCart.nextTurn
  val moves = currentCart.moves + 1
  // clear the current position
  return when (location) {
    '-' -> Cart(x + direction.deltaX, y, direction, nextTurn, moves)
    '|' -> Cart(x, y + direction.deltaY, direction, nextTurn, moves)
    '/' -> when (direction) {
      DOWN -> Cart(x - 1, y, LEFT, nextTurn, moves)
      UP -> Cart(x + 1, y, RIGHT, nextTurn, moves)
      LEFT -> Cart(x, y + 1, DOWN, nextTurn, moves)
      RIGHT -> Cart(x, y - 1, UP, nextTurn, moves)
    }
    '\\' -> when (direction) {
      DOWN -> Cart(x + 1, y, RIGHT, nextTurn, moves)
      UP -> Cart(x - 1, y, LEFT, nextTurn, moves)
      LEFT -> Cart(x, y - 1, UP, nextTurn, moves)
      RIGHT -> Cart(x, y + 1, DOWN, nextTurn, moves)
    }
    '+' -> when (nextTurn) {
      TurnDirection.LEFT -> when (direction) {
        DOWN -> Cart(x + 1, y , RIGHT, TurnDirection.STRAIGHT, moves)
        UP -> Cart(x - 1, y, LEFT, TurnDirection.STRAIGHT, moves)
        LEFT -> Cart(x, y + 1, DOWN, TurnDirection.STRAIGHT, moves)
        RIGHT -> Cart(x, y - 1, UP, TurnDirection.STRAIGHT, moves)
      }
      TurnDirection.STRAIGHT -> when (direction) {
        DOWN -> Cart(x, y + direction.deltaY, DOWN, TurnDirection.RIGHT, moves)
        UP -> Cart(x, y + direction.deltaY, UP, TurnDirection.RIGHT, moves)
        LEFT -> Cart(x + direction.deltaX, y, LEFT, TurnDirection.RIGHT, moves)
        RIGHT -> Cart(x + direction.deltaX, y, RIGHT, TurnDirection.RIGHT, moves)
      }
      TurnDirection.RIGHT ->  when (direction) {
        DOWN -> Cart(x - 1, y , LEFT, TurnDirection.LEFT, moves)
        UP -> Cart(x + 1, y, RIGHT, TurnDirection.LEFT, moves)
        LEFT -> Cart(x, y - 1, UP, TurnDirection.LEFT, moves)
        RIGHT -> Cart(x, y + 1, DOWN, TurnDirection.LEFT, moves)
      }
    }
    else -> throw RuntimeException("Invalid location found")
  }
}

fun printCiruit(currentCircuit: List<CharArray>, queue: PriorityQueue<Cart>) {
  // ensure a copy
  val circuit = currentCircuit.map { it.copyOf() }.toList()
  queue.forEach {
    circuit[it.y][it.x] = it.direction.char
  }
  circuit.forEach {line ->
    line.forEach { print(it) }
    println()
  }

}

enum class Direction(val deltaX: Int, val deltaY: Int, val char: Char) {
  UP(0, -1, '^'), DOWN(0, 1, 'v'), LEFT(-1, 0, '<'), RIGHT(1, 0, '>')
}

enum class TurnDirection {
  LEFT, STRAIGHT, RIGHT
}


data class Cart(val x: Int, val y: Int, val direction: Direction, val nextTurn: TurnDirection, val moves: Int)

fun parseLine(y: Int, string: String): Pair<CharArray, List<Cart>> {
  val carts = mutableListOf<Cart>()
  // Note: carts never start on intersections
  val line = string.toCharArray().mapIndexed { x, c ->
    when (c) {
      'v' -> {
        carts.add(Cart(x, y, DOWN, TurnDirection.LEFT, 0))
        '|'
      }
      '^' -> {
        carts.add(Cart(x, y, UP, TurnDirection.LEFT, 0))
        '|'
      }
      '>' -> {
        carts.add(Cart(x, y, RIGHT, TurnDirection.LEFT, 0))
        '-'
      }
      '<' -> {
        carts.add(Cart(x, y, LEFT, TurnDirection.LEFT, 0))
        '-'
      }
      else -> c
    }
  }.toCharArray()
  return Pair(line, carts)
}
