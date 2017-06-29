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
 * fragment转换器对象
 */
class FragmentConverter(factory: PsiElementFactory, file: PsiJavaFileImpl, clazz: PsiClass, configuration: Configuration, single: Boolean) : BaseConverter(factory, file, clazz, configuration, single) {

    override fun preConvert() {
        deleteClassAnnotation()
        deleteOnCreateViewInit()
        ensureViewCreatedMethod()
    }

    override fun postConvert() {
        deleteImportList()
    }

    override fun generateMethod(fieldItems: List<FieldItem>, methodItems: List<MethodItem>) {
        val out = StringBuilder()
        out.append("private void ${configuration.methodName}(View itemView){")
        //方法注解
        fieldItems.forEach {
            out.append(when(it.actionItem.action){
                ActionType.BIND_FIELD_VIEW->"${it.fieldName} = (${if("view"==it.fieldType) "" else it.fieldType})itemView.findViewById(${it.id});"
                ActionType.BIND_FIELD_ANIM->"${it.fieldName} = AnimationUtils.loadAnimation(getContext(),${it.id});"
                ActionType.BIND_FIELD_ARRAY->"${it.fieldName} = getResources().get${it.fieldType}Array(${it.id});"
                ActionType.BIND_FIELD_COLOR->"${it.fieldName} = ContextCompat.getColor(getContext(),${it.id});"
                ActionType.BIND_FIELD_DRAWABLE->"${it.fieldName} = ContextCompat.getDrawable(getContext(),${it.id});"
                ActionType.BIND_FIELD_BITMAP->"${it.fieldName} = BitmapFactory.decodeResource(getResources(),${it.id});"
                else ->"${it.fieldName}=getString(${it.id});"
            })
        }
        //处理方法
        methodItems.forEach {
            out.append(when(it.actionItem.action){
                ActionType.BIND_METHOD_LONG_CLICK->"itemView.findViewById(${it.id}).setOnLongClickListener(this::${it.name});"
                else ->"itemView.findViewById(${it.id}).setOnClickListener(this::${it.name});"
            })
        }
        out.append("}")
        val generateMethod = factory.createMethodFromText(out.toString(), null)
        //插入方法
        findMethodItem("onViewCreated")?.let { clazz.addAfter(generateMethod, it) }
    }

    override fun insertExpression() {
        findMethodItem("onViewCreated")?.let { method->
            val parameterList = method.parameterList.parameters
            val psiParameter = parameterList[0]
            method.body?.let { body->
            for (statement in body.statements) {
                // Search for setContentView()
                if (statement is PsiExpressionStatement) {
                    val firstChild = statement?.firstChild
                    if(null!=firstChild){
                        val innerChild = firstChild.firstChild
                        val lastChild = firstChild.lastChild
                        if (null != innerChild) {
                            if(null!=lastChild && innerChild.text == "super.onViewCreated"){
                                //加入方法
                                body.addAfter(factory.createStatementFromText("${configuration.methodName}(${psiParameter.lastChild.text});", clazz), statement)
                            } else {
                                //删除初始化类代码
                                val innerChild = innerChild.firstChild
                                if(innerChild is PsiReferenceExpressionImpl){
                                    if(injectItem.actionItems.any {  it.clazz==innerChild.qualifiedName} ){
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

    private fun deleteOnCreateViewInit() {
        findMethodItem("onCreateView")?.let { it.body?.let {
            for (statement in it.statements) {
                // Search for setContentView()
                if (statement is PsiExpressionStatement) {
                    val firstChild = statement?.firstChild
                    if(null!=firstChild){
                        val innerChild = firstChild.firstChild
                        if (null != innerChild) {
                            //删除初始化类代码
                            val innerChild = innerChild.firstChild
                            if(null!=innerChild&&innerChild is PsiReferenceExpressionImpl){
                                if(injectItem.actionItems.any {  it.clazz==innerChild.qualifiedName }){
                                    statement.delete()
                                }
                            }
                        }
                    }
                }
            }
        } }

    }

    /**
     * 确保onViewCreated存在
     */
    private fun ensureViewCreatedMethod() {
        val out = StringBuilder()
        val findCreateViewMethod = findMethodItem("onCreateView")
        var findViewCreateMethod = findMethodItem("onViewCreated")
        //创建onViewCreate方法
        if (null == findViewCreateMethod) {
            out.append("@Override\n")
            out.append("public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {\n")
            out.append("super.onViewCreated(view, savedInstanceState);\n")
            out.append("${configuration.methodName}(view);\n")
            out.append("}\n")
            findViewCreateMethod = factory.createMethodFromText(out.toString(), null)
            if (null != findCreateViewMethod) {
                clazz.addAfter(findViewCreateMethod, findCreateViewMethod)
            } else if (0 < clazz.methods.size) {
                //加到第1个方法后面
                clazz.addAfter(findViewCreateMethod, clazz.methods[0])
            }
        }
    }

}