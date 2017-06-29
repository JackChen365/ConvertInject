package com.cz.convert

import com.cz.convert.model.Configuration
import com.cz.convert.model.InjectItem
import com.cz.model.plugin.delegate.MessageDelegate
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiClass
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
            val configuration = Template.loadTemplateItems(project)
            WriteCommandAction.runWriteCommandAction(project){ eachVirtualFile(project,selectedFile,configuration) }
            MessageDelegate.showSuccessMessage()
        }
    }

    fun eachVirtualFile(project: Project, file: VirtualFile,configuration: Configuration){
        if(file.isDirectory){
            file.children.forEach { eachVirtualFile(project,it,configuration) }
        } else {
            val psiFile = PsiManager.getInstance(project).findFile(file)
            if (psiFile is PsiJavaFileImpl) {
                psiFile.classes.forEach {
                    //处理类
                    processJavaFile(project,psiFile,it,configuration)
                    //处理内部内
                    it.innerClasses.forEach{processJavaFile(project,psiFile,it,configuration)}
                }
            }
        }
    }
    fun processJavaFile(project: Project, psiFile: PsiJavaFileImpl,classFile:PsiClass,configuration: Configuration){
        if(null!=classFile&&(classFile.isActivity()||classFile.isFragment()||classFile.isDialog()||classFile.isViewHolder())){
            ConvertInjectAction(project, psiFile, classFile,false,configuration).run()
        }
    }



}
