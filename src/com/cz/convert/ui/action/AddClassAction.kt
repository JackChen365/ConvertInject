package com.cz.convert.ui.action

import com.cz.convert.model.ActionItem
import com.cz.convert.model.InjectItem
import com.cz.convert.ui.NewActionPanel
import com.cz.convert.ui.NewTemplatePanel
import com.intellij.openapi.ui.DialogBuilder
import java.awt.event.ActionEvent
import javax.swing.Action
import javax.swing.SwingUtilities

/**
 * Created by cz on 2017/6/27.
 */
class AddClassAction : ToolbarAction() {
    private var callback: ((ActionItem) -> Unit)?=null
    /**
     * Create a new add path action.
     */
    init {
        putValue(Action.NAME, "Add")
        putValue(Action.SHORT_DESCRIPTION, "Add a new action item")
        putValue(Action.LONG_DESCRIPTION, "Add a new action item")
    }

    override fun actionPerformed(e: ActionEvent?) {
        val dialogBuilder = DialogBuilder()
        dialogBuilder.setTitle("New Action")
        val panel = NewActionPanel(dialogBuilder)
        panel.setOnCreateNewActionListener { this.callback?.invoke(it) }
        dialogBuilder.setCenterPanel(panel.rootComponent)
        dialogBuilder.removeAllActions()
        SwingUtilities.invokeLater { dialogBuilder.show() }
    }

    fun onNewActionAdded(callback:(ActionItem)->Unit){
        this.callback=callback
    }

    companion object {
        private val serialVersionUID = -1389576037231727361L
    }
}
