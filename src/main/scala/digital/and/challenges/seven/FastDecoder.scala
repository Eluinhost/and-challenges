package digital.and.challenges.seven

import com.googlecode.lanterna.terminal.Terminal

import scala.collection.mutable

// simple optimisation to cache what each character corresponds to up-front on first request
// and then just use it as a lookup table after that
class FastDecoder(gridSource: String, terminal: Terminal) extends Decoder(gridSource, terminal) {
  lazy val cache: Map[Char, Char] = {
    val map = mutable.Map.empty[Char, Char]

    (('a' to 'z') ++ ('A' to 'Z')).foreach { char =>
      map.put(char, super.decodeChar(char, 0))
    }

    map.toMap
  }

  override def decodeChar(char: Char, position: Int): Char = cache(char)
}
