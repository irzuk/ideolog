package com.intellij.ideolog.intentions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.ideolog.fileType.LogFileType
import com.intellij.ideolog.foldings.FoldingCalculatorTask
import com.intellij.ideolog.highlighting.LogParsingUtils
import com.intellij.ideolog.util.IdeologDocumentContext
import com.intellij.ideolog.util.ideologContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

abstract class HideColumnsLeftRightIntentionBase(val setter: (IdeologDocumentContext, Int) -> Unit, private val directionText: String) : IntentionAction {
  override fun getText() = "Hide columns $directionText"

  override fun getFamilyName() = "Logs"

  override fun isAvailable(project: Project, editor: Editor, file: PsiFile?) = file?.fileType == LogFileType

  override fun invoke(project: Project, editor: Editor, file: PsiFile?) {
    val column = LogParsingUtils.getColumnByOffset(editor)
    setter(editor.document.ideologContext, column)
    FoldingCalculatorTask.restartFoldingCalculator(project, editor, file)
  }

  override fun startInWriteAction(): Boolean {
    return false
  }
}

class HideColumnsLeftIntention: HideColumnsLeftRightIntentionBase({ context, column -> context.hideColumnsLeft = column }, "left")
class HideColumnsRightIntention: HideColumnsLeftRightIntentionBase({ context, column -> context.hideColumnsRight = column }, "right")
