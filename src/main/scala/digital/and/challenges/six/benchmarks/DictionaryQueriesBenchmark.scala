package digital.and.challenges.six.benchmarks

import java.util.concurrent.TimeUnit

import digital.and.challenges.six.{AutocompleteResult, DawgDictionary, Dictionary, NaiveDictionary}
import org.openjdk.jmh.annotations._

import scala.io.Source
import scala.util.Random

object DictionaryState {
  val lines: List[String] = Source.fromResource("challenge-six.txt").getLines.toList
  val misspellings: List[String] = Source.fromResource("challenge-six-misspellings.txt").getLines.toList

  @State(Scope.Thread)
  class NaiveDictionaryState {
    var dictionary: Dictionary = _

    @Setup
    def up(): Unit = dictionary = new NaiveDictionary().insertAll(lines)

    @TearDown
    def down(): Unit = dictionary = null
  }

  @State(Scope.Thread)
  class TrieDictionaryState {
    var dictionary: Dictionary = _

    @Setup
    def up(): Unit = dictionary = new DawgDictionary().insertAll(lines)

    @TearDown
    def down(): Unit = dictionary = null
  }

  @State(Scope.Thread)
  class DAWGDictionaryState {
    var dictionary: DawgDictionary = _

    @Setup
    def up(): Unit = dictionary = new DawgDictionary().insertAll(lines).minimise()

    @TearDown
    def down(): Unit = dictionary = null
  }

  @State(Scope.Benchmark)
  class ValidWords {
    var list: List[String] = _

    @Setup
    def up(): Unit = list = Random.shuffle(lines).take(10_000)
  }

  @State(Scope.Benchmark)
  class InvalidWords {
    var list: List[String] = _

    @Setup
    def up(): Unit = list = Random.shuffle(misspellings).map(_.toLowerCase).take(10_000)
  }
}

@BenchmarkMode(Array(Mode.Throughput))
@Warmup(time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(time = 10, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.SECONDS)
class IsValidWordWithValidWordsBenchmark {
  import DictionaryState._

  @Benchmark
  @OperationsPerInvocation(10_000)
  def naiveDictionary(state: NaiveDictionaryState, words: ValidWords): List[Boolean] =
    words.list.map(state.dictionary.isWord)

  @Benchmark
  @OperationsPerInvocation(10_000)
  def trieDictionary(state: TrieDictionaryState, words: ValidWords): List[Boolean] =
    words.list.map(state.dictionary.isWord)

  @Benchmark
  @OperationsPerInvocation(10_000)
  def dawgDictionary(state: DAWGDictionaryState, words: ValidWords): List[Boolean] =
    words.list.map(state.dictionary.isWord)
}

@BenchmarkMode(Array(Mode.Throughput))
@Warmup(time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(time = 10, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.SECONDS)
class IsValidWordWithInvalidWordsBenchmark {
  import DictionaryState._

  @Benchmark
  @OperationsPerInvocation(10_000)
  def naiveDictionary(state: NaiveDictionaryState, words: InvalidWords): List[Boolean] =
    words.list.map(state.dictionary.isWord)

  @Benchmark
  @OperationsPerInvocation(10_000)
  def trieDictionary(state: TrieDictionaryState, words: InvalidWords): List[Boolean] =
    words.list.map(state.dictionary.isWord)

  @Benchmark
  @OperationsPerInvocation(10_000)
  def dawgDictionary(state: DAWGDictionaryState, words: InvalidWords): List[Boolean] =
    words.list.map(state.dictionary.isWord)
}

@BenchmarkMode(Array(Mode.Throughput))
@Warmup(time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(time = 10, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.SECONDS)
class AutocompleteValidWordsBenchmark {
  import DictionaryState._

  @Benchmark
  @OperationsPerInvocation(10_000)
  def naiveDictionary(
      state: NaiveDictionaryState,
      words: ValidWords
  ): List[AutocompleteResult] =
    words.list.map(state.dictionary.spellcheck)

  @Benchmark
  @OperationsPerInvocation(10_000)
  def trieDictionary(
      state: TrieDictionaryState,
      words: ValidWords
  ): List[AutocompleteResult] =
    words.list.map(state.dictionary.spellcheck)

  @Benchmark
  @OperationsPerInvocation(10_000)
  def dawgDictionary(
      state: DAWGDictionaryState,
      words: ValidWords
  ): List[AutocompleteResult] =
    words.list.map(state.dictionary.spellcheck)
}

@BenchmarkMode(Array(Mode.Throughput))
@Warmup(time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(time = 10, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.SECONDS)
class AutocompleteInvalidWordsBenchmark {
  import DictionaryState._

  @Benchmark
  @OperationsPerInvocation(10_000)
  def naiveDictionary(
      state: NaiveDictionaryState,
      words: InvalidWords
  ): List[AutocompleteResult] =
    words.list.map(state.dictionary.spellcheck)

  @Benchmark
  @OperationsPerInvocation(10_000)
  def trieDictionary(
      state: TrieDictionaryState,
      words: InvalidWords
  ): List[AutocompleteResult] =
    words.list.map(state.dictionary.spellcheck)

  @Benchmark
  @OperationsPerInvocation(10_000)
  def dawgDictionary(
      state: DAWGDictionaryState,
      words: InvalidWords
  ): List[AutocompleteResult] =
    words.list.map(state.dictionary.spellcheck)
}
