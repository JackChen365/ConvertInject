package com.cz.convert.ui

import com.cz.convert.Template
import com.intellij.openapi.module.Module
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

/**
 * Created by cz on 2017/6/17.
 */
class ConvertConfiguration(module: Module) : Configurable{
    val configurationPanel:ConfigurationPanel
    init {
        val configuration = Template.loadTemplateItems(module.project)
        configurationPanel=ConfigurationPanel(module.project,configuration)
    }
    override fun isModified(): Boolean=configurationPanel.isModified

    override fun getDisplayName(): String ="Convert Annotation"

    override fun apply() =configurationPanel.updateTemplateItems()

    override fun createComponent(): JComponent =configurationPanel.rootComponent
}