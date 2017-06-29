package com.cz.convert.ui.action

import com.cz.convert.model.InjectItem
import java.awt.event.ActionEvent
import javax.swing.Action

/**
 * Process the removal of a path element.
 */
class RemoveTemplateAction : ToolbarAction() {
    private var callback: (() -> Unit)?=null
    init {
        putValue(Action.NAME, "Remove")
        putValue(Action.SHORT_DESCRIPTION, "Remove a new template item")
        putValue(Action.LONG_DESCRIPTION, "Remove a new template item")
    }

    override fun actionPerformed(e: ActionEvent?) {
        this.callback?.invoke()
    }

    fun onTemplateRemove(callback:()->Unit){
        this.callback=callback
    }

    companion object {
        private val serialVersionUID = 7339136485307147623L
    }
}