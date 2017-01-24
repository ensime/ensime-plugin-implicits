import shapeless._

class Primitive {
  implicit val b@bar@ar: Int = cachedImplicit

  def doBar(implicit i: Int): Unit = ???
  doBar
}
