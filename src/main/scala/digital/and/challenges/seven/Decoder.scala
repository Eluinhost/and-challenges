package digital.and.challenges.seven

import com.googlecode.lanterna.graphics.TextGraphics
import com.googlecode.lanterna.terminal.Terminal
import com.googlecode.lanterna.{SGR, TextCharacter, TextColor}
import digital.and.challenges.seven.Decoder.{DECODED_PREFIX, DECODING_PREFIX}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source

object Decoder {
  val DECODING_PREFIX = "    Decoding: "
  val DECODED_PREFIX = "Decoded word: "
}

class Decoder(gridSource: String, val terminal: Terminal) {
  protected var velocity: Vector2 = _
  protected var pos: Vector2 = _

  protected val graphics: TextGraphics = terminal.newTextGraphics()

  private val lines = Source.fromResource(gridSource).getLines().toList
  protected val width: Int = lines.maxBy(_.length).length
  protected val height: Int = lines.length

  val cells: List[List[Char]] = lines.map(_.padTo(width, ' ').toCharArray.toList)

  protected val startingPositions = mutable.Map.empty[Char, Vector2]

  // store starting positions for each char so we can lookup where to start
  cells.zipWithIndex.foreach {
    case (row, y) =>
      row.zipWithIndex.foreach {
        case (cell, x) =>
          if ((cell >= 'a' && cell <= 'z') || (cell >= 'A' && cell <= 'Z')) {
            startingPositions.put(cell, Vector2(x, y))
          }
      }
  }

  protected def printGrid(): Unit = {
    (0 until width).foreach { x =>
      (0 until height).foreach { y =>
        graphics.setCharacter(
          x,
          y,
          new TextCharacter(cells(y)(x))
        )
      }
    }
    terminal.flush()
  }

  protected def move(): Option[Char] = {
    // recalculate the new position
    pos = pos.add(velocity)

    cells(pos.y)(pos.x) match {
      // no need to run anything if it's empty
      case ' ' =>
      // mirror reflects so update velocity based on mirror orientations
      case '/' =>
        velocity = velocity match {
          case Vector2(0, 1)  => Vector2(-1, 0)
          case Vector2(0, -1) => Vector2(1, 0)
          case Vector2(1, 0)  => Vector2(0, -1)
          case Vector2(-1, 0) => Vector2(0, 1)
        }
      case '\\' =>
        velocity = velocity match {
          case Vector2(0, 1)  => Vector2(1, 0)
          case Vector2(0, -1) => Vector2(-1, 0)
          case Vector2(1, 0)  => Vector2(0, 1)
          case Vector2(-1, 0) => Vector2(0, -1)
        }
      // if it's anything else we assume it's a letter and let the caller know which one
      case letter =>
        return Some(letter)
    }

    // not a letter so let caller know
    None
  }

  def decodeChar(char: Char, position: Int): Char = {
    pos = startingPositions(char)

    // set initial vector depending on which edge it is on
    velocity = pos match {
      case Vector2(0, _)                    => Vector2(1, 0)
      case Vector2(_, 0)                    => Vector2(0, 1)
      case Vector2(x, _) if x == width - 1  => Vector2(-1, 0)
      case Vector2(_, y) if y == height - 1 => Vector2(0, -1)
    }

    // loop until we end up hitting a letter (or the end of the universe, whichever comes first)
    var letter: Option[Char] = None
    do {
      letter = move()
    } while (letter.isEmpty)

    // pass letter back up to build the word up
    letter.get
  }

  def decode(word: String): String = {
    terminal.enterPrivateMode()
    terminal.clearScreen()
    terminal.setCursorVisible(false)

    // print initial clean grid out
    printGrid()

    // write out the word we're decoding with a label
    s"$DECODING_PREFIX$word".zipWithIndex.foreach {
      case (char, x) =>
        graphics.setCharacter(
          x,
          height + 2,
          new TextCharacter(char)
            .withModifier(SGR.BOLD)
        )
    }

    // write out the label for the decoded word
    (0 until DECODED_PREFIX.length).foreach { x =>
      graphics.setCharacter(
        x,
        height + 3,
        new TextCharacter(DECODED_PREFIX(x))
          .withModifier(SGR.BOLD)
      )
    }

    terminal.flush()

    val decoded = ListBuffer.empty[Char]

    (0 until word.length).foreach { x =>
      // decode this individual character and add it to the end of the word we're building
      val decodedChar = decodeChar(word(x), x)
      decoded += decodedChar

      // add actual char to UI in correct position
      graphics.setCharacter(
        DECODED_PREFIX.length + x,
        height + 3,
        new TextCharacter(decodedChar)
      )
      terminal.flush()
    }

    // we're done so highlight the full decoded word in green and bold it
    decoded.indices.foreach { x =>
      graphics.setCharacter(
        DECODED_PREFIX.length + x,
        height + 3,
        new TextCharacter(decoded(x))
          .withBackgroundColor(TextColor.ANSI.GREEN)
          .withModifier(SGR.BOLD)
      )
    }
    terminal.flush()

    // give the full decoded word back
    decoded.mkString("")
  }
}
