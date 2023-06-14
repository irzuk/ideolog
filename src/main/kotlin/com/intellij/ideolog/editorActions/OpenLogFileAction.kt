package com.intellij.ideolog.editorActions
// package com.intellij.ide.scratch;
import org.jetbrains.annotations.NotNull
import com.intellij.featureStatistics.FeatureUsageTracker
import com.intellij.ide.scratch.*
import com.intellij.ide.util.PsiNavigationSupport
import com.intellij.idea.ActionsBundle
import com.intellij.ideolog.fileType.LogFileType
import com.intellij.ideolog.fileType.LogLanguage
import com.intellij.lang.Language
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiUtilCore
import com.intellij.util.ArrayUtil
import com.intellij.util.Consumer
import com.intellij.util.ObjectUtils
import com.intellij.util.PathUtil
import java.util.*

class OpenLogFileAction : ScratchFileActions.NewFileAction() {
  override fun update(e: AnActionEvent) {
    e.presentation.isEnabled = canExecute(e)
  }

  private fun canExecute(e: AnActionEvent) = true

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val context = createContext(e, project) ?: return
    val consumer = Consumer { l: Language? ->
      context.language = l
      ScratchFileCreationHelper.EXTENSION.forLanguage(context.language).prepareText(
        project, context, DataContext.EMPTY_CONTEXT)
      doCreateNewScratch(project, context)
    }

    if (context.language != null) {
      consumer.consume(context.language)
    } else {
      LRUPopupBuilder.forFileLanguages(project, ActionsBundle.message("action.NewScratchFile.text.with.new"), null, consumer).showCenteredInCurrentWindow(project)
    }
  }

  @NotNull
  fun createContext(e: AnActionEvent, project: Project): ScratchFileCreationHelper.Context? {
    val context = ScratchFileCreationHelper.Context()
    context.text = StringUtil.notNullize("text")
    if (context.text.isNotEmpty()) {
      context.language = LogLanguage
    }
    context.ideView = e.getData(LangDataKeys.IDE_VIEW)
    return context
  }

  private fun doCreateNewScratch(project: Project, context: ScratchFileCreationHelper.Context): PsiFile? {
    FeatureUsageTracker.getInstance().triggerFeatureUsed("scratch")
    val language = Objects.requireNonNull(context.language)
    if (context.fileExtension == null) {
      val fileType = LogFileType
      context.fileExtension = fileType.defaultExtension ?: ""
    }
    ScratchFileCreationHelper.EXTENSION.forLanguage(language).beforeCreate(project, context)
    val dir = if (context.ideView != null) PsiUtilCore.getVirtualFile(ArrayUtil.getFirstElement(context.ideView.directories)) else null
    val rootType = if (dir == null) null else ScratchFileService.findRootType(dir)
    val relativePath = (if (rootType !== ScratchRootType.getInstance()) "" else FileUtil.getRelativePath(ScratchFileService.getInstance().getRootPath(rootType), dir!!.path, '/'))!!
    val fileName = (if (StringUtil.isEmpty(relativePath)) "" else "$relativePath/") + "logsFromKibana.log"
    val file = ScratchRootType.getInstance().createScratchFile(
      project, fileName, language, context.text, context.createOption)
      ?: return null
    PsiNavigationSupport.getInstance().createNavigatable(project, file, context.caretOffset).navigate(true)
    val psiFile = PsiManager.getInstance(project).findFile(file)
    if (context.ideView != null && psiFile != null) {
      context.ideView.selectElement(psiFile)
    }
    return psiFile
  }

}
