package digital.and.challenges.one

import scala.io.Source

object Main extends App {
  val lines = Source.fromResource("challenge-one.txt").getLines().map(_.toLowerCase().replaceAll("[^a-z]", ""))

  val (pangrams, nonPangrams) = lines.partition(_.toCharArray.distinct.length == 26)
  val (perfect, nonPerfect) = pangrams.partition(_.length == 26)

  println(s"checksum: ${(perfect.length * nonPerfect.length) - nonPangrams.length}")
}
