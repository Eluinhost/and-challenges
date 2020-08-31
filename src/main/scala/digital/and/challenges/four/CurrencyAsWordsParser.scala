package digital.and.challenges.four

import scala.language.{implicitConversions, postfixOps}
import scala.util.parsing.input.CharSequenceReader

class CurrencyAsWordsParser(integerToWords: IntegerToWords) extends CharacterParsers {
  // parser that reads digits (at least 1) until a non-digit
  protected def integer: Parser[List[Char]] =
    rep1(digit) >> {
      // show the MAX_LENGTH_ERROR if there are more character than we can convert
      case digits if digits.length > integerToWords.MAX_CONVERSION_LENGTH =>
        failure(
          s"Number too long to parse, we don't have a name for that..., can only handle ${integerToWords.MAX_CONVERSION_LENGTH} digits total"
        )
      case digits => success(digits)
    }

  // parse exactly 2 digits for fractional part
  protected def fractional: Parser[List[Char]] =
    repN(2, digit)

  // currency is an integer part and an optional fractional part
  protected def currency: Parser[List[Char] ~ Option[List[Char]]] =
    integer ~ opt('.' ~>! fractional)

  protected def currencyAsWords: Parser[String] =
    currency ^^ {
      case integer ~ maybeFractional =>
        val fractional = maybeFractional
          // if it's all zeros then treat it as not provided
          .filter(x => !x.forall(_ == '0'))
          .map(integerToWords.convertDigits)
          .map(x => s" and $x pence")
          .getOrElse("")

        s"${integerToWords.convertDigits(integer)} pounds$fractional"
    }

  /**
    * Takes a given input number and attempts to parse it as currency into words
    * @param input number string to convert
    * @return
    */
  def parse(input: String): ParseResult[String] =
    (currencyAsWords <~! endOfInput)(new CharSequenceReader(input))
}
