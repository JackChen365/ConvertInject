package com.cz.convert.ui.action

import com.cz.convert.ui.NewTemplatePanel
import com.intellij.openapi.ui.DialogBuilder
import java.awt.event.ActionEvent
import javax.swing.Action
import javax.swing.SwingUtilities

/**
 * Process the addition of a path element.
 */
internal class AddPathAction : ToolbarAction() {
    /**
     * Create a new add path action.
     */
    init {
        putValue(Action.NAME, "Add")
        putValue(Action.SHORT_DESCRIPTION, "Add a new template item")
        putValue(Action.LONG_DESCRIPTION, "Add a new template item")
    }

    override fun actionPerformed(e: ActionEvent) {
        val dialogBuilder = DialogBuilder()
        dialogBuilder.setTitle("New Template")
        val panel = NewTemplatePanel(dialogBuilder)
        dialogBuilder.setCenterPanel(panel.rootComponent)
        dialogBuilder.removeAllActions()
        SwingUtilities.invokeLater { dialogBuilder.show() }
    }

    companion object {
        private val serialVersionUID = -1389576037231727360L
    }
}
