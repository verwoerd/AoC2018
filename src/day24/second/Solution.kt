package day24.second

import tools.timeSolution
import java.util.PriorityQueue
import kotlin.math.min

/**
 * @author verwoerd
 * @since 24-12-2018
 */
const val VERBOSE = false

fun main() = timeSolution {
  val groupByName = mutableMapOf<String, Group>()
  val groupsAreImmuneSystem = mapOf(Pair(true, mutableListOf<Group>()), Pair(false, mutableListOf()))
  System.`in`.bufferedReader().useLines { sequence ->
    var immune = true
    var index = 1
    sequence.forEach { line ->
      when (line.trim()) {
        "Immune System:" -> {
          immune = true
          index = 1
        }
        "Infection:" -> {
          immune = false
          index = 1
        }
        "" -> {
        }
        else -> {
          val name = when (immune) {
            true -> "immune"
            else -> "infection"
          } + when {
            index < 10 -> "0"
            else -> ""
          } + index++
          val group = parseLine(name, line, immune)

          groupByName[name] = group
          groupsAreImmuneSystem[immune]!!.add(group)
        }
      }
    }
  }

  // Binary Search
  var distance = 2 shl 16
  var boost = distance - 1
  var solution = Int.MAX_VALUE
  var bestBoost = Int.MAX_VALUE
  while (distance != 0) {
    val newGroupByName = groupByName.mapValues { boostedGroup(it.value, boost) }.toMutableMap()
    val newGroupsAreImmuneSystem = groupsAreImmuneSystem.mapValues { (_, value) ->
      value.map { boostedGroup(it, boost) }.toMutableList()
    }
    val (won, size) = iteration(newGroupsAreImmuneSystem, newGroupByName)
    println("Boost $boost: healty=$won remaining=$size")
    if (won && boost < bestBoost) {
      solution = min(size,solution)
      bestBoost = boost
    }
    distance /=2
    boost += when(won) {
      true -> -distance
      else -> distance
    }
  }
  println("Solution $solution")
}

fun boostedGroup(group: Group, boost: Int) = group.copy(damage = group.damage + when(group.immuneSystem) {
  true -> boost
  else -> 0
})


fun iteration(
  groupsAreImmuneSystem: Map<Boolean, MutableList<Group>>,
  groupByName: MutableMap<String, Group>
             ): Pair<Boolean, Int> {

  val queue =
    PriorityQueue<Triple<Group, Group, Int>>(Comparator { (target1), (target2) -> -target1.initiative.compareTo(target2.initiative) })
  val stuck = mutableSetOf<MutableMap<String, Group>>()
  while (!groupsAreImmuneSystem[true]!!.isEmpty() && !groupsAreImmuneSystem[false]!!.isEmpty() && stuck.add(groupByName)) {
    if (VERBOSE) {
      groupsAreImmuneSystem.printSummary()
      println()
    }
    // target Selection
    // a target can only be attacked once
    val attacked = mutableSetOf<Group>()
    // Infections first, ordered by effective power descending, tiebreaker intiative descending
    groupByName.values.sortedWith(Comparator { group1, group2 ->
      when (effectivePower(group1)) {
        effectivePower(group2) -> -group1.initiative.compareTo(group2.initiative)
        else -> -effectivePower(group1).compareTo(effectivePower(group2))
      }
    })
      .map { group ->
        val power = effectivePower(group)
        val target = groupsAreImmuneSystem[!group.immuneSystem]!!
          .filter { it !in attacked }
          .map {
            val multiplier = damageMultiplier(group, it)
            Triple(group, it, multiplier * power)
          }
          .filter { (_, _, attack) -> attack > 0 }
          .map {
            if (VERBOSE) {
              println("${it.first.name} would deal defending ${it.second.name} ${it.third} damage")
            }
            it
          }
          .sortedWith(Comparator { (_, target1, attack1), (_, target2, attack2) ->
            // Note: reverse order
            when (attack1) {
              attack2 -> when (effectivePower(target1)) {
                effectivePower(target2) -> -target1.initiative.compareTo(target2.initiative)
                else -> -effectivePower(target1).compareTo(effectivePower(target2))
              }
              else -> -attack1.compareTo(attack2)
            }
          }).firstOrNull()
        if (target != null) {
          attacked += target.second
        }
        target
      }.filter { it != null && it.third > 0 }
      .toCollection(queue)
    if (VERBOSE) {
      println()
    }
    // Attack phase
    while (!queue.isEmpty()) {
      var (attacker, defender, _) = queue.poll()
      if (attacker.name !in groupByName || defender.name !in groupByName) {
        // targets already died
        continue
      }
      // get the latest values
      attacker = groupByName[attacker.name]!!
      defender = groupByName[defender.name]!!
      val damage = damageMultiplier(attacker, defender) * effectivePower(attacker)
      val unitsKilled = damage / defender.hitPoints
      groupsAreImmuneSystem[defender.immuneSystem]!!.removeIf { it.name == defender.name }
      if (unitsKilled >= defender.size) {
        // killed all off, removing it
        groupByName.remove(defender.name)
      } else {
        groupsAreImmuneSystem[defender.immuneSystem]!!.add(defender.copy(size = defender.size - unitsKilled))
        groupByName[defender.name] = defender.copy(size = defender.size - unitsKilled)
      }
      if (VERBOSE) {
        println("${attacker.name} attacks ${defender.name}, killing $unitsKilled unit")
      }
    }
    if (VERBOSE) {
      println()
    }
  }
  return Pair(groupsAreImmuneSystem[false]!!.isEmpty(),  groupByName.values.sumBy { it.size })
}

