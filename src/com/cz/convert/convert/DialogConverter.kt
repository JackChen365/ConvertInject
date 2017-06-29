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
 * dialog转换器对象
 */
class DialogConverter(factory: PsiElementFactory, file: PsiJavaFileImpl, clazz: PsiClass, configuration: Configuration, single: Boolean) : BaseConverter(factory, file, clazz, configuration, single) {
    override fun preConvert() {
        deleteClassAnnotation()
        ensureCreateMethod()
    }

    override fun postConvert() {
        deleteImportList()
    }

    override fun generateMethod(fieldItems: List<FieldItem>, methodItems: List<MethodItem>) {
        val out = StringBuilder()
        out.append("private void ${configuration.methodName}(){")
        //方法注解
        fieldItems.forEach {
            out.append(when(it.actionItem.action){
                ActionType.BIND_FIELD_VIEW->"${it.fieldName} = (${if("view"==it.fieldType) "" else it.fieldType})findViewById(${it.id});"
                ActionType.BIND_FIELD_ANIM->"${it.fieldName} = AnimationUtils.loadAnimation(this,${it.id});"
                ActionType.BIND_FIELD_ARRAY->"${it.fieldName} = getResources().get${it.fieldType}Array(${it.id});"
                ActionType.BIND_FIELD_COLOR->"${it.fieldName} = ContextCompat.getColor(this,${it.id});"
                ActionType.BIND_FIELD_DRAWABLE->"${it.fieldName} = ContextCompat.getDrawable(this,${it.id});"
                ActionType.BIND_FIELD_BITMAP->"${it.fieldName} = BitmapFactory.decodeResource(getResources(),${it.id});"
                else ->"${it.fieldName} = getString(${it.id});"
            })
        }
        //处理方法
        methodItems.forEach {
            out.append(when(it.actionItem.action){
                ActionType.BIND_METHOD_LONG_CLICK->"findViewById(${it.id}).setOnLongClickListener(this::${it.name});"
                else ->"findViewById(${it.id}).setOnClickListener(this::${it.name});"
            })
        }
        out.append("}")
        val generateMethod = factory.createMethodFromText(out.toString(), null)
        //插入方法
        findMethodItem("onCreate")?.let { clazz.addAfter(generateMethod, it) }
    }

    override fun insertExpression() {
        findMethodItem("onCreate")?.let { it.body?.let { body->
            for (statement in body.statements) {
                if (statement is PsiExpressionStatement) {
                    val firstChild = statement?.firstChild
                    if(null!=firstChild){
                        val innerChild = firstChild.firstChild
                        val lastChild = firstChild.lastChild
                        if (null != innerChild) {
                            if(null!=lastChild && innerChild.text == "setContentView"){
                                //加入方法
                                body.addAfter(factory.createStatementFromText("${configuration.methodName}();", clazz), statement)
                            } else {
                                val innerChild = innerChild.firstChild
                                if(innerChild is PsiReferenceExpressionImpl){
                                    if(injectItem.actionItems.any { it.clazz==innerChild.qualifiedName} ){
                                        statement.delete()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } }
    }

    /**
     * 确保onCreate存在
     */
    private fun ensureCreateMethod() {
        val out = StringBuilder()
        var findCreateMethod = findMethodItem("onCreate")
        //创建onViewCreate方法
        if (null == findCreateMethod) {
            out.append("@Override\n")
            out.append("protected void onCreate(Bundle savedInstanceState) {\n")
            out.append("super.onCreate(savedInstanceState);\n")
            out.append("${configuration.methodName}();\n")
            out.append("}\n")
            findCreateMethod = factory.createMethodFromText(out.toString(), null)
            if (0 < clazz.methods.size) {
                //加到第1个方法后面
                clazz.addAfter(findCreateMethod, clazz.methods[0])
            }
        }
    }
}