package com.intellij.ideolog.intentions

import com.intellij.ideolog.highlighting.LogParsingUtils
import com.intellij.openapi.editor.CaretState
import com.intellij.openapi.editor.FoldRegion
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.impl.FoldRegionImpl
import com.intellij.testFramework.UsefulTestCase
import com.intellij.testFramework.builders.ModuleFixtureBuilder
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase
import org.junit.jupiter.api.Test

internal class HideColumnsTest() : CodeInsightFixtureTestCase<ModuleFixtureBuilder<*>>() {

  @Test
  fun testEditorFoldings() {
    assert(myFixture != null)
    val psifile = myFixture.configureByFile("test/kotlin/com/intellij/ideolog/intentions/files_for_tests/HideColumnLeft.log")
    //val VirtualFile get() = myFixture.findFileInTempDir(".")

    //protected fun FileTree.create(): TestProject = create(project, projectDir)

    val editor = myFixture.editor
    val i = HideColumnsLeftIntention()
    //
    editor.caretModel.caretsAndSelections = listOf(CaretState(LogicalPosition(0,2),LogicalPosition(0,2),LogicalPosition(0,2)))
    i.invoke(myFixture.project,editor,psifile)
    val foldRegions = editor.foldingModel.allFoldRegions
    println(foldRegions[0])
    //UsefulTestCase.assertSameElements(foldRegions,)
}
}
