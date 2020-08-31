package digital.and.challenges.four

import scala.language.implicitConversions
import scala.util.parsing.combinator.Parsers

trait CharacterParsers extends Parsers {
  override type Elem = Char

  // parser to find specific literal characters
  protected implicit def literal(s: Char): Parser[Char] =
    (in: Input) =>
      if (in.atEnd)
        Failure(s"Expected '$s' but reached end of input instead.", in)
      else if (in.first == s)
        Success(s, in.rest)
      else
        Failure(s"Expected '$s' but found '${in.first}' instead", in)

  // parser that consumes a single digit
  protected val digit: Parser[Char] =
    (in: Input) =>
      if (in.atEnd)
        Failure(s"Expected a digit but reached end of input instead.", in)
      else if (in.first >= '0' && in.first <= '9')
        Success(in.first, in.rest)
      else
        Failure(s"Expected a digit but found '${in.first}' instead", in)

  protected val endOfInput: Parser[Unit] = (in: Input) =>
    if (in.atEnd) Success((), in.rest)
    else Failure(s"Expected end of input, found '${in.first}' instead", in)
}
