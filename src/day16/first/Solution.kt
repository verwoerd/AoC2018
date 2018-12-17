package day16.first

import tools.timeSolution

/**
 * @author verwoerd
 * @since 16-12-2018
 */
fun main() = timeSolution{
  val cases = System.`in`.bufferedReader().useLines { sequence ->
    sequence.chunked(4).filter { it[0].startsWith("Before:") }.map { toTestCase(it) }.toList()
  }
  val result = cases.map { Pair(it, checkOperation(it)) }.filter { it.second.count() >= 3 }.count()
  println("Solution: $result")
}

fun checkOperation(case: TestCase): MutableList<Opcode> {
  val matches= mutableListOf<Opcode>()
  Opcode.values().forEach {
    try {
      val result = it(case.start, case.a, case.b, case.c )
      if (result == case.end) {
        matches.add(it)
      }
    } catch (e: Exception) {
      // ignore
    }
  }
  return matches
}

private operator fun Opcode.invoke(start: Register, a: Int, b: Int, c: Int) =
  when(this) {
    Opcode.ADDR -> addr(start, a,b,c)
    Opcode.ADDI -> addi(start, a,b,c)
    Opcode.MULR -> mulr(start, a,b,c)
    Opcode.MULI -> muli(start, a,b,c)
    Opcode.BANR -> banr(start, a,b,c)
    Opcode.BANI -> bani(start, a,b,c)
    Opcode.BORR -> borr(start, a,b,c)
    Opcode.BORI -> bori(start, a,b,c)
    Opcode.SETR -> setr(start, a,b,c)
    Opcode.SETI -> seti(start, a,b,c)
    Opcode.GTIR -> gtir(start, a,b,c)
    Opcode.GTRI-> gtri(start, a,b,c)
    Opcode.GTRR -> gtrr(start, a,b,c)
    Opcode.EQIR -> eqir(start, a,b,c)
    Opcode.EQRI -> eqri(start, a,b,c)
    Opcode.EQRR -> eqrr(start, a,b,c)
  }

fun addr(register: Register, left: Int, right: Int, result: Int)= register.copy().setRegister(result, register.getRegister(left)+register.getRegister(right))
fun addi(register: Register, left: Int, right: Int, result: Int)= register.copy().setRegister(result, register.getRegister(left)+right)
fun mulr(register: Register, left: Int, right: Int, result: Int)= register.copy().setRegister(result, register.getRegister(left)*register.getRegister(right))
fun muli(register: Register, left: Int, right: Int, result: Int)= register.copy().setRegister(result, register.getRegister(left)*right)
fun banr(register: Register, left: Int, right: Int, result: Int)= register.copy().setRegister(result, register.getRegister(left) and register.getRegister(right))
fun bani(register: Register, left: Int, right: Int, result: Int)= register.copy().setRegister(result, register.getRegister(left) and right)
fun borr(register: Register, left: Int, right: Int, result: Int)= register.copy().setRegister(result, register.getRegister(left) or register.getRegister(right))
fun bori(register: Register, left: Int, right: Int, result: Int)= register.copy().setRegister(result, register.getRegister(left) or right)
fun setr(register: Register, left: Int, right: Int, result: Int)= register.copy().setRegister(result, register.getRegister(left))
fun seti(register: Register, left: Int, right: Int, result: Int)= register.copy().setRegister(result, left)
fun gtir(register: Register, left: Int, right: Int, result: Int)= register.copy().setRegister(result, boolToInt(left > register.getRegister(right)))
fun gtri(register: Register, left: Int, right: Int, result: Int)= register.copy().setRegister(result, boolToInt(register.getRegister(left) > right))
fun gtrr(register: Register, left: Int, right: Int, result: Int)= register.copy().setRegister(result, boolToInt(register.getRegister(left) > register.getRegister(right)))
fun eqir(register: Register, left: Int, right: Int, result: Int)= register.copy().setRegister(result, boolToInt(left == register.getRegister(right)))
fun eqri(register: Register, left: Int, right: Int, result: Int)= register.copy().setRegister(result, boolToInt(register.getRegister(left) == right))
fun eqrr(register: Register, left: Int, right: Int, result: Int)= register.copy().setRegister(result, boolToInt(register.getRegister(left) == register.getRegister(right)))



fun boolToInt(bool: Boolean) = if (bool) 1 else 0

enum class Opcode {
  ADDR, ADDI, MULR, MULI, BANR, BANI, BORR, BORI, SETR, SETI, GTIR, GTRI, GTRR, EQIR, EQRI, EQRR
}


data class Register(var r0: Int, var r1: Int, var r2: Int, var r3: Int) {
  fun setRegister(register: Int, value: Int) : Register {
    when(register) {
      0 -> r0 = value
      1 -> r1 = value
      2 -> r2 = value
      3 -> r3 = value
    }
    return this
  }

  fun getRegister(register:Int): Int {
    return when(register) {
      0 -> r0
      1 -> r1
      2 -> r2
      3 -> r3
      else -> throw IllegalArgumentException("Invalid register $register")
    }
  }
}
data class TestCase(val opcode: Int, val a:Int, val b:Int, val c:Int, val start: Register, val end: Register)
val beforeRegex = Regex("Before: \\[(\\d), (\\d), (\\d), (\\d)]")
val operationRegex = Regex("(\\d+) (\\d) (\\d) (\\d)")
val afterRegex = Regex("After:  \\[(\\d), (\\d), (\\d), (\\d)]")

fun toTestCase(case: List<String>) : TestCase {
  val before = beforeRegex.matchEntire(case[0])!!.groupValues
  val operation = operationRegex.matchEntire(case[1])!!.groupValues
  val after = afterRegex.matchEntire(case[2])!!.groupValues
  return TestCase(opcode = operation[1].toInt(), a= operation[2].toInt(), b=operation[3].toInt(), c=operation[4].toInt(),
           start = Register(r0 = before[1].toInt(), r1 = before[2].toInt(), r2=before[3].toInt(), r3 = before[4].toInt()),
           end = Register(r0 = after[1].toInt(), r1 = after[2].toInt(), r2=after[3].toInt(), r3 = after[4].toInt())
           )
}
