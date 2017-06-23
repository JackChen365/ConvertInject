package com.cz.model.plugin.delegate

import com.intellij.notification.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.Messages

/**
 * Created by cz on 2017/5/31.
 */
object MessageDelegate {
    val GROUP_DISPLAY = "KotlinModelGenerator"
    val GROUP_DISPLAY_LOG = "KotlinModelGenerator LOG"
    val GROUP_DISPLAY_ID_INFO = NotificationGroup(GROUP_DISPLAY, NotificationDisplayType.BALLOON, true)
    val GROUP_DISPLAY_ID_LOG = NotificationGroup(GROUP_DISPLAY_LOG, NotificationDisplayType.NONE, true)

    fun onPluginExceptionHandled(exception: Exception) {
        showMessage(exception.message!!, "Action Error!")
    }

    fun logEventMessage(message: String) {
        val notification = GROUP_DISPLAY_ID_LOG.createNotification(message, NotificationType.INFORMATION)
        sendNotification(notification)
    }

    fun showSuccessMessage() {
        val notification = GROUP_DISPLAY_ID_INFO.createNotification("Convert Success!", NotificationType.INFORMATION)
        sendNotification(notification)
    }

    private fun sendNotification(notification: Notification) {
        ApplicationManager.getApplication().invokeLater {
            val projects = ProjectManager.getInstance().openProjects
            Notifications.Bus.notify(notification, projects[0])
        }
    }

    fun showMessage(message: String, header: String) {
        Messages.showDialog(message, header, arrayOf("ok"), -1, null)
    }

}
