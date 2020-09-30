package digital.and.challenges.six

trait Dictionary {
  def insert(word: String): Dictionary

  def insertAll(words: Seq[String]): Dictionary

  def spellcheck(word: String): AutocompleteResult

  def isWord(word: String): Boolean
}
