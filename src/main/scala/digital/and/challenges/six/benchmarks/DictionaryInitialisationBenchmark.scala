package digital.and.challenges.six.benchmarks

import java.util.concurrent.TimeUnit

import digital.and.challenges.six.{DawgDictionary, Dictionary, NaiveDictionary}
import org.openjdk.jmh.annotations.{
  Benchmark,
  BenchmarkMode,
  Fork,
  Measurement,
  Mode,
  OutputTimeUnit,
  Scope,
  Setup,
  State,
  Threads,
  Warmup
}

import scala.io.Source

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@Warmup(time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(time = 10, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
class DictionaryInitialisationBenchmark {
  var words: List[String] = _

  @Setup
  def prepare(): Unit = {
    words = Source.fromResource("challenge-six.txt").getLines.toList
  }

  @Benchmark
  def naive: Dictionary = {
    val dictionary = new NaiveDictionary
    words.foreach(dictionary.insert)
    dictionary
  }

  @Benchmark
  def basicTrie: Dictionary = {
    val dictionary = new DawgDictionary
    words.foreach(dictionary.insert)
    dictionary
  }

  // this one takes _coniderably_ longer to run (around 5-6 seconds on my machine to minimize so we run the
  // benchmark iterations over 5 minutes each instead to get a better sample (hope you have some spare time)
  @Benchmark
  @Warmup(time = 5, timeUnit = TimeUnit.MINUTES)
  @Measurement(time = 5, timeUnit = TimeUnit.MINUTES)
  def dawg: Dictionary = {
    val dictionary = new DawgDictionary
    words.foreach(dictionary.insert)
    dictionary.minimise()
    dictionary
  }
}

//@State(Scope.Thread)
//@OutputTimeUnit(TimeUnit.MICROSECONDS)
//@BenchmarkMode(Array(Mode.AverageTime))
//class Insert {
//  var words: List[String] = _
//
//  @Setup
//  def prepare(): Unit = {
//    words = Source.fromResource("challenge-six.txt").getLines.toList
//  }
//
//  @Benchmark
//  def intoNaiveImplementation(): Dictionary = {
//    val dict = new NaiveDictionary
//
//    words.foreach(dict.insert)
//
//    dict
//  }
//
//  @Benchmark
//  def intoTrieImplementation(): Dictionary = {
//    val dict = new DawgDictionary
//
//    words.foreach(dict.insert)
//
//    dict
//  }
//}
//
//@State(Scope.Thread)
//@OutputTimeUnit(TimeUnit.MICROSECONDS)
//@BenchmarkMode(Array(Mode.AverageTime))
//class IsWord {
//  var words: List[String] = _
//  var random: Random = _
//
//  var naive: NaiveDictionary = _
//  var trie: DawgDictionary = _
//
//  @Setup
//  def prepare(): Unit = {
//    random = new Random
//
//    words = Source.fromResource("challenge-six.txt").getLines.toList
//
//    naive = new NaiveDictionary
//    trie = new DawgDictionary
//
//    words.foreach { line =>
//      naive.insert(line)
//      trie.insert(line)
//    }
//  }
//
//  @Benchmark
//  def withNaiveImplementation(): Boolean =
//    naive.isWord(words(random.nextInt(words.length)))
//
//  @Benchmark
//  def withTrieImplementation(): Boolean =
//    trie.isWord(words(random.nextInt(words.length)))
//}