private fun Map<Boolean, List<Group>>.printSummary() {
  println("Immune System:")
  this[true]!!.forEach { println("${it.name} contains ${it.size} units") }
  println("Infection:")
  this[false]!!.forEach { println("${it.name} contains ${it.size} units") }
}

fun effectivePower(group: Group) = group.damage * group.size
fun damageMultiplier(attacker: Group, defender: Group) = when {
  attacker.damageType in defender.weakness -> 2
  attacker.damageType in defender.immune -> 0
  else -> 1
}

val groupRegex =
  Regex("(?<units>\\d+) units each with (?<hitPoints>\\d+) hit points (?<hasTraits>\\((?<traits>[\\w\\d; ,]+)\\) )?with an attack that does (?<damage>\\d+) (?<damageType>\\w+) damage at initiative (?<initiative>\\d+)")
val immuneRegex =
  Regex("(immune to (?<immune>(\\w+,? ?)+))?;? ?(weak to (?<weak>(\\w+,? ?)+))?;? ?(immune to (?<immune2>(\\w+,? ?)+)?)?")


data class Group(
  val name: String,
  val size: Int,
  val hitPoints: Int,
  val weakness: List<String>,
  val immune: List<String>,
  val damage: Int,
  val damageType: String,
  val initiative: Int,
  val immuneSystem: Boolean
                )

fun parseLine(name: String, line: String, immuneSystem: Boolean): Group {
  val basic = groupRegex.matchEntire(line)!!.groups
  val immune = mutableListOf<String>()
  val weak = mutableListOf<String>()
  if (basic["hasTraits"] != null) {
    val traits = immuneRegex.matchEntire(basic["traits"]!!.value)!!.groups
    if (traits["immune"] != null) {
      traits["immune"]?.value!!.split(",").map(String::trim).toCollection(immune)
    } else if (traits["immune2"] != null) {
      traits["immune2"]?.value!!.split(",").map(String::trim).toCollection(immune)
    }
    if (traits["weak"] != null) {
      traits["weak"]!!.value.split(",").map(String::trim).toCollection(weak)
    }
  }
  return Group(
    name = name,
    size = basic["units"]!!.value.toInt(),
    hitPoints = basic["hitPoints"]!!.value.toInt(),
    weakness = weak,
    immune = immune,
    damage = basic["damage"]!!.value.toInt(),
    damageType = basic["damageType"]!!.value,
    initiative = basic["initiative"]!!.value.toInt(),
    immuneSystem = immuneSystem
              )
}
