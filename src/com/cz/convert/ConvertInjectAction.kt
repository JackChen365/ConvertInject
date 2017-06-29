package com.cz.convert

import com.cz.convert.convert.ActivityConverter
import com.cz.convert.convert.DialogConverter
import com.cz.convert.convert.FragmentConverter
import com.cz.convert.convert.ViewHolderConverter
import com.cz.convert.model.Configuration
import com.cz.model.plugin.delegate.MessageDelegate
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.impl.source.PsiJavaFileImpl

/**
 * Created by cz on 2017/6/16.
 */
class ConvertInjectAction(project: Project, internal var file: PsiJavaFileImpl, internal var clazz: PsiClass, val single:Boolean,val configuration: Configuration):Runnable {
    internal var factory: PsiElementFactory = JavaPsiFacade.getElementFactory(project)

    override fun run() = try {
        if (clazz.isActivity()) {
            ActivityConverter(factory,file,clazz,configuration,single).convertClass()
        } else if (clazz.isFragment()) {
            FragmentConverter(factory,file,clazz,configuration,single).convertClass()
        } else if(clazz.isDialog()){
            DialogConverter(factory,file,clazz,configuration,single).convertClass()
        } else if(clazz.isViewHolder()){
            ViewHolderConverter(factory,file,clazz,configuration,single).convertClass()
        } else {
            MessageDelegate.logEventMessage("${clazz.name} not an activity/fragment/dialog/recyclerView.ViewHolder!")
        }
    } catch (e: Exception) {
        MessageDelegate.onPluginExceptionHandled(e)
    }
}
