package digital.and.challenges.four

import org.scalacheck.Gen
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class IntegerToWordsSpec
    extends AnyFunSpec
    with should.Matchers
    with ScalaCheckDrivenPropertyChecks
    with TableDrivenPropertyChecks {

  val randomValidInteger: Gen[Seq[Char]] = for {
    length <- Gen.choose(1, 27)
    characters <- Gen.listOfN(length, Gen.numChar)
  } yield characters

  def isValidInteger(input: Char): Boolean = input >= '0' && input <= '9'

  def isValidIntegerInput(input: Seq[Char]): Boolean =
    input.nonEmpty && input.length <= 27 && input.forall(isValidInteger)

  // uses regular numbers and just replaces a random character in it with a non-number character
  val randomInvalidInteger: Gen[Seq[Char]] = for {
    integer <- randomValidInteger
    index <- Gen.choose(0, integer.length - 1)
    replacement <- Gen.asciiPrintableChar.suchThat { !isValidInteger(_) }
    (left, right) = integer.splitAt(index)
  } yield left :+ replacement :++ right.drop(1)

  def fixture =
    new {
      val integerToWords = new IntegerToWords()
    }

  describe("IntegerToWords.convertDigits") {
    it("should succeed for a random 100,000 valid random integers with < 28 characters") {
      forAll((randomValidInteger, "integer"), minSuccessful(100000)) { (integer: Seq[Char]) =>
        whenever(isValidIntegerInput(integer)) {
          noException should be thrownBy fixture.integerToWords.convertDigits(integer)
        }
      }
    }

    it("should fail for a random 100,000 random integers when there is non-valid characters in the input") {
      forAll((randomInvalidInteger, "invalid integer"), minSuccessful(100000)) { (invalidInteger: Seq[Char]) =>
        whenever(!isValidIntegerInput(invalidInteger)) {
          the[IllegalArgumentException] thrownBy {
            fixture.integerToWords.convertDigits(invalidInteger)
          } should have message s"found invalid digit character in '$invalidInteger'"
        }
      }
    }

    it("should handle any leading zeros") {
      fixture.integerToWords.convertDigits("00023") should be("twenty three")
      fixture.integerToWords.convertDigits("00000023") should be("twenty three")
      // max length input
      fixture.integerToWords.convertDigits("000000000000000000000000023") should be("twenty three")
    }

    it("should fail when too many digits provided") {
      // 1 above max input
      the[IllegalArgumentException] thrownBy {
        fixture.integerToWords.convertDigits("0000000000000000000000000023")
      } should have message s"can only handle up to ${fixture.integerToWords.MAX_CONVERSION_LENGTH} digits, found 28"

      the[IllegalArgumentException] thrownBy {
        fixture.integerToWords.convertDigits("9329849943290840923840283408")
      } should have message s"can only handle up to ${fixture.integerToWords.MAX_CONVERSION_LENGTH} digits, found 28"

      // much above max input
      the[IllegalArgumentException] thrownBy {
        fixture.integerToWords.convertDigits("309847298743827343290840923840283408")
      } should have message s"can only handle up to ${fixture.integerToWords.MAX_CONVERSION_LENGTH} digits, found 36"
    }

    def runTable(table: TableFor2[String, String]): Unit =
      forAll(table) { (input: String, expected: String) =>
        it(s"should output '$expected' for input $input") {
          fixture.integerToWords.convertDigits(input) shouldBe expected
        }
      }

    describe("base units") {
      runTable(
        Table(
          ("input", "expected"),
          ("0", "zero"),
          ("1", "one"),
          ("2", "two"),
          ("3", "three"),
          ("4", "four"),
          ("5", "five"),
          ("6", "six"),
          ("7", "seven"),
          ("8", "eight"),
          ("9", "nine")
        )
      )
    }

    describe("teens") {
      runTable(
        Table(
          ("input", "expected"),
          ("10", "ten"),
          ("11", "eleven"),
          ("12", "twelve"),
          ("13", "thirteen"),
          ("14", "fourteen"),
          ("15", "fifteen"),
          ("16", "sixteen"),
          ("17", "seventeen"),
          ("18", "eighteen"),
          ("19", "nineteen")
        )
      )
    }

    describe("tens") {
      describe("exact tens") {
        runTable(
          Table(
            ("input", "expected"),
            ("20", "twenty"),
            ("30", "thirty"),
            ("40", "fourty"),
            ("50", "fifty"),
            ("60", "sixty"),
            ("70", "seventy"),
            ("80", "eighty"),
            ("90", "ninety")
          )
        )
      }

      describe("tens with units") {
        runTable(
          Table(
            ("input", "expected"),
            ("22", "twenty two"),
            ("25", "twenty five"),
            ("31", "thirty one"),
            ("34", "thirty four"),
            ("43", "fourty three"),
            ("46", "fourty six"),
            ("59", "fifty nine"),
            ("57", "fifty seven"),
            ("68", "sixty eight"),
            ("63", "sixty three"),
            ("74", "seventy four"),
            ("76", "seventy six"),
            ("81", "eighty one"),
            ("85", "eighty five"),
            ("93", "ninety three"),
            ("99", "ninety nine")
          )
        )
      }
    }

    describe("hundreds") {
      describe("exact hundreds") {
        runTable(
          Table(
            ("input", "expected"),
            ("100", "one hundred"),
            ("200", "two hundred"),
            ("300", "three hundred"),
            ("400", "four hundred"),
            ("500", "five hundred"),
            ("600", "six hundred"),
            ("700", "seven hundred"),
            ("800", "eight hundred"),
            ("900", "nine hundred")
          )
        )
      }

      describe("non-exact hundreds") {
        runTable(
          Table(
            ("input", "expected"),
            ("120", "one hundred and twenty"),
            ("232", "two hundred and thirty two"),
            ("398", "three hundred and ninety eight"),
            ("443", "four hundred and fourty three"),
            ("519", "five hundred and nineteen"),
            ("689", "six hundred and eighty nine"),
            ("747", "seven hundred and fourty seven"),
            ("880", "eight hundred and eighty"),
            ("999", "nine hundred and ninety nine")
          )
        )
      }
    }

    describe("integer groups") {
      describe("skipping 0-only inner groups") {
        runTable(
          Table(
            ("input", "expected"),
            ("120000120", "one hundred and twenty million one hundred and twenty"),
            (
              "232000234444",
              "two hundred and thirty two billion two hundred and thirty four thousand four hundred and fourty four"
            ),
            ("398000000232", "three hundred and ninety eight billion two hundred and thirty two")
          )
        )
      }

      it("should use the correct name for each integer group") {
        fixture.integerToWords.convertDigits("1001001001001001001001001") should be(
          "one septillion one sextillion one quintillion one quadrillion one trillion one billion one million one thousand one"
        )
      }

      describe("some random numbers") {
        runTable(
          Table(
            ("input", "expected"),
            (
              "324987324",
              "three hundred and twenty four million nine hundred and eighty seven thousand three hundred and twenty four"
            ),
            (
              "23490",
              "twenty three thousand four hundred and ninety"
            ),
            (
              "432829034",
              "four hundred and thirty two million eight hundred and twenty nine thousand thirty four"
            ),
            (
              "324098320948",
              "three hundred and twenty four billion ninety eight million three hundred and twenty thousand nine hundred and fourty eight"
            ),
            (
              "4392840",
              "four million three hundred and ninety two thousand eight hundred and fourty"
            ),
            (
              "1231",
              "one thousand two hundred and thirty one"
            )
          )
        )
      }
    }
  }
}
