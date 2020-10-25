package digital.and.challenges.eight

import scala.collection.mutable.ListBuffer
import scala.io.Source

object Main extends App {
  def parseLogo(lines: List[List[Char]]): List[String] =
    // for each row + column build up a list of words and combine them back into 1 big list
    (lines concat lines.transpose).flatMap {
      _.foldLeft(ListBuffer(ListBuffer.empty[Char])) {
        // start a new word when we come across a space
        case (acc, ' ') =>
          acc.addOne(ListBuffer.empty)
        // any other character appends to the end of current 'word'
        case (acc, char) =>
          acc.last.addOne(char)
          acc
      }
      // combine collected 'words' back into strings
        .map(_.mkString("").trim)
        // get rid of empty and single character 'words'
        .filter(_.length > 1)
    }

  (1 to 5)
    // parse the file into a List[List[Char]] of it's contents
    .map(id => Source.fromResource(s"challenge-eight-$id.txt").getLines().toList.map(_.toList))
    .map(parseLogo)
    .foreach(result => println(result.mkString(", ")))

  // OUTPUT:
  //
  // HAPPY, FISHING, THE, PENGUIN, GROUP
  // STEVE, BAKERS, LTD, TACKLE, SUPPLIER
  // HONEST, WHOLESOME, EMPORIUM, JOES, MEAT
  // BILL, BURIALS, BUDGET, BENS
  // HOPE, CENTER, EDWARD, ADULTS, HOUSE, AND, STUDYL, FOR
}
