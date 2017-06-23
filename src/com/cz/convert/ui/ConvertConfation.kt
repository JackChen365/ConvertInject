package com.cz.convert.ui

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

/**
 * Created by cz on 2017/6/17.
 */
class ConvertConfation : Configurable{

    override fun isModified(): Boolean {
        return false
    }

    override fun getDisplayName(): String ="Convert Annotation"

    override fun apply() {
    }

    override fun createComponent(): JComponent? {
        return ConfagrationPanel().rootComponent
    }

}