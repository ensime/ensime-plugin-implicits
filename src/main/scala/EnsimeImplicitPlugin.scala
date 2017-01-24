// Copyright: 2016 https://github.com/ensime/ensime-plugin-implicits/graphs
// License: http://www.apache.org/licenses/LICENSE-2.0
package org.ensime.plugin

import scala.tools.nsc._
import scala.tools.nsc.plugins._
import scala.tools.nsc.transform._

class EnsimeImplicitPlugin(override val global: Global) extends Plugin {
  override val description: String = "ignores cachedImplicit"
  override val name: String = "ensime-implicits"

  abstract class TransformingComponent(override val global: Global)
    extends PluginComponent
    with TypingTransformers {

    override def newPhase(prev: Phase): Phase = new StdPhase(prev) {
      override def apply(unit: global.CompilationUnit): Unit = newTransformer(unit).transformUnit(unit)
    }
    override val runsAfter: List[String] = "parser" :: Nil
    override val runsBefore: List[String] = "namer" :: Nil

    def newTransformer(unit: global.CompilationUnit) = new TypingTransformer(unit) {
      override def transform(tree: global.Tree): global.Tree = {
        TransformingComponent.this.transform(super.transform(tree))
      }
    }

    def transform: global.Tree => global.Tree
  }

  /**
   * Replace all calls to cachedImplicit with `null`. This makes the presentation compiler
   * faster, at the expense of not type-checking that the said implicit actually exists.
   */
  private val NullCachedImplicit = new TransformingComponent(global) {
    import global._

    private val CachedImplicit = newTermName("cachedImplicit")

    override val phaseName: String = "null-cachedImplicit"

    // best way to inspect a tree, just call this
    private def debug(name: String, tree: Tree): Unit = {
      println(s"$name ${tree.id} ${tree.pos}: ${showCode(tree)}\n${showRaw(tree)}")
    }

    private def missingExplicitType(t: Tree) = reporter.error(t.pos, "Missing explicit type on implicit value when calling `cachedImplicit`")
    private def notDoingAnyWork(t: Tree) = reporter.warning(t.pos, s"`cachedImplicit` is not doing any work (ensime plugin)")

    override def transform = {
      case tree @ ValDef(mods, name, tpt: TypeTree, TypeApply(Ident(CachedImplicit), _)) =>
        if (tpt.original == EmptyTree || (tpt.original eq null)) {
          missingExplicitType(tree)
          tree
        } else {
          notDoingAnyWork(tree)
          treeCopy.ValDef(tree, mods, name, tpt, atPos(tree.rhs.pos) { Literal(Constant(null)) })
        }

      case tree @ ValDef(mods, name, tpt, Ident(CachedImplicit)) =>
        if (tpt == EmptyTree) {
          missingExplicitType(tree)
          tree
        } else {
          notDoingAnyWork(tree)
          val target = tpt.duplicate.setPos(NoPosition)
          treeCopy.ValDef(tree, mods, name, tpt, atPos(tree.rhs.pos) { q"null.asInstanceOf[$target]" })
        }

      case t =>
        t
    }
  }

  override val components = List(NullCachedImplicit)
}
