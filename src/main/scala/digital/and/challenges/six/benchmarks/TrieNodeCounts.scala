package digital.and.challenges.six.benchmarks

import digital.and.challenges.six.DawgDictionary
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator

import scala.io.Source

object TrieNodeCounts extends App {
  val lines = Source.fromResource("challenge-six.txt").getLines.toList

  val trieDictionary = new DawgDictionary().insertAll(lines)

  val (trieNodes, trieEdges) = trieDictionary.size

  println(
    s"Trie size: ${ObjectSizeCalculator.getObjectSize(trieDictionary)} bytes. $trieNodes nodes and $trieEdges edges"
  )

  trieDictionary.minimise()

  val (minimizedNodes, minimizedEdges) = trieDictionary.size

  println(
    s"DAWG size: ${ObjectSizeCalculator.getObjectSize(trieDictionary)} bytes. $minimizedNodes nodes and $minimizedEdges edges"
  )
}
