package com.cz.convert.ui.action

import java.awt.event.ActionEvent
import javax.swing.Action

/**
 * Process the removal of a path element.
 */
class RemovePathAction
/**
 * Create a new add path action.
 */
internal constructor() : ToolbarAction() {

    init {
        putValue(Action.NAME, "Remove")
        putValue(Action.SHORT_DESCRIPTION, "Remove a new template item")
        putValue(Action.LONG_DESCRIPTION, "Remove a new template item")
    }

    override fun actionPerformed(e: ActionEvent) {

    }

    companion object {
        private val serialVersionUID = 7339136485307147623L
    }
}