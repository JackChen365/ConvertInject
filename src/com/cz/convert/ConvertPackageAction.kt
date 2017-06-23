package com.cz.convert

import com.cz.model.plugin.delegate.MessageDelegate
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.source.PsiJavaFileImpl

/**
 * Created by cz on 2017/6/16.
 */
class ConvertPackageAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project?:return
        val selectedFile = DataKeys.VIRTUAL_FILE.getData(event.dataContext)
        if(null==selectedFile||!selectedFile.exists()){
            MessageDelegate.showMessage("An invalid file!","File Error")
        } else if(selectedFile.isDirectory){
            WriteCommandAction.runWriteCommandAction(project){ eachVirtualFile(project,selectedFile) }
            MessageDelegate.showSuccessMessage()
        }
    }

    fun eachVirtualFile(project: Project, file: VirtualFile){
        if(file.isDirectory){
            file.children.forEach { eachVirtualFile(project,it) }
        } else {
            val psiFile = PsiManager.getInstance(project).findFile(file)
            if (psiFile is PsiJavaFileImpl&&0<psiFile.classes.size) {
                psiFile.classes.forEach {
                    if(null!=it&&(it.isActivity()||it.isFragment())){
                        ConvertInjectAction(project, psiFile, it).run()
                    }
                }
            }
        }
    }



}
