package digital.and.challenges.six

import scala.collection.mutable

class DawgDictionary extends Dictionary {
  private[this] case class StackItem(current: DawgNode, char: Char, parent: DawgNode)

  private[this] val root = new DawgNode

  override def insert(word: String): DawgDictionary = {
    var node = root

    for (char <- word.toLowerCase) {
      node = node.children.getOrElseUpdate(char, new DawgNode)
    }

    node.isWord = true
    this
  }

  override def insertAll(words: Seq[String]): DawgDictionary = {
    words.foreach(insert)
    this
  }

  override def spellcheck(word: String): AutocompleteResult = {
    var node = root
    var validChars = 0

    for (char <- word) {
      node.children.get(char) match {
        // child exists, keep iterating
        case Some(child) =>
          node = child
          validChars += 1
        // child doesn't exist so this char onwards is invalid
        // start iterating to collate all future possibilities
        case None =>
          return InvalidWord(partial = word.substring(0, validChars), suffixes = collectSuffixes(node))
      }
    }

    if (node.isWord) {
      ValidWord
    } else {
      InvalidWord(partial = word.substring(0, validChars), suffixes = collectSuffixes(node))
    }
  }

  override def isWord(word: String): Boolean = {
    var node = root

    for (char <- word) {
      node.children.get(char) match {
        case Some(child) =>
          node = child
        case None =>
          return false
      }
    }

    node.isWord
  }

  def minimise(): DawgDictionary = {
    val suffixTree = mutable.Map.empty[Char, mutable.ListBuffer[DawgNode]]

    val stack = generateStack

    while (stack.nonEmpty) {
      val StackItem(current, char, parent) = stack.pop()

      // lookup the available suffixes for this char in the tree
      suffixTree.get(char) match {
        case Some(suffixes) =>
          suffixes.find(_ == current) match {
            // if it's a duplicate node we can replace ourselves in the
            // parent node with the found node instead
            case Some(suffix) => parent.children.put(char, suffix)
            // not a duplicate node so we add ourselves to the available options
            case None => suffixes.addOne(current)
          }
        case None =>
          // no found suffixes for this char yet, add current node to the available options for this char
          suffixTree.put(char, mutable.ListBuffer(current))
      }
    }

    // we done bois
    this
  }

  def size: (Int, Int) = {
    val foundIds = mutable.Set.empty[Int]
    var edgeCount = 0

    def collectIds(node: DawgNode): Unit = {
      for (child <- node.children.values) {
        if (!foundIds.contains(child.id)) {
          foundIds.add(child.id)
          edgeCount += child.children.size
        }
        collectIds(child)
      }
    }

    collectIds(root)

    (foundIds.size, edgeCount)
  }

  // really only useful for small sample testing, pointless running for a full dict
  def printTree(node: DawgNode = this.root, char: Char = '*', prefix: String = ""): Unit = {
    println(s"$prefix$char -> (${node.id}): ${node.isWord}")

    for ((char, child) <- node.children) {
      printTree(child, char, s"$prefix  ")
    }
  }

  private[this] def generateStack: mutable.Stack[StackItem] = {
    var node = root
    val stack = mutable.Stack.empty[StackItem]

    val depthStack = mutable.Stack(node)
    while (depthStack.nonEmpty) {
      node = depthStack.pop()

      for ((key, child) <- node.children) {
        stack.push(StackItem(current = child, char = key, parent = node))
        depthStack.push(child)
      }
    }

    stack
  }

  private[this] def collectSuffixes(node: DawgNode): List[String] = {
    val buffer = mutable.ListBuffer.empty[String]

    def recurse(node: DawgNode, prefix: String): Unit = {
      if (node.isWord) {
        buffer.addOne(prefix)
      }

      for ((char, child) <- node.children) {
        recurse(child, prefix + char)
      }
    }

    recurse(node, "")

    buffer.toList
  }
}
