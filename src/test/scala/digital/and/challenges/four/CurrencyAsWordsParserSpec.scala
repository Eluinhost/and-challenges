package digital.and.challenges.four

import org.scalacheck.Gen
import org.scalactic.source.Position
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, Inside, ParallelTestExecution}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class CurrencyAsWordsParserSpec
    extends AnyFunSpec
    with should.Matchers
    with ScalaCheckDrivenPropertyChecks
    with TableDrivenPropertyChecks
    with MockFactory
    with ParallelTestExecution
    with Inside {

  val randomInteger: Gen[String] = for {
    length <- Gen.choose(1, 27)
    characters <- Gen.listOfN(length, Gen.numChar)
  } yield characters.mkString("")

  val randomFractional: Gen[String] = Gen.listOfN(2, Gen.numChar).map(_.mkString)

  // useful for `whenever` blocks so property testing shrinking in the forAll failures makes valid outputs
  def isValidIntegerInput(input: String): Boolean =
    input.length > 0 && input.length <= 27 && input.matches("^\\d+$")
  def isValidFractionalInput(input: String): Boolean =
    input.length == 2 && input.matches("^\\d+$")

  def fixture =
    new {
      val integerToWords = mock[IntegerToWords]
      val parser = new CurrencyAsWordsParser(integerToWords)
    }

  it(
    "should succeed for a random 10,000 random integers < 28 characters with no fractional part"
  ) {
    forAll((randomInteger, "integer"), minSuccessful(10000)) { (integer: String) =>
      whenever(isValidIntegerInput(integer)) {
        val f = fixture

        inAnyOrder {
          (f.integerToWords.convertDigits _).expects(integer.toCharArray.toSeq).once.returning("integer")
        }

        val output = f.parser.parse(integer)
        output shouldBe a[CurrencyAsWordsParser#Success[_]]
        output.get shouldBe "integer pounds"
      }
    }
  }

  it(
    "should succeed for a random 10,000 random integers < 28 characters with additional 2 character fractional part"
  ) {
    forAll((randomInteger, "integer"), (randomFractional, "fractional"), minSuccessful(10000)) {
      (integer: String, fractional: String) =>
        whenever(
          integer != fractional && fractional != "00" &&
            isValidIntegerInput(integer) && isValidFractionalInput(fractional)
        ) {
          val toParse = s"$integer.$fractional"

          val f = fixture

          inAnyOrder {
            (f.integerToWords.convertDigits _).expects(integer.toCharArray.toSeq).once.returning("integer")
            (f.integerToWords.convertDigits _).expects(fractional.toCharArray.toSeq).once.returning("fractional")
          }

          val output = f.parser.parse(toParse)
          output shouldBe a[CurrencyAsWordsParser#Success[_]]
          output.get shouldBe "integer pounds and fractional pence"
        }
    }
  }

  describe("should have the correct failures") {
    forAll(
      Table(
        ("input", "message", "longString"),
        (
          ".23",
          "Expected a digit but found '.' instead",
          """|.23
             |^""".stripMargin
        ),
        (
          "",
          "Expected a digit but reached end of input instead.",
          """|
             |^""".stripMargin
        ),
        (
          "1545465465456456465456431577.21",
          "Number too long to parse, we don't have a name for that..., can only handle 27 digits total",
          """|1545465465456456465456431577.21
             |                            ^""".stripMargin
        )
      )
    ) { (input: String, message: String, longString: String) =>
      it(s"should error for '$input' with message '$message'") {
        val f = fixture

        val result = f.parser.parse(input)

        inside(result) {
          case x: f.parser.Failure =>
            x.msg shouldBe message
            x.next.pos.longString shouldBe longString
        }
      }
    }
  }

  describe("should have the correct errors") {
    forAll(
      Table(
        ("input", "message", "longString"),
        (
          "1.1",
          "Expected a digit but reached end of input instead.",
          """|1.1
             |   ^""".stripMargin
        ),
        (
          "1.144",
          "Expected end of input, found '4' instead",
          """|1.144
             |    ^""".stripMargin
        ),
        (
          "1.144467486241",
          "Expected end of input, found '4' instead",
          """|1.144467486241
             |    ^""".stripMargin
        ),
        (
          "1.a4",
          "Expected a digit but found 'a' instead",
          """|1.a4
             |  ^""".stripMargin
        ),
        (
          "1a.54",
          "Expected end of input, found 'a' instead",
          """|1a.54
             | ^""".stripMargin
        )
      )
    ) { (input: String, message: String, longString: String) =>
      it(s"should error for '$input' with message '$message'") {
        val f = fixture

        (f.integerToWords.convertDigits _).expects(*).anyNumberOfTimes().returning("integer")

        val result = f.parser.parse(input)

        inside(result) {
          case x: f.parser.Error =>
            x.msg shouldBe message
            x.next.pos.longString shouldBe longString
        }
      }
    }
  }

  describe("pounds and pence") {
    it("should include pence value when fractional is not .00") {
      val f = fixture

      (f.integerToWords.convertDigits _).expects(Seq('1')).once().returning("integer")
      (f.integerToWords.convertDigits _).expects(Seq('2', '3')).once().returning("fractional")

      val result = f.parser.parse("1.23")

      inside(result) {
        case x: f.parser.Success[_] =>
          x.get shouldBe "integer pounds and fractional pence"
      }
    }

    it("should not include pence value when fractional is .00") {
      val f = fixture

      (f.integerToWords.convertDigits _).expects(Seq('1', '2', '3', '4', '4', '2')).once().returning("integer")

      val result = f.parser.parse("123442.00")

      inside(result) {
        case x: f.parser.Success[_] =>
          x.get shouldBe "integer pounds"
      }
    }

    it("should not include pence value when no fractional") {
      val f = fixture

      (f.integerToWords.convertDigits _).expects(Seq('2', '4', '3')).once().returning("integer")

      val result = f.parser.parse("243")

      inside(result) {
        case x: f.parser.Success[_] =>
          x.get shouldBe "integer pounds"
      }
    }
  }
}
