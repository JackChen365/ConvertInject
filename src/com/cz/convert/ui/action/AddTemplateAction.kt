package com.cz.convert.ui.action

import com.cz.convert.model.InjectItem
import com.cz.convert.ui.NewTemplatePanel
import com.intellij.openapi.ui.DialogBuilder
import java.awt.event.ActionEvent
import javax.swing.Action
import javax.swing.SwingUtilities

/**
 * Process the addition of a path element.
 */
internal class AddTemplateAction : ToolbarAction() {
    private var callback: ((InjectItem) -> Unit)?=null
    /**
     * Create a new add path action.
     */
    init {
        putValue(Action.NAME, "Add")
        putValue(Action.SHORT_DESCRIPTION, "Add a new template item")
        putValue(Action.LONG_DESCRIPTION, "Add a new template item")
    }

    override fun actionPerformed(e: ActionEvent?) {
        val dialogBuilder = DialogBuilder()
        dialogBuilder.setTitle("New Template")
        val panel = NewTemplatePanel(dialogBuilder,null)
        panel.setOnTemplateModifyListener { injectItem ->callback?.invoke(injectItem)  }
        dialogBuilder.setCenterPanel(panel.rootComponent)
        dialogBuilder.removeAllActions()
        SwingUtilities.invokeLater { dialogBuilder.show() }
    }

    fun onNewTemplateAdded(callback:(InjectItem)->Unit){
        this.callback=callback
    }

    companion object {
        private val serialVersionUID = -1389576037231727360L
    }
}
