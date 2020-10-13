package digital.and.challenges.seven

case class Vector2(x: Int, y: Int) {
  def add(vector: Vector2): Vector2 = Vector2(x + vector.x, y + vector.y)
}
