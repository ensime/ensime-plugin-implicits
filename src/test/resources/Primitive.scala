// Copyright: 2016 - 2017 https://github.com/ensime/ensime-plugin-implicits/graphs
// License: http://www.apache.org/licenses/LICENSE-2.0
import shapeless._

class Primitive {
  implicit val b@bar@ar: Int = cachedImplicit

  def doBar(implicit i: Int): Unit = ???
  doBar
}
