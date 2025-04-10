package io.tolgee.formats.paramConvertors.out

import io.tolgee.formats.FromIcuPlaceholderConvertor
import io.tolgee.formats.MessagePatternUtil
import io.tolgee.formats.escapePercentSign

class IcuToPhpPlaceholderConvertor : FromIcuPlaceholderConvertor {
  private val baseToCLikePlaceholderConvertor =
    BaseToCLikePlaceholderConvertor(
      defaultSpecifier = "s",
      argNameStringProvider = null,
    )

  override fun convert(node: MessagePatternUtil.ArgNode): String = baseToCLikePlaceholderConvertor.convert(node)

  override fun convertText(
    node: MessagePatternUtil.TextNode,
    keepEscaping: Boolean,
  ): String = escapePercentSign(node.getText(keepEscaping))

  override fun convertReplaceNumber(
    node: MessagePatternUtil.MessageContentsNode,
    argName: String?,
  ): String = "%d"
}
