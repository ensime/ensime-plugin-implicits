package org.ensime.plugin

import org.scalatest._
import org.ensime.pcplod._

class EnsimeImplicitPluginSpec extends FlatSpec with Matchers {
  "EnsimeImplicitPlugin" should "replace simple cachedImplicit" in {
    withPcPlod { pc =>
      pc.loadScala("Simple1.scala")
      println(pc.messages)
    }
  }
}
