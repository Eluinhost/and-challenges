package digital.and.challenges.six

import scala.io.{Source, StdIn}

object Main extends App {
  val lines = Source.fromResource("challenge-six.txt").getLines.toList

  println("Loading dictionary...")

  val dictionary = new DawgDictionary().insertAll(lines).minimise()

  Iterator
    .continually {
      println("Enter word to check spelling or empty to quit")
      StdIn.readLine("> ")
    }
    .takeWhile(_.nonEmpty)
    .foreach { word =>
      dictionary.spellcheck(word) match {
        case ValidWord => println("Valid Word!")
        case InvalidWord(partial, suffixes) =>
          println(s"Invalid word, correct until $partial[${word.substring(partial.length)}]")
          println(s"Alternatives to complete word:")
          suffixes.take(10).foreach(suffix => println(partial + suffix))
          if (suffixes.length > 10) {
            println(s"and ${suffixes.length - 10} more...")
          }
      }
    }
}
