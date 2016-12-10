package org.ensime.plugin

import org.scalatest._
import org.ensime.pcplod._

class EnsimeImplicitPluginSpec extends FlatSpec with Matchers {
  "EnsimeImplicitPlugin" should "replace simple cachedImplicit" in {
    withMrPlod("Simple1.scala") { mr =>
      println(mr.messages)

      val foo = List()

    }
  }
}
