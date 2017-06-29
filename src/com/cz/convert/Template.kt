package com.cz.convert

import com.cz.convert.model.ActionItem
import com.cz.convert.model.ActionType
import com.cz.convert.model.Configuration
import com.cz.convert.model.InjectItem
import com.cz.model.plugin.delegate.MessageDelegate
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import org.apache.http.util.TextUtils
import org.jdom.Document
import org.jdom.Element
import org.jdom.output.Format
import org.jdom.output.XMLOutputter
import java.io.ByteArrayOutputStream
import org.jdom.input.SAXBuilder


/**
 * Created by cz on 2017/6/26.
 * 预设的模块配置
 */
object Template{
    val DEFAULT_INDEX=1
    val DEFAULT_METHOD="findViewItems"
    val FILE_NAME="inject_template.xml"
    val configuration=Configuration()
    init {
        configuration {
            index=DEFAULT_INDEX
            methodName=DEFAULT_METHOD
            item(this){
                name="MyInject"
                action(this){
                    name="Id"
                    clazz="com.cz.injectlibrary.Id"
                    key="value"
                    action=ActionType.BIND_FIELD_VIEW
                }
                action(this){
                    name="Title"
                    clazz="com.cz.aop.inject.Title"
                    action=ActionType.BIND_CLASS
                }
                action(this){
                    name="Click"
                    clazz="com.cz.aop.inject.Click"
                    action=ActionType.BIND_METHOD_CLICK
                }
                action(this){
                    name="ViewUtils"
                    clazz="com.cz.aop.inject.ViewUtils"
                    action=ActionType.BIND_INIT
                }
            }
            item(this){
                name="ButterKnife"

                action(this){
                    name="ButterKnife"
                    clazz="butterknife.ButterKnife"
                    action=ActionType.BIND_INIT
                }

                action(this){
                    name="Bind"
                    clazz="butterknife.Bind"
                    action=ActionType.BIND_FIELD_VIEW
                }
                action(this){
                    name="BindView"
                    clazz="butterknife.BindView"
                    action=ActionType.BIND_FIELD_VIEW
                }
                action(this){
                    name="InjectView"
                    clazz="butterknife.InjectView"
                    action=ActionType.BIND_FIELD_VIEW
                }
                action(this){
                    name="BindString"
                    clazz="butterknife.BindString"
                    action=ActionType.BIND_FIELD_STRING
                }
                action(this){
                    name="BindArray"
                    clazz="butterknife.BindArray"
                    action=ActionType.BIND_FIELD_ARRAY
                }
                action(this){
                    name="OnClick"
                    clazz="butterknife.OnClick"
                    action=ActionType.BIND_METHOD_CLICK
                }
                action(this){
                    name="OnLongClick"
                    clazz="butterknife.OnLongClick"
                    action=ActionType.BIND_METHOD_LONG_CLICK
                }
            }
            item(this){
                name="AndroidAnnotations"
                action(this){
                    name="ViewById"
                    clazz="com.googlecode.androidannotations.annotations.ViewById"
                    action=ActionType.BIND_FIELD_VIEW
                }
                action(this){
                    name="EActivity"
                    clazz="com.googlecode.androidannotations.annotations.EActivity"
                    action=ActionType.BIND_CLASS
                }
                action(this){
                    name="Click"
                    clazz="com.googlecode.androidannotations.annotations.Click"
                    action=ActionType.BIND_METHOD_CLICK
                }
            }
        }
    }
    fun configuration(closure: Configuration.()->Unit){
        configuration.apply(closure)
    }
    fun item(item:Configuration,closure: InjectItem.()->Unit){
        item.items.add(InjectItem().apply(closure))
    }
    fun action(item:InjectItem,closure: ActionItem.()->Unit){
        item.actionItems.add(ActionItem().apply(closure))
    }

    /**
     * 创建模块文件
     */
    private fun createTemplateFile(configuration: Configuration, file: VirtualFile) {
        val root = Element("template")
        root.setAttribute("index", "${configuration.index}")
        root.setAttribute("method", "${configuration.methodName}")
        val doc = Document(root)
        configuration.items.forEach {
            val itemElement = Element("item")
            itemElement.setAttribute("name", "${it.name}")
            it.actionItems.forEach {
                val classElement = Element("class")
                itemElement.addContent(classElement)
                classElement.setAttribute("name", "${it.name}")
                classElement.setAttribute("class", "${it.clazz}")
                classElement.setAttribute("key", "${it.key}")
                classElement.setAttribute("action", "${it.action.name}")
            }
            root.addContent(itemElement)
        }
        val format = Format.getPrettyFormat()
        format.indent = "\t"
        val byteStream = ByteArrayOutputStream()
        XMLOutputter(format).output(doc, byteStream)
        val content = byteStream.toString("utf-8")
        VfsUtil.saveText(file, content)
        MessageDelegate.logEventMessage("Update $FILE_NAME success!")
    }


    /**
     * 加载模块配置条目
     */
    fun loadTemplateItems(project:Project):Configuration{
        val projectFile = project.projectFile
        val ideaFolder = projectFile?.parent
        var configuration:Configuration= Configuration()
        if (null != ideaFolder && ideaFolder.exists()) {
            val templateFile = ideaFolder.findChild(FILE_NAME)
            if(null==templateFile||!templateFile.exists()){
                //添加默认
                configuration=this.configuration
                WriteCommandAction.runWriteCommandAction(project) {
                    createTemplateFile(configuration,ideaFolder.findOrCreateChildData(null,FILE_NAME))
                }
            } else {
                //解析配置
                try {
                    val sax = SAXBuilder()
                    val doc = sax.build(templateFile.inputStream)
                    val root = doc.rootElement
                    configuration.index=root.getAttribute("index")?.value?.toInt()?: DEFAULT_INDEX
                    configuration.methodName=root.getAttribute("method")?.value?:DEFAULT_METHOD
                    root.children.forEach {
                        val item=InjectItem()
                        item.name=it.getAttribute("name")?.value
                        it.children?.forEach{
                            val actionItem=ActionItem()
                            item.actionItems.add(actionItem)
                            actionItem.name=it.getAttribute("name").value
                            actionItem.clazz=it.getAttribute("class").value
                            actionItem.key=it.getAttribute("key").value
                            val actionValue = it.getAttribute("action").value
                            if(!TextUtils.isEmpty(actionValue)){
                                val actionType = ActionType.valueOf(actionValue)
                                if(null!=actionType){
                                    actionItem.action=actionType
                                }
                            }
                        }
                        configuration.items.add(item)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return configuration
    }

    /**
     * 添加一个自定义模版
     */
    fun updateTemplateItem(configuration: Configuration,project:Project){
        val projectFile = project.projectFile
        val ideaFolder = projectFile?.parent
        if (null != ideaFolder && ideaFolder.exists()) {
            val templateFile = ideaFolder.findOrCreateChildData(null, FILE_NAME)
            WriteCommandAction.runWriteCommandAction(project) {
                createTemplateFile(configuration,templateFile)
            }
        }
    }
}
