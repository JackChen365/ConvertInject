package com.cz.convert

import com.cz.convert.model.FieldItem
import com.cz.model.plugin.delegate.MessageDelegate
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.impl.source.PsiJavaFileImpl
import com.intellij.psi.impl.source.PsiModifierListImpl

/**
 * Created by cz on 2017/6/16.
 */
class ConvertInjectAction(project: Project, internal var file: PsiJavaFileImpl, internal var clazz: PsiClass, val single:Boolean=true):Runnable {
    val INJECT_ID = "com.cz.aop.inject.Id"
    internal var factory: PsiElementFactory = JavaPsiFacade.getElementFactory(project)

    override fun run() {
        try {
            //检测是否己存在findViewItems方法,若己存在,则移除
            val findMethod = findMethodItem("findViewItems")
            if (null != findMethod) {
                findMethod.delete()
                MessageDelegate.logEventMessage("${clazz.name} findViewItems existed! Remove it")
            }
            //获取所有使用字段,若没有字段不执行
            val items = getClassFieldItems()
            if (items.isEmpty()) {
                if (single) {
                    MessageDelegate.showMessage("${clazz.name} no view field to convert!", "Convert Action")
                } else {
                    MessageDelegate.logEventMessage("${clazz.name} no view field to convert!")
                }
            } else {
                if (clazz.isActivity()||clazz.isDialog()) {
                    createActivityFindViewMethod(items)
                } else if (clazz.isFragment()) {
                    createFragmentFindViewMethod(items)
                } else {
                    //other
                    MessageDelegate.logEventMessage("${clazz.name} not a activity/fragment!")
                }
            }
            //删除导入包
            file.importList?.importStatements?.find { it.qualifiedName == INJECT_ID }?.delete()
            if (single) {
                MessageDelegate.showSuccessMessage()
            } else {
                MessageDelegate.logEventMessage("${clazz.name} convert success!")
            }
        } catch (e: Exception) {
            MessageDelegate.onPluginExceptionHandled(e)
        }
    }


    /**
     * 创建activity查找条目
     */
    private fun createActivityFindViewMethod(items: List<FieldItem>) {
        val out = StringBuilder()
        out.append("private void findViewItems(){")
        items.forEach { out.append("${it.fieldName}=(${it.fieldType})findViewById(${it.id});") }
        out.append("}")
        val findMethod = findMethodItem("onCreate")
        val createMethod = factory.createMethodFromText(out.toString(), null)
        if (null != findMethod) {
            clazz.addAfter(createMethod, findMethod)
            for (statement in findMethod.body!!.statements) {
                // Search for setContentView()
                if (statement is PsiExpressionStatement) {
                    val firstChild = statement?.firstChild?.firstChild
                    if (null != firstChild && firstChild.text == "setContentView") {
                        //加入方法
                        statement.addAfter(factory.createStatementFromText("findViewItems();", clazz), statement)
                    }
                }
            }
        }
    }

    /**
     * 创建fragment查找条目
     */
    private fun createFragmentFindViewMethod(items: List<FieldItem>) {
        val out = StringBuilder()
        out.append("private void findViewItems(View view){")
        items.forEach {
            //View不用转换
            val fieldType = if ("View" != it.fieldType) "(${it.fieldType})" else ""
            out.append("${it.fieldName}=${fieldType}view.findViewById(${it.id});\n")
        }
        out.append("}\n")
        val findCreateViewMethod = findMethodItem("onCreateView")
        var findViewCreateMethod = findMethodItem("onViewCreated")
        //如果fragment类中没有创建onViewCreated方法,帮助创建
        val createMethod = factory.createMethodFromText(out.toString(), null)
        if (null == findViewCreateMethod) {
            //创建onViewCreate方法
            out.delete(0, out.length)
            out.append("@Override\n")
            out.append("public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {\n")
            out.append("super.onViewCreated(view, savedInstanceState);\n")
            out.append("findViewItems(view);\n")
            out.append("}\n")
            findViewCreateMethod = factory.createMethodFromText(out.toString(), null)
            if (null != findCreateViewMethod) {
                clazz.addAfter(findViewCreateMethod, findCreateViewMethod)
            } else if (0 < clazz.methods.size) {
                //加到第1个方法后面
                clazz.addAfter(findViewCreateMethod, clazz.methods[0])
            }
            clazz.addAfter(createMethod, findCreateViewMethod)
        } else if (null != findCreateViewMethod) {
            clazz.addAfter(createMethod, findViewCreateMethod)
            for (statement in findViewCreateMethod.body!!.statements) {
                // Search for setContentView()
                if (statement is PsiExpressionStatement) {
                    val firstChild = statement?.firstChild?.firstChild
                    if (null != firstChild && firstChild.text == "super.onViewCreated") {
                        //加入方法
                        statement.addAfter(factory.createStatementFromText("findViewItems(view);", clazz), statement)
                    }
                }
            }
        }
    }

    private fun findMethodItem(methodName: String): PsiMethod? {
        val methodItems = clazz.findMethodsByName(methodName, false)
        return if (0 < methodItems.size) methodItems[0] else null
    }

    private fun getClassFieldItems(): List<FieldItem> {
        return clazz.fields?.map {
            val element = it.children.find { it is PsiModifierListImpl && null != it.annotations && 0 < it.annotations.size }
            var annotation: PsiAnnotation? = null
            if (null != element && element is PsiModifierListImpl) {
                annotation = element.annotations.find { it.qualifiedName == INJECT_ID }
            }
            it.to(annotation)
        }.filter { (_, annotation) ->
            null != annotation && null != annotation.findAttributeValue("value")
        }.map { (field, annotation) ->
            //删除注解
            annotation?.delete()
            FieldItem(field.name, field.type.presentableText, annotation?.findAttributeValue("value")?.text)
        }
    }

}
