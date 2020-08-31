package digital.and.challenges.four
import scala.collection.immutable.NumericRange

class IntegerToWords {
  protected val BASE_UNITS: List[String] =
    "zero" :: "one" :: "two" :: "three" :: "four" :: "five" :: "six" :: "seven" :: "eight" :: "nine" :: Nil

  protected val TEENS: List[String] =
    "ten" :: "eleven" :: "twelve" :: "thirteen" :: "fourteen" :: "fifteen" :: "sixteen" :: "seventeen" :: "eighteen" :: "nineteen" :: Nil

  protected val TENS: List[String] =
    null :: null :: "twenty" :: "thirty" :: "fourty" :: "fifty" :: "sixty" :: "seventy" :: "eighty" :: "ninety" :: Nil

  protected val GROUP_NAMES: List[String] =
    null :: "thousand" :: "million" :: "billion" :: "trillion" :: "quadrillion" :: "quintillion" :: "sextillion" :: "septillion" :: Nil

  val VALID_DIGIT_CHARACTERS: NumericRange[Char] = '0' to '9'

  // take a group of characters and turns them into words, will fail if > 3 chars are provided
  protected def convertDigitGroup(digits: Seq[Char], withZero: Boolean = false): String =
    // start by dropping any leading zeros
    digits.dropWhile(_ == '0') match {
      // handles exact hundreds
      case Seq(hundreds, '0', '0') =>
        s"${convertDigitGroup(Seq(hundreds))} hundred"
      // handles hundreds with left-overs
      case Seq(hundreds, tens, units) =>
        s"${convertDigitGroup(Seq(hundreds))} hundred and${convertDigitGroup(Seq(tens, units))}"
      // handles 10-19
      case Seq('1', units) =>
        s" ${TEENS(units - VALID_DIGIT_CHARACTERS.start)}"
      // handles 20-99
      case Seq(tens, units) =>
        s" ${TENS(tens - VALID_DIGIT_CHARACTERS.start)}${convertDigitGroup(Seq(units))}"
      // handles 1-9
      case Seq(units) =>
        s" ${BASE_UNITS(units - VALID_DIGIT_CHARACTERS.start)}"
      // handles 0
      case Nil if !withZero =>
        ""
      // handles 0
      case Nil =>
        s" ${BASE_UNITS.head}"
      case x =>
        println(x)
        throw new IllegalArgumentException(s"Invalid input $x")
    }

  val MAX_CONVERSION_LENGTH: Int = GROUP_NAMES.length * 3

  def convertDigits(digits: Seq[Char]): String = {
    if (digits.length > MAX_CONVERSION_LENGTH) {
      throw new IllegalArgumentException(s"can only handle up to $MAX_CONVERSION_LENGTH digits, found ${digits.length}")
    }

    if (digits.exists(digit => !VALID_DIGIT_CHARACTERS.contains(digit))) {
      throw new IllegalArgumentException(s"found invalid digit character in '$digits'")
    }

    // converts string like 1234567 into [567, 234, 1]
    val digitGroups = digits
      // reverse so largest unit group will be one that can contain < 3 chars
      .reverse
      // group digits by 3 chars
      .grouped(3)
      // put characters back into original order within their groups
      .map(_.reverse)
      .toList

    digitGroups.zipWithIndex
      .map {
        // first group is smallest unit and doesn't have a 'group name', it is allowed to render 'zero' only if it's the only group
        case (group, index) if index == 0 =>
          convertDigitGroup(group, withZero = digitGroups.length == 1)
        // empty groups/zerod groups don't render anything
        case (group, _) if group.forall(_ == '0') =>
          ""
        // convert the digits and add the correct group name afterwards
        case (group, index) =>
          s"${convertDigitGroup(group)} ${GROUP_NAMES(index)}"
      }
      // put the groups back into the correct order and combine them into a single string
      .reverse
      .mkString
      .drop(1) // remove the leading space
  }
}
