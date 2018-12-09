package day8.first

import tools.timeSolution

/**
 * @author verwoerd
 * @since 8-12-2018
 */
fun main() = timeSolution {
  val definition = readLine()!!.split(" ").map { it.toInt() }
  val tree = parse(definition, 0)
  println("Solution: ${sumMetadata(tree.first)}")

}

fun parse(definition: List<Int>, index: Int): Pair<Node, Int> {
  var i = index
  val header = Header(definition[i], definition[++i])
  val node = Node(header, mutableListOf(), mutableListOf())
  repeat(header.children) {
    val result = parse(definition, ++i)
    node.children.add(result.first)
    i = result.second
  }
  node.metadata.addAll(definition.subList(++i, i + header.metadataSize))
  return Pair(node, i + header.metadataSize - 1)
}

fun sumMetadata(root: Node): Int = root.metadata.sum() + root.children.sumBy { sumMetadata(it) }


data class Node(val header: Header, val children: MutableList<Node>, val metadata: MutableList<Int>)
data class Header(val children: Int, val metadataSize: Int)
