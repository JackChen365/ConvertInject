package com.cz.convert.ui.action

import java.awt.event.ActionEvent
import javax.swing.Action

/**
 * Process the removal of a path element.
 */
class RemoveClassAction : ToolbarAction() {
    private var callback: (() -> Unit)?=null
    init {
        putValue(Action.NAME, "Remove")
        putValue(Action.SHORT_DESCRIPTION, "Remove a new action item")
        putValue(Action.LONG_DESCRIPTION, "Remove a new action item")
    }

    override fun actionPerformed(e: ActionEvent?) {
        this.callback?.invoke()
    }

    fun onRemoveAction(callback:()->Unit){
        this.callback=callback
    }

    companion object {
        private val serialVersionUID = 7339136485307147624L
    }
}