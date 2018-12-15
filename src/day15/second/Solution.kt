package day15.second


import tools.FinishedSignal
import tools.timeSolution
import java.util.PriorityQueue

/**
 * @author verwoerd
 * @since 15-12-2018
 */
fun main() = timeSolution {

  val startPawns = mutableListOf<Pawn>()
  val startMaze = System.`in`.bufferedReader().useLines { sequence ->
    sequence.mapIndexed { y, line ->
      line.toCharArray().mapIndexed { x, c ->
        when (c) {
          'G' -> {
            startPawns.add(Pawn(PawnType.GOBLIN, HIT_POINTS, ATTACK_POWER, x, y, 0))
            c
          }
          'E' -> {
            startPawns.add(Pawn(PawnType.ELF, HIT_POINTS, ATTACK_POWER, x, y, 0))
            c
          }
          else -> c
        }
      }.toCharArray()
    }.toList()
  }
  val totalElves = startPawns.filter { it.pawnType == PawnType.ELF }.count()
  (4..200).forEach { index ->
    val (alive, turns, health) = doInteration(startMaze.map { it.copyOf() }.toList(), startPawns.map {
      when (it.pawnType) {
        PawnType.ELF -> it.copy(attackPower = index)
        else -> it.copy()
      }
    })
    if (alive == totalElves) {
      println("Found Solution with attackpower $index")
      println("Solution: ${turns * health}  (turns=$turns health=$health)")
      throw FinishedSignal()
    }
  }
}

fun doInteration(maze: List<CharArray>, pawns: List<Pawn>): Triple<Int, Int, Int> {
  val pawnByLocation = Array(maze.size + 1) { Array<Pawn?>(maze.first().size + 1) { null } }
  var elfAlive = 0
  var goblinAlive = 0
  val queue = PriorityQueue<Pawn>(Comparator { pawn1, pawn2 ->
    when {
      pawn1 == null -> 1
      pawn2 == null -> -1
      pawn1.turns < pawn2.turns -> -1
      pawn1.turns > pawn2.turns -> 1
      else -> readOrder.compare(Coordinate(pawn1.x, pawn1.y), Coordinate(pawn2.x, pawn2.y))
    }
  })

  pawns.forEach {
    if (it.pawnType == PawnType.ELF) {
      elfAlive++
    } else {
      goblinAlive++
    }
    pawnByLocation[it.y][it.x] = it
    queue.offer(it)
  }

  while (elfAlive > 0 && goblinAlive > 0) {
    val current = queue.poll()

    val target = current.pawnType.target
    var x = current.x
    var y = current.y
    // Move Phase
    if (maze[y - 1][x] != target && maze[y][x - 1] != target && maze[y][x + 1] != target && maze[y + 1][x] != target) {
      val nextMove = when {
        // can we move?
        maze[y - 1][x] != '.' && maze[y][x - 1] != '.' && maze[y][x + 1] != '.' && maze[y + 1][x] != '.' -> null
        // we got options
        else -> {
          val (closestEnemies, _) = floodSearch(maze, current.x, current.y, target)
          val targetCoordinates = coordinateSequence(x, y).filter { passable(maze, it) }.toList()
          closestEnemies
            .flatMap { coordinate ->
              coordinateSequence(coordinate.x, coordinate.y).filter { passable(maze, it) }.toList()
            }.distinct()
            .map { floodBacktrack(maze, it, targetCoordinates) }
            .flatten()
            .sortedWith(Comparator { coordinate1: Triple<Coordinate, Coordinate, Int>?, coordinate2 ->
              when {
                coordinate1 == null -> 1
                coordinate2 == null -> -1
                else -> {
                  val d = readOrder.compare(coordinate1.second, coordinate2.second)
                  when (d) {
                    0 -> coordinate1.third.compareTo(coordinate2.third)
                    else -> d
                  }
                }
              }
            })
            .minBy { it.third }?.first
        }
      }
      // execute the move
      if (nextMove != null) {
        maze[y][x] = '.'
        maze[nextMove.y][nextMove.x] = current.pawnType.self
        pawnByLocation[y][x] = null
        pawnByLocation[nextMove.y][nextMove.x] = current
        current.x = nextMove.x
        current.y = nextMove.y
        x = nextMove.x
        y = nextMove.y
      }
    }
    // Attack Phase
    val enemy: Pawn? = coordinateSequence(x, y).filter { maze[it.y][it.x] == target }.map { pawnByLocation[it.y][it.x] }
      .sortedWith(readPawnOrder)
      .minBy { it!!.hitPoints }
    if (enemy != null) {
      enemy.hitPoints -= current.attackPower
      if (enemy.hitPoints <= 0) {
        pawnByLocation[enemy.y][enemy.x] = null
        maze[enemy.y][enemy.x] = '.'
        queue.removeIf { it == enemy }
        when {
          enemy.pawnType == PawnType.ELF -> elfAlive--
          else -> goblinAlive--
        }
      }
    }
    current.turns++
    queue.offer(current)
  }
  val turns = requireNotNull(pawns.filter { it.hitPoints > 0 }.map { it.turns }.min())
  val health = pawns.filter { it.hitPoints > 0 }.map { it.hitPoints }.sum()
  return Triple(elfAlive, turns, health)
}


fun printMaze(
  maze: List<CharArray>,
  pawnByLocation: Array<Array<Pawn?>>
             ) {
  maze.forEachIndexed { index, line ->
    line.forEach { print(it) }
    print(" ")
    pawnByLocation[index].filter { it != null }.forEach {
      print("${it?.pawnType?.self}(${it?.hitPoints}), ")
    }
    println()
  }
  println()
}


fun floodBacktrack(
  maze: List<CharArray>,
  coordinate: Coordinate,
  targetCoordinates: List<Coordinate>
                  ): List<Triple<Coordinate, Coordinate, Int>> {
  val results = mutableListOf<Triple<Coordinate, Coordinate, Int>>()
  val seen = mutableSetOf<Coordinate>()
  var nextLoop = mutableSetOf<Coordinate>()
  var currentLoop = mutableSetOf(coordinate)
  var first = true
  var length = 0
  while (results.isEmpty() && !currentLoop.isEmpty()) {
    currentLoop.forEach { current ->
      seen.add(current)
      if (current in targetCoordinates) {
        results.add(Triple(current, coordinate, length))
      } else {
        if (passable(maze, current) || first) {
          first = false
          coordinateSequence(current.x, current.y).filter { !seen.contains(it) }
            .toCollection(nextLoop)
        }
      }
    }
    currentLoop = nextLoop
    nextLoop = mutableSetOf()
    length++
  }
  if (results.isEmpty()) {
    // Unreachable
    return listOf()
  }
  return results
}

fun passable(maze: List<CharArray>, location: Coordinate) = maze[location.y][location.x] == '.'

fun coordinateSequence(x: Int, y: Int) =
  sequenceOf(Coordinate(x, y - 1), Coordinate(x - 1, y), Coordinate(x + 1, y), Coordinate(x, y + 1))

val readOrder = Comparator { coordinate1: Coordinate?, coordinate2 ->
  when {
    coordinate1 == null -> 1
    coordinate2 == null -> -1
    coordinate1.y < coordinate2.y -> -1
    coordinate1.y > coordinate2.y -> 1
    coordinate1.x < coordinate2.x -> -1
    coordinate1.x > coordinate2.x -> 1
    else -> 0
  }
}

val readPawnOrder = Comparator { coordinate1: Pawn?, coordinate2 ->
  when {
    coordinate1 == null -> 1
    coordinate2 == null -> -1
    coordinate1.y < coordinate2.y -> -1
    coordinate1.y > coordinate2.y -> 1
    coordinate1.x < coordinate2.x -> -1
    coordinate1.x > coordinate2.x -> 1
    else -> 0
  }
}

fun floodSearch(maze: List<CharArray>, x: Int, y: Int, target: Char): Pair<List<Coordinate>, Int> {
  val results = mutableListOf<Coordinate>()
  val seen = mutableSetOf<Coordinate>()
  var nextLoop = mutableSetOf<Coordinate>()
  var currentLoop = mutableSetOf(Coordinate(x, y))
  var first = true
  var length = 0
  while (results.isEmpty() && !currentLoop.isEmpty()) {
    currentLoop.forEach { current ->
      if (maze[current.y][current.x] == target) {
        results.add(current)
      } else {
        seen.add(current)
        if (maze[current.y][current.x] == '.' || first) {
          first = false
          coordinateSequence(current.x, current.y).filter { !seen.contains(it) }
            .toCollection(nextLoop)
        }
      }
    }
    currentLoop = nextLoop
    nextLoop = mutableSetOf()
    length++
  }
  return Pair(results, length)
}

const val HIT_POINTS = 200
const val ATTACK_POWER = 3

data class Coordinate(val x: Int, val y: Int)
enum class PawnType(val target: Char, val self: Char) { ELF('G', 'E'), GOBLIN('E', 'G') }
data class Pawn(
  val pawnType: PawnType,
  var hitPoints: Int,
  var attackPower: Int,
  var x: Int, var y: Int,
  var turns: Int
               )
