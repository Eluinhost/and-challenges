package digital.and.challenges.six.benchmarks

object TimeBlock {
  def time[R](message: Long => String)(block: => R): Unit = {
    val start = System.nanoTime()
    block
    val end = System.nanoTime()

    println(message(end - start))
  }
}
