
class Simple1 {
//  val foo = cachedImplicit[Int]
  val bar: Int = cachedImplicit

  def cachedImplicit[T]: T = ???
}
