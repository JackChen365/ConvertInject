package com.cz.convert.convert

import com.cz.convert.model.*
import com.cz.convert.service.JavaService
import com.cz.model.plugin.delegate.MessageDelegate
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.*
import com.intellij.psi.impl.source.PsiJavaFileImpl
import com.intellij.psi.impl.source.PsiModifierListImpl
import com.sun.javafx.scene.CameraHelper.project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiSearchHelper
import com.intellij.psi.search.PsiSearchScopeUtil
import com.intellij.psi.search.PsiShortNamesCache
import jdk.nashorn.internal.codegen.CompilerConstants.className
import org.bouncycastle.asn1.iana.IANAObjectIdentifiers.directory
import com.intellij.psi.JavaDirectoryService
import com.intellij.psi.PsiClass
import com.intellij.psi.impl.file.PsiDirectoryFactory
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiImportStatement
import com.sun.javafx.scene.CameraHelper.project
import com.intellij.psi.PsiElementFactory




/**
 * Created by cz on 2017/6/28.
 * 转换器
 */
abstract class BaseConverter(val factory: PsiElementFactory, var file: PsiJavaFileImpl, internal var clazz: PsiClass, val configuration: Configuration, val single:Boolean){
    val project=file.project
    var searchScope = GlobalSearchScope.allScope(project)
    protected val injectItem:InjectItem = configuration.items[configuration.index]
    /**
     * 转换前置操作,如设定移除类中注解,检测类方法是否存在,检测等操作
     */
    abstract fun preConvert()

    /**
     * 生成方法
     */
    abstract fun generateMethod(fieldItems:List<FieldItem>,methodItems:List<MethodItem>)
    /**
     * 转换表达式
     */
    abstract fun insertExpression()

    /**
     * 执行转换后操作,移除导入包,等,一些操作不能在pre执行,因为移除了,后面字段引用会出错
     */
    abstract fun postConvert()

    private fun convert(fieldItems:List<FieldItem>,methodItems:List<MethodItem>){
        preConvert()//前置操作
        importFieldClass(fieldItems)//导入字段包名
        generateMethod(fieldItems,methodItems)//生成方法
        insertExpression()//插入表达式
    }
    fun convertClass(){
        val findMethod = findMethodItem(configuration.methodName)
        if (null != findMethod) {
            MessageDelegate.logEventMessage("${clazz.name} ${configuration.methodName} existed! Skip it")
        } else {
            //获取所有使用字段,若没有字段不执行
            val classFieldItems = getClassFieldItems()
            val classMethodItems = getClassMethodItems()
            if (classFieldItems.isEmpty()&&classMethodItems.isEmpty()) {
                if (single) {
                    MessageDelegate.showMessage("${clazz.name} no view element to convert!", "Convert Action")
                } else {
                    MessageDelegate.logEventMessage("${clazz.name} no view element to convert!")
                }
            } else {
                //检测通过,开始执行转换
                convert(classFieldItems,classMethodItems)
                if (single) {
                    MessageDelegate.showSuccessMessage()
                } else {
                    MessageDelegate.logEventMessage("${clazz.name} convert success!")
                }
            }
        }
    }

    private fun importFieldClass(fieldItems: List<FieldItem>) {
//        import android.graphics.BitmapFactory;
//        import android.graphics.drawable.Drawable;
//        import android.support.v4.content.ContextCompat;
//        import android.view.animation.AnimationUtils;
        val javaService = JavaService.getInstance(project)
        fieldItems.forEach {
            val importValue=when(it.actionItem.action){
                ActionType.BIND_FIELD_ANIM->"android.view.animation.AnimationUtils"
                ActionType.BIND_FIELD_COLOR->"android.support.v4.content.ContextCompat"
                ActionType.BIND_FIELD_DRAWABLE->"android.support.v4.content.ContextCompat"
                ActionType.BIND_FIELD_BITMAP->"android.graphics.BitmapFactory"
                else ->null
            }
            if(null!=importValue){
                if(file.importList?.allImportStatements?.none { it.text.contains(importValue) }?:true){
                    javaService.findClass(importValue, searchScope)?.let {
                        file.importList?.add(factory.createImportStatement(it))
                    }
                }
            }
        }
    }

    /**
     * 查找指定方法
     */
    protected fun findMethodItem(methodName: String): PsiMethod? {
        val methodItems = clazz.findMethodsByName(methodName, false)
        return if (0 < methodItems.size) methodItems[0] else null
    }

    /**
     * 删除导入包列表
     */
    protected fun deleteImportList(){
        //删除导入包
        file.importList?.importStatements?.forEach { item->
            if(injectItem.actionItems.any {  it.clazz==item.qualifiedName}){
                item.delete()
            }
        }
    }

    /**
     * 删除类上的配置注解
     */
    protected fun deleteClassAnnotation(){
        //删除class上注解
        clazz.modifierList?.annotations?.forEach { item->
            if(injectItem.actionItems.any {  it.clazz==item.qualifiedName}){
                item.delete()
            }
        }
    }

    /**
     * 获取符合配置注解类字段对象
     */
    protected fun getClassFieldItems(): List<FieldItem> {
        return clazz.fields.map {
            val element = it.children.find { it is PsiModifierListImpl && null != it.annotations && 0 < it.annotations.size }
            var annotation: PsiAnnotation? = null
            if (null != element && element is PsiModifierListImpl) {
                annotation = element.annotations.find { item->
                    injectItem.actionItems.any {  it.clazz==item.qualifiedName }
                }
            }
            it.to(annotation)
        }.filter { (_, annotation) -> null != annotation
        }.map { (field, annotation) ->
            val actionItem = injectItem.actionItems.find { null != annotation?.findAttributeValue(it.key)&&annotation.qualifiedName==it.clazz }
            FieldElementItem(field,annotation,actionItem)
        }.filter { (_, _,actionItem) -> null!=actionItem
        }.map { (field, annotation,actionItem) ->
            //删除注解
            annotation?.delete()
            FieldItem(field.name, field.type.presentableText, annotation?.findAttributeValue(actionItem?.key)?.text,actionItem!!)
        }
    }

    /**
     * 获取配置注解的方法对象
     */
    protected fun getClassMethodItems(): List<MethodItem> {
        return clazz.methods.map {
            val element = it.children.find { it is PsiModifierListImpl && null != it.annotations && 0 < it.annotations.size }
            var annotation: PsiAnnotation? = null
            if (null != element && element is PsiModifierListImpl) {
                annotation = element.annotations.find { item->
                    configuration.items.any { it.actionItems?.any { it.clazz==item.qualifiedName } }
                }
            }
            it.to(annotation)
        }.filter { (_, annotation) -> null != annotation
        }.map { (method, annotation) ->
            val actionItem = injectItem.actionItems.find { null != annotation?.findAttributeValue(it.key)&&annotation.qualifiedName==it.clazz }
            MethodElementItem(method,annotation,actionItem)
        }.filter { (_, _,actionItem) -> null!=actionItem
        }.map { (method, annotation,actionItem) ->
            //删除注解
            annotation?.delete()
            MethodItem(method.name,annotation?.findAttributeValue(actionItem?.key)?.text,actionItem!!)
        }
    }

}