package com.cz.convert

import com.cz.model.plugin.delegate.MessageDelegate
import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.codeInsight.generation.actions.BaseGenerateAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.impl.source.PsiJavaFileImpl
import com.intellij.psi.util.PsiUtilBase

/**
 * Created by cz on 2017/6/16.
 */
class ConvertAction : BaseGenerateAction {
    constructor() : super(null)

    constructor(handler: CodeInsightActionHandler) : super(handler)

    override fun actionPerformed(event: AnActionEvent) {
        event.getData(PlatformDataKeys.PROJECT)?.let {project->
            WriteCommandAction.runWriteCommandAction(project){
                val editor = event.getData(PlatformDataKeys.EDITOR)
                val psiFile = PsiUtilBase.getPsiFileInEditor(editor!!, project)
                if(null==psiFile||psiFile !is PsiJavaFileImpl){
                    MessageDelegate.showMessage("File:${psiFile?.name} not a java class file!","File Error")
                } else {
                    val clazz = getTargetClass(editor, psiFile)
                    if(null!=clazz){
                        val configuration = Template.loadTemplateItems(project)
                        ConvertInjectAction(project, psiFile, clazz,true,configuration).run()
                    }
                }
            }
        }
    }


}
