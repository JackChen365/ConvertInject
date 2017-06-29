package com.cz.convert.convert

import com.cz.convert.model.ActionType
import com.cz.convert.model.Configuration
import com.cz.convert.model.FieldItem
import com.cz.convert.model.MethodItem
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiExpressionStatement
import com.intellij.psi.impl.source.PsiJavaFileImpl
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl

/**
 * Created by cz on 2017/6/28.
 * RecyclerView/ViewHolder转换器对象
 */
class ViewHolderConverter(factory: PsiElementFactory, file: PsiJavaFileImpl, clazz: PsiClass, configuration: Configuration, single: Boolean) : BaseConverter(factory, file, clazz, configuration, single) {

    override fun preConvert() {
        deleteClassAnnotation()
    }

    override fun postConvert() {
        deleteImportList()
    }

    override fun generateMethod(fieldItems: List<FieldItem>, methodItems: List<MethodItem>) {
        val out = StringBuilder()
        out.append("private void ${configuration.methodName}(View itemView){")
        //字段注解,ViewHolder不应该配置其他注解
        fieldItems.forEach {
            out.append(if(ActionType.BIND_FIELD_VIEW==it.actionItem.action)
                "${it.fieldName} = (${if("view"==it.fieldType) "" else it.fieldType})itemView.findViewById(${it.id});" else "")
        }
        //处理方法
        methodItems.forEach {
            out.append(when(it.actionItem.action){
                ActionType.BIND_METHOD_LONG_CLICK->"itemView.findViewById(${it.id}).setOnLongClickListener(this::${it.name});"
                else ->"itemView.findViewById(${it.id}).setOnClickListener(this::${it.name});"
            })
        }
        out.append("}")
        val constructors = clazz.constructors
        if(null!=constructors&&!constructors.isEmpty()) {
            val last = constructors[constructors.size - 1]
            val createMethod = factory.createMethodFromText(out.toString(), null)
            clazz.addAfter(createMethod, last)
        }
    }

    override fun insertExpression() {
        //生成方法到最后一个构造器后面.并添加引用方法
        val constructors = clazz.constructors
        if(null!=constructors&&!constructors.isEmpty()) {
            val last = constructors[constructors.size - 1]
            last.body?.let { body ->
                for (statement in body.statements) {
                    // Search for setContentView()
                    if (statement is PsiExpressionStatement) {
                        val firstChild = statement?.firstChild
                        if (null != firstChild) {
                            val innerChild = firstChild.firstChild
                            val lastChild = firstChild.lastChild
                            if (null != innerChild) {
                                if (null != lastChild && innerChild.text == "super") {
                                    //加入方法
                                    body.addAfter(factory.createStatementFromText("${configuration.methodName}${lastChild.text};", clazz), statement)
                                } else {
                                    val innerChild = innerChild.firstChild
                                    if (innerChild is PsiReferenceExpressionImpl) {
                                        if (injectItem.actionItems.any { it.clazz == innerChild.qualifiedName }) {
                                            statement.delete()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}