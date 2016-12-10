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
    def debug(name: String, tree: Tree): Unit = {
      global.reporter.warning(tree.pos, s"$name: ${showCode(tree)}\n${showRaw(tree)}")
    }

    override def transform = {
      case tree @ ValDef(mods, name, tpt: TypeTree, TypeApply(Ident(CachedImplicit), _)) =>
        if (tpt.original == EmptyTree || (tpt.original eq null)) {
          reporter.error(tree.pos, s"Missing explicit type in call to `cachedImplicit`.")
          tree
        } else {
          reporter.warning(tree.pos, s"This `cachedImplicit` call is not doing any work.")
          treeCopy.ValDef(tree, mods, name, tpt, Literal(Constant(null)))
        }

      case tree @ ValDef(mods, name, tpt, Ident(CachedImplicit)) =>
        println("matched!")
        if (tpt == EmptyTree) {
          reporter.error(tree.pos, s"Missing explicit type in call to `cachedImplicit`.")
          tree
        } else {
          reporter.warning(tree.pos, s"This `cachedImplicit` call is not doing any work.")
          treeCopy.ValDef(tree, mods, name, tpt, q"null.asInstanceOf[$tpt]")
        }

      case t =>
        //debug("", t);
        t
    }
  }

  override val components = List(NullCachedImplicit)
}
