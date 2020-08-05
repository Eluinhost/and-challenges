package digital.and.challenges.two

import scala.io.Source

// "Some people, when confronted with a problem, think “I know, I'll use regular expressions.” Now they have two problems."
object Main extends App {
  val output = Source.fromResource("challenge-two.txt").getLines().next().replaceAll(""".{1,32}(?<=\s|\S-|$)""", "$0\n")

  // this answer comes out at 64, which was 1 off of the submit answer but idk why 🤔,
  // removing the newlines ends up with the original string and no lines are above 32
  println(output, output.linesIterator.count(_.length == 32))
}

// regex explanation (I may be wrong, regex is fun...):
//
// .{1,32}       # 1 up to 32 of any character
// (?<=          # look behind (basically previous must end in this regex):
//   \s|\S-|$    # either a whitespace char, a non-whitespace char followed by a hypen, or the end of the string
// )
//
// we then replace matches in the string with $0\n, meaning whatever was matched then a newline
//
// alternative: (.{1,32})(?:\s+|(?:<=\S-)|$) -> $1\n
// same but without the trailing whitespace being counted as part of the 32 and also then not included in output, makes
// for some more 'full' lines of 32 characters and a harder to understand regex
