package digital.and.challenges.six

sealed trait AutocompleteResult

case object ValidWord extends AutocompleteResult
case class InvalidWord(partial: String, suffixes: Seq[String]) extends AutocompleteResult
