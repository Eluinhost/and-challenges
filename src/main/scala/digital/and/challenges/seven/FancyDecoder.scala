package digital.and.challenges.seven

import com.googlecode.lanterna.{SGR, TextCharacter, TextColor}
import com.googlecode.lanterna.terminal.Terminal

/**
  * Implementation that adds a simple terminal animation of decoding the grid whilst it works it out
  * @param tickTime how long to wait between rendering each step of movement during decoding
  */
class FancyDecoder(terminal: Terminal, gridSource: String, val tickTime: Int) extends Decoder(gridSource, terminal) {
  override def move(): Option[Char] = {
    // rerender the correct char in the current space but with our 'trail' background color
    graphics.setCharacter(
      pos.x,
      pos.y,
      new TextCharacter(cells(pos.y)(pos.x))
        .withBackgroundColor(TextColor.ANSI.BLUE)
    )
    terminal.flush()

    // let parent logic calculate logic for new position/velocity
    val result = super.move()

    // set the styles for the new pos to our 'highlighted' colours
    graphics.setCharacter(
      pos.x,
      pos.y,
      new TextCharacter(cells(pos.y)(pos.x))
        .withForegroundColor(TextColor.ANSI.BLACK)
        .withBackgroundColor(TextColor.ANSI.CYAN)
    )
    terminal.flush()

    // let it just sit there for a bit before continuing so we can see it visually
    Thread.sleep(tickTime)

    result
  }

  override def decodeChar(char: Char, position: Int): Char = {
    // render the 'waiting' char until we get our result to overwrite it and move the next char
    graphics.setCharacter(
      Decoder.DECODED_PREFIX.length + position,
      height + 3,
      new TextCharacter('_')
        .withBackgroundColor(TextColor.ANSI.BLACK)
        .withModifier(SGR.BLINK)
    )
    terminal.flush()

    val result = super.decodeChar(char, position)

    // reset the grid after printing lines all over it
    printGrid()

    result
  }
}
