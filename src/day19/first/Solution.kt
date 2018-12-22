package day19.first

import tools.timeSolution

/**
 * @author verwoerd
 * @since 19-12-2018
 */

fun main() = timeSolution {
  val instructions = System.`in`.bufferedReader().useLines { sequence ->
    sequence.map { parseLine(it) }.toMutableList()
  }
  val ip = instructions.first().a
  instructions.removeAt(0)

  var register = Register(0, 0, 0, 0, 0, 0)
  while (register[ip] < instructions.size) {
    val instruction = instructions[register[ip]]
//    print("${register[ip]} $register $instruction ")
    register = instruction.operator(register, instruction.a, instruction.b, instruction.c)
    register.setRegister(ip, register[ip] + 1)
//    println("$register")
  }
  println(register)
}


data class Instruction(val operator: Opcode, val a: Int, val b: Int, val c: Int)


// Copied from day 16
private operator fun Opcode.invoke(start: Register, a: Int, b: Int, c: Int) =
  when (this) {
    Opcode.ADDR -> addr(start, a, b, c)
    Opcode.ADDI -> addi(start, a, b, c)
    Opcode.MULR -> mulr(start, a, b, c)
    Opcode.MULI -> muli(start, a, b, c)
    Opcode.BANR -> banr(start, a, b, c)
    Opcode.BANI -> bani(start, a, b, c)
    Opcode.BORR -> borr(start, a, b, c)
    Opcode.BORI -> bori(start, a, b, c)
    Opcode.SETR -> setr(start, a, b, c)
    Opcode.SETI -> seti(start, a, b, c)
    Opcode.GTIR -> gtir(start, a, b, c)
    Opcode.GTRI -> gtri(start, a, b, c)
    Opcode.GTRR -> gtrr(start, a, b, c)
    Opcode.EQIR -> eqir(start, a, b, c)
    Opcode.EQRI -> eqri(start, a, b, c)
    Opcode.EQRR -> eqrr(start, a, b, c)
  }

fun addr(register: Register, left: Int, right: Int, result: Int) =
  register.copy().setRegister(result, register[left] + register[right])

fun addi(register: Register, left: Int, right: Int, result: Int) =
  register.copy().setRegister(result, register[left] + right)

fun mulr(register: Register, left: Int, right: Int, result: Int) =
  register.copy().setRegister(result, register[left] * register[right])

fun muli(register: Register, left: Int, right: Int, result: Int) =
  register.copy().setRegister(result, register[left] * right)

fun banr(register: Register, left: Int, right: Int, result: Int) =
  register.copy().setRegister(result, register[left] and register[right])

fun bani(register: Register, left: Int, right: Int, result: Int) =
  register.copy().setRegister(result, register[left] and right)

fun borr(register: Register, left: Int, right: Int, result: Int) =
  register.copy().setRegister(result, register[left] or register[right])

fun bori(register: Register, left: Int, right: Int, result: Int) =
  register.copy().setRegister(result, register[left] or right)

fun setr(register: Register, left: Int, right: Int, result: Int) = register.copy().setRegister(result, register[left])
fun seti(register: Register, left: Int, right: Int, result: Int) = register.copy().setRegister(result, left)
fun gtir(register: Register, left: Int, right: Int, result: Int) =
  register.copy().setRegister(result, boolToInt(left > register[right]))

fun gtri(register: Register, left: Int, right: Int, result: Int) =
  register.copy().setRegister(result, boolToInt(register[left] > right))

fun gtrr(register: Register, left: Int, right: Int, result: Int) =
  register.copy().setRegister(result, boolToInt(register[left] > register[right]))

fun eqir(register: Register, left: Int, right: Int, result: Int) =
  register.copy().setRegister(result, boolToInt(left == register[right]))

fun eqri(register: Register, left: Int, right: Int, result: Int) =
  register.copy().setRegister(result, boolToInt(register[left] == right))

fun eqrr(register: Register, left: Int, right: Int, result: Int) =
  register.copy().setRegister(result, boolToInt(register[left] == register[right]))


fun boolToInt(bool: Boolean) = if (bool) 1 else 0

enum class Opcode {
  ADDR, ADDI, MULR, MULI, BANR, BANI, BORR, BORI, SETR, SETI, GTIR, GTRI, GTRR, EQIR, EQRI, EQRR
}


data class Register(var r0: Int, var r1: Int, var r2: Int, var r3: Int, var r4: Int, var r5: Int) {
  fun setRegister(register: Int, value: Int): Register {
    when (register) {
      0 -> r0 = value
      1 -> r1 = value
      2 -> r2 = value
      3 -> r3 = value
      4 -> r4 = value
      5 -> r5 = value
    }
    return this
  }

  operator fun get(register: Int): Int {
    return when (register) {
      0 -> r0
      1 -> r1
      2 -> r2
      3 -> r3
      4 -> r4
      5 -> r5
      else -> throw IllegalArgumentException("Invalid register $register")
    }
  }
}

val operationRegex = Regex("(\\w+) (\\d+) (\\d+) (\\d)")
val ipRegex = Regex("#ip (\\d)")
fun parseLine(line: String) = when {
  line[0] == '#' -> Instruction(
    a = ipRegex.matchEntire(line)!!.groupValues[1].toInt(),
    b = 0,
    c = -1,
    operator = Opcode.SETI
                               )
  else -> operationRegex.matchEntire(line)!!.groupValues.let {
    Instruction(
      Opcode.valueOf(it[1].toUpperCase()),
      it[2].toInt(),
      it[3].toInt(),
      it[4].toInt()
               )
  }
}
