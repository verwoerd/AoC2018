package day19.second

import tools.timeSolution

/**
 * @author verwoerd
 * @since 19-12-2018
 */


fun main() = timeSolution {
  var value = 2 * 2 * 19 * 11 + (4 * 22 + 15)
  println("First Solution($value) = ${(1..value).filter { value % it == 0 }.sum()}")
  value += (27 * 28 + 29) * 30 * 14 * 32
  println("Second Solution($value) = ${(1..value).filter { value % it == 0 }.sum()}")
}

// rewrite the instruction code to kotlin
var r0 = 1 // Decider for input, solution
var r1 = 0 // Test variable
var r2 = 0 // left side of multiplication
var r3 = 0 // right side of multiplication (counted)
var r5 = 0 // target number

// pseudo code for instructions (not meant to be run, to slow and call stack issues)
// The program calculates the sum of all unique dividers of the number calculated from line 17
fun start() {
  line17()
}

fun line1() {
  r3 = 1             // 1
  line2()
}

fun line2() {
  r2 = 1            // 2
  line3()
}

fun line3() {
  r1 = r2 * r3      // 3
  if (r1 == r5) { // 4 + 5
    r0 += r3   // 7
  }
  r2++  //8
  if (r2 > r5) { //8
    r3++// 12
    if (r3 > r5) { // 13
      finished() // 16
    } else {
      line2()  //15
    }
  } else {
    line3() // 11
  }
}


fun line17() {
  r5 += 2    // 17
  r5 *= r5  // 18
  r5 *= 19  // 19
  r5 *= 11  //20
  r1 += 4     // 21
  r1 *= 22  // 22
  r1 += 15  // 23
  r5 += r1  // 24
  if (r0 == 0) { // 25
    r1 = 27 // 27
    r1 *= 28 //28
    r1 += 29 // 29
    r1 *= 30 // 30
    r1 *= 14 // 31
    r1 *= 32 //32
    r5 += r1 // 33
    r0 = 0 // 34
    line1() // 35
  }
}

fun finished() {
  println(r0)
}
