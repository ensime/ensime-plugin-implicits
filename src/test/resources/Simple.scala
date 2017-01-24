import shapeless._

class Simple {
  implicit val b@bar@ar: String = cachedImplicit

  def doBar(implicit i: String): Unit = ???
  doBar
}
