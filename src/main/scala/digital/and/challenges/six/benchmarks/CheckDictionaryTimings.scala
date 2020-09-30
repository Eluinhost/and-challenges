package digital.and.challenges.six.benchmarks

import java.text.NumberFormat

import TimeBlock._
import digital.and.challenges.six._
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator

import scala.io.Source

// very basic app for a basic overview of timings (not really comparable to the actual benchmarks)
object CheckDictionaryTimings extends App {
  val formatter = NumberFormat.getInstance
  val lines = Source.fromResource("challenge-six.txt").getLines.toList

  println("#### NaiveDictionary ###")
  new CheckDictionaryTimings(new NaiveDictionary().insertAll(lines), formatter).run()
  println("#### TrieDictionary ###")
  new CheckDictionaryTimings(new DawgDictionary().insertAll(lines), formatter).run()
  println("#### DawgDictionary ###")
  new CheckDictionaryTimings(new DawgDictionary().insertAll(lines).minimise(), formatter).run()
}

class CheckDictionaryTimings(val dictionary: Dictionary, val formatter: NumberFormat) {
  private def format(n: Number): String = formatter.format(n)

  private def printSize(): Unit = {
    println(s"Size: ${ObjectSizeCalculator.getObjectSize(dictionary)} bytes")
    dictionary match {
      case x: NaiveDictionary =>
        println(s"Size: ${x.size} items")
      case x: DawgDictionary =>
        val (nodes, edges) = x.size

        println(s"Nodes: ${format(nodes)} Edges: ${format(edges)}")
      case _ =>
        println("Cannot print size, don't understand this dictionary")
    }
  }

  private def runTimingForIsWord(word: String, isWord: Boolean): Unit = {
    time(duration => s"Took ${format(duration / 1000)}µs to check if '$word' is a word") {
      assert(dictionary.isWord(word) == isWord)
    }
  }

  private def runTimingForAutocomplete(word: String, isWord: Boolean): Unit = {
    time(duration => s"Took ${format(duration / 1000)}µs to spellcheck '$word'") {
      dictionary.spellcheck(word) match {
        case ValidWord =>
          assert(isWord, s"Expected an invalid word but found a valid word instead for '$word'")
        case InvalidWord(partial, potentials) =>
          assert(!isWord, s"Expected an valid word but found a invalid word instead for '$word'")

          println(s"'$word' correct up until: '$partial', suggestions:")

          potentials.take(5).foreach { potential =>
            println(s"??? $partial$potential")
          }

          if (potentials.length > 5) {
            println(s"and ${potentials.length} more...")
          }
      }
    }
  }

  def setup(lines: List[String]): Unit = {
    time(duration =>
      s"Took ${formatter.format(duration / 1_000_000)}ms to create dictionary for ${formatter.format(lines.size)} entries"
    ) {
      lines.foreach(dictionary.insert)
    }
  }

  def run(): Unit = {
    printSize()

    runTimingForIsWord("dog", isWord = true)
    runTimingForIsWord("echo", isWord = true)
    runTimingForIsWord("echolocation", isWord = true)
    runTimingForIsWord("echolochocolate", isWord = false)
    runTimingForIsWord("k", isWord = false)

    runTimingForAutocomplete("dog", isWord = true)
    runTimingForAutocomplete("echo", isWord = true)
    runTimingForAutocomplete("echolochocolate", isWord = false)
    runTimingForAutocomplete("k", isWord = false)
  }
}
