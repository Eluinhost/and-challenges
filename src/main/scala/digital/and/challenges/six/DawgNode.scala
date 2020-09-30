package digital.and.challenges.six

import scala.collection.mutable

object DawgNode {
  private[this] var currentNodeId: Int = 0

  private def getNextNodeId: Int = {
    val id = currentNodeId
    currentNodeId += 1
    id
  }
}

@SerialVersionUID(100L)
class DawgNode extends Serializable {
  val id: Int = DawgNode.getNextNodeId
  var isWord = false
  val children: mutable.Map[Char, DawgNode] = mutable.Map.empty

  // nodes are equal if they have the same edges and share the 'isWord' flag
  override def equals(other: Any): Boolean = {
    other match {
      case node: DawgNode =>
        isWord == node.isWord && children == node.children
      case _ => false
    }
  }

  // for debugger
  override def toString: String = s"{Node#$id, isWord=$isWord, children keys: ${children.keySet.mkString(",")}}"
}
