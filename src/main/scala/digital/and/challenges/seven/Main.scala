package digital.and.challenges.seven

import com.googlecode.lanterna.terminal.DefaultTerminalFactory

object Main extends App {
  val result = new FancyDecoder(
    terminal = new DefaultTerminalFactory().createTerminal(),
    gridSource = "challenge-seven.txt",
    tickTime = 15
  ).decode("TmixcmCZhxDmrmoaQhxhzmfCDnohyehhxW")

  println(s"Result: $result")

//  val result =
//    new FastDecoder(terminal = new DefaultTerminalFactory().createTerminal(), gridSource = "challenge-seven.txt")
//      .decode("TmixcmCZhxDmrmoaQhxhzmfCDnohyehhxW")
}
