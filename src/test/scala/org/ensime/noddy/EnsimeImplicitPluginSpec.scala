// Copyright: 2016 - 2017 https://github.com/ensime/ensime-plugin-implicits/graphs
// License: http://www.apache.org/licenses/LICENSE-2.0
package org.ensime.plugin

import org.ensime.pcplod._
import org.ensime.pcplod.PcMessageSeverity._
import org.scalatest._
import org.scalatest.Matchers._
import org.slf4j.bridge.SLF4JBridgeHandler

class EnsimeImplicitPluginSpec extends FlatSpec {
  SLF4JBridgeHandler.removeHandlersForRootLogger()
  SLF4JBridgeHandler.install()

  "EnsimeImplicitPlugin" should "replace simple cachedImplicit" in withMrPlod("Simple.scala") { mr =>
    mr.messages should contain theSameElementsAs Seq(
      PcMessage("Simple.scala", Warning, "`cachedImplicit` is not doing any work (ensime plugin)"),
      PcMessage("Simple.scala", Warning, "Unused import") // false positive
    )

    mr.typeAtPoint('bar) shouldBe Some("String")
  }

  it should "replace primitive cachedImplicit" in withMrPlod("Primitive.scala") { mr =>
    mr.messages should contain theSameElementsAs Seq(
      PcMessage("Primitive.scala", Warning, "`cachedImplicit` is not doing any work (ensime plugin)"),
      PcMessage("Primitive.scala", Warning, "Unused import") // false positive
    )

    mr.typeAtPoint('bar) shouldBe Some("Int")
  }

  it should "refuse to accept missing type parameters" in withMrPlod("Bad.scala") { mr =>
    mr.messages should contain theSameElementsAs Seq(
      PcMessage("Bad.scala", Error, "Missing explicit type on implicit value when calling `cachedImplicit`"),
      PcMessage("Bad.scala", Error, "Could not find an implicit value of type Int to cache")
    )

    mr.typeAtPoint('bar) shouldBe Some("<error>")
  }
}
