package digital.and.challenges.four

import scala.util.Random

object Main extends App {
  val currencyAsWordsParser = new CurrencyAsWordsParser(new IntegerToWords())

  List
    .fill(1000) {
      val integer = "%6d".format(Random.nextInt(1000000));
      val fractional = "%02d".format(Random.nextInt(100))

      val amount = s"$integer.$fractional"
      val converted = currencyAsWordsParser.parse(amount.trim)

      amount -> converted
    }
    // Invalid input example to end
    .appended("1232.2342" -> currencyAsWordsParser.parse("1232.2342"))
    .foreach {
      case (input, currencyAsWordsParser.Success(matched, _)) =>
        println(s"$input -> $matched")
      case (input, f: currencyAsWordsParser.Failure) =>
        println(s"$input -> $f")
      case (input, e: currencyAsWordsParser.Error) =>
        println(s"$input -> $e")
    }
}

// SAMPLE OUTPUT
// -------------
//   177100.40 -> one hundred and seventy seven thousand one hundred pounds and fourty pence
//   853139.18 -> eight hundred and fifty three thousand one hundred and thirty nine pounds and eighteen pence
//    39247.94 -> thirty nine thousand two hundred and fourty seven pounds and ninety four pence
//   307022.10 -> three hundred and seven thousand twenty two pounds and ten pence
//   686662.03 -> six hundred and eighty six thousand six hundred and sixty two pounds and three pence
//   441431.48 -> four hundred and fourty one thousand four hundred and thirty one pounds and fourty eight pence
//   247952.14 -> two hundred and fourty seven thousand nine hundred and fifty two pounds and fourteen pence
//   778821.28 -> seven hundred and seventy eight thousand eight hundred and twenty one pounds and twenty eight pence
//   428254.41 -> four hundred and twenty eight thousand two hundred and fifty four pounds and fourty one pence
//    84879.76 -> eighty four thousand eight hundred and seventy nine pounds and seventy six pence
//   199759.33 -> one hundred and ninety nine thousand seven hundred and fifty nine pounds and thirty three pence
//    22842.93 -> twenty two thousand eight hundred and fourty two pounds and ninety three pence
//   838139.87 -> eight hundred and thirty eight thousand one hundred and thirty nine pounds and eighty seven pence
//    12716.24 -> twelve thousand seven hundred and sixteen pounds and twenty four pence
//   296652.83 -> two hundred and ninety six thousand six hundred and fifty two pounds and eighty three pence
// ...
// ...
//   1232.2342 -> [1.8] error: Expected end of input, found '4' instead
//
//   1232.2342
//       ^
