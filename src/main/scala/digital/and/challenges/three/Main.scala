package digital.and.challenges.three

import java.lang.reflect.{Field, Modifier}
import java.nio.file.Files
import java.util.Collections

import scala.io.Source
import scala.language.postfixOps
import scala.sys.process._
import scala.util.Random

object Main extends App {
  val file = Files.write(Files.createTempFile("a", ".file"), Source.fromResource("digital/and/challenges/three/a.js").getLines().mkString("\n").getBytes())

  val clazz = Class.forName("java.lang.Integer$IntegerCache")
  val field = clazz.getDeclaredField("cache")
  field.setAccessible(true)
  val modifiersField = classOf[Field].getDeclaredField("modifiers")
  modifiersField.setAccessible(true)
  modifiersField.setInt(field, field.getModifiers & ~Modifier.FINAL)
  val orig = field.get(clazz).asInstanceOf[Array[Integer]]
  val n: Array[Integer] = orig.map(x => if (x == 11) new Integer(2) else if (x == 2) new Integer(11) else x)
  val expected = (1 + 1): Integer
  field.set(null, n)

  // alternative, this broke the JVM too much for me
  //  val random = new Random()
  //  val n: Array[Integer] = random.shuffle(orig.toList).toArray

  do {
    val answer = s"""node ${file.toAbsolutePath}""".run(true).exitValue()

    if (answer == expected) {
      println(s"value of 1 + 1 is: $answer")
      System.exit(answer)
    } else {
      println(s"No, sorry $answer is not correct, try again")
    }
  } while (true)
}

// Process:

// A: writes the JS file to a temp file so node can run it later

// B: IntCache modification
//   - we swap the intcache results for 2 <-> 11 so casting picks the wrong one
//   - we store the result of 1 + 1 as an integer (2) BEFORE modification

// C: Run the JS file
//   - asks node on the copied file containing 'jsfucked' code (check out a.js...), check 'de-fucked-a.js' for actual contents
//   - this code prompts the user 'To prove you are a human solve the following formula: 1 + 1,
//   - reads the users response, and exits with that status code.

// D: we then check if the answer matches our expected integer, because of int cache modification 11 <-> 2 now so only
// the answer of 11 will work now and 2 will not.
