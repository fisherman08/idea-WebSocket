package com.github.fisherman08.idea_websocket.platform

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class WebSocketToolWindowFactory: ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val webSocketToolWindow = WebSocketToolWindow();
        val content = contentFactory.createContent(webSocketToolWindow.getComponent(), "", false)
        toolWindow.contentManager.addContent(content)
    }
}
