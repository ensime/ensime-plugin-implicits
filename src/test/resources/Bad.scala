import shapeless._

class Bad {
  implicit val b@bar@ar = cachedImplicit[Int]
}
