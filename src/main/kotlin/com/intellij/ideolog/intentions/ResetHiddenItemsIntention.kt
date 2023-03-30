package com.intellij.ideolog.intentions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.ideolog.fileType.LogFileType
import com.intellij.ideolog.foldings.*
import com.intellij.ideolog.util.ideologContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.util.containers.isNullOrEmpty

class ResetHiddenItemsIntention : IntentionAction {
  override fun getText() = "Restore all hidden lines"

  override fun getFamilyName() = "Logs"

  override fun isAvailable(project: Project, editor: Editor, file: PsiFile?): Boolean {
    if (file?.fileType != LogFileType)
      return false

    val context = editor.document.ideologContext
    val hasHiddenItems = !context.hiddenItems.isNullOrEmpty()
    val hasHiddenSubstrings = !context.hiddenSubstrings.isNullOrEmpty()
    val hasWhitelistedSubstrings = !context.whitelistedSubstrings.isNullOrEmpty()
    val hasWhitelistedItems = !context.whitelistedItems.isNullOrEmpty()

    return hasHiddenItems || hasHiddenSubstrings || hasWhitelistedSubstrings || hasWhitelistedItems || context.hideLinesAbove >= 0 || context.hideLinesBelow < Int.MAX_VALUE || context.hideColumnsLeft > -1
  }

  override fun invoke(project: Project, editor: Editor, file: PsiFile?) {
    val context = editor.document.ideologContext
    context.hiddenItems.clear()
    context.hiddenSubstrings.clear()
    context.whitelistedItems.clear()
    context.whitelistedSubstrings.clear()

    context.hideLinesAbove = -1
    context.hideLinesBelow = Int.MAX_VALUE
    context.hideColumnsLeft = -1

    FoldingCalculatorTask.restartFoldingCalculator(project, editor, file)
  }

  override fun startInWriteAction() = false
}
