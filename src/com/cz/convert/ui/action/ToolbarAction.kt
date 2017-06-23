package com.cz.convert.ui.action

import com.intellij.ui.AnActionButton
import com.intellij.ui.AnActionButtonRunnable
import javax.swing.AbstractAction

abstract class ToolbarAction : AbstractAction(), AnActionButtonRunnable {
    override fun run(anActionButton: AnActionButton) {
        actionPerformed(null)
    }

    companion object {
        private val serialVersionUID = 7091312536206510956L
    }
}
