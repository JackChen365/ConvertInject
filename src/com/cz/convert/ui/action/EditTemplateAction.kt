package com.cz.convert.ui.action

import com.cz.convert.model.InjectItem
import com.cz.convert.ui.NewTemplatePanel
import com.intellij.openapi.ui.DialogBuilder
import javax.swing.*
import java.awt.event.ActionEvent

/**
 * Process the editing of a path element.
 */
class EditTemplateAction(val list:JList<InjectItem>) : ToolbarAction() {
    private var callback: ((InjectItem) -> Unit)?=null

    /**
     * Create a new edit path action.
     */
    init {
        putValue(Action.NAME, "Edit")
        putValue(Action.SHORT_DESCRIPTION, "Edit a template item")
        putValue(Action.LONG_DESCRIPTION, "Edit a template item")
    }

    override fun actionPerformed(e: ActionEvent?) {
        val dialogBuilder = DialogBuilder()
        dialogBuilder.setTitle("Edit Template")
        val panel = NewTemplatePanel(dialogBuilder,list.selectedValue)
        panel.setOnTemplateModifyListener { injectItem ->callback?.invoke(injectItem)  }
        dialogBuilder.setCenterPanel(panel.rootComponent)
        dialogBuilder.removeAllActions()
        SwingUtilities.invokeLater { dialogBuilder.show() }
    }

    fun onEditTemplateItem(callback:(InjectItem)->Unit){
        this.callback=callback
    }

    companion object {
        private val serialVersionUID = -1455378231580505750L
    }
}
