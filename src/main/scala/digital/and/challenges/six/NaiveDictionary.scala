package digital.and.challenges.six

import scala.collection.mutable

/**
  * Basic implementation that stores the dictionary as 1 big list as a baseline
  */
class NaiveDictionary extends Dictionary {
  private[this] case class PrefixCheck(prefixSize: Int, suffix: String)

  private[this] val list = mutable.ListBuffer.empty[String]

  override def insert(word: String): NaiveDictionary = {
    list.addOne(word.toLowerCase)
    this
  }

  override def insertAll(words: Seq[String]): NaiveDictionary = {
    list.addAll(words.map(_.toLowerCase()))
    this
  }

  override def isWord(word: String): Boolean = list.contains(word)

  override def spellcheck(word: String): AutocompleteResult = {
    if (isWord(word)) {
      ValidWord
    } else {
      val suffixes = mutable.ListBuffer.empty[PrefixCheck]
      var longestPrefix = 0

      for (check <- list) {
        val prefixLength = getCommonPrefixLength(word, check)

        if (prefixLength > 0 && prefixLength >= longestPrefix) {
          suffixes.addOne(PrefixCheck(prefixSize = prefixLength, suffix = check.substring(prefixLength)))

          if (prefixLength > longestPrefix) {
            longestPrefix = prefixLength

            // get rid of all potentials we've collected that are shorter than this one
            suffixes.filterInPlace(_.prefixSize >= prefixLength)
          }
        }
      }

      InvalidWord(partial = word.substring(0, longestPrefix), suffixes = suffixes.map(_.suffix).toList)
    }
  }

  def size: Int = list.size

  private[this] def getCommonPrefixLength(left: String, right: String) = {
    val upto = Math.min(left.length, right.length)
    var size: Int = 0

    while (size < upto && left(size) == right(size))
      size += 1;

    size
  }
}
