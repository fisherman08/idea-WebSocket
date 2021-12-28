package com.github.fisherman08.idea_websocket.platform

import com.github.fisherman08.idea_websocket.client.EventHandler
import com.github.fisherman08.idea_websocket.client.Headers
import com.github.fisherman08.idea_websocket.client.ServerUri
import com.github.fisherman08.idea_websocket.client.WebSocketClientImpl
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.panel
import java.awt.Color
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel

class WebSocketToolWindow {

    private val logger = Logger.getInstance(javaClass)
    private val properties = WebSocketProperties()

    private var client: WebSocketClientImpl? = null

    private val urlHistories = ComboBox(arrayOf("") + properties.getUrls()).apply {
        isEditable = true
    }

    private val connectButton = JButton("Connect").apply {
        addActionListener { handleConnectButtonClicked() }
    }
    private val disconnectButton = JButton("Disconnect").apply {
        isEnabled = false
        addActionListener { handleDisconnectButtonClicked() }
    }

    private val isConnectedLabel = JLabel()

    private val responseArea = JBTextArea(15, 50).apply {
        isEditable = false
    }
    private val responseAreaScrollPane = JBScrollPane(responseArea)


    private val requestArea = JBTextArea()

    private val sendMessageButton = JButton("Send").apply {
        isEnabled = false
        addActionListener { handleSendMessageButtonClicked() }
    }

    private fun handleConnectButtonClicked() {
        try {
            this.client?.close()
            val url = ServerUri(urlHistories.item)
            val headers = Headers(emptyMap())
            val handler = EventHandler(
                onOpen = {},
                onClose = { handleConnectionClosed() },
                onError = { e ->
                    logger.warn(e)
                },
                onMessage = {message ->  responseArea + "Response =>\n$message"}
            )
            this.client = WebSocketClientImpl(url, headers, handler)

            if (this.client!!.connectBlocking()) {
                handleConnectionSuccess()
            } else {
                handleConnectionFailed()
            }
        } catch (e: Throwable) {
            logger.warn(e)
            handleConnectionFailed()
        }
    }

    private fun handleDisconnectButtonClicked() {
        this.client?.let {
            it.close()
            handleConnectionClosed()
        }
    }

    private fun handleSendMessageButtonClicked() {
        responseArea + "Request =>\n${requestArea.text}"
        this.client?.send(requestArea.text)
    }

    private fun handleConnectionSuccess() {
        properties.setUrl(urlHistories.item)
        isConnectedLabel.text = "Connected to ${urlHistories.item}"
        isConnectedLabel.foreground = Color.GREEN
        connectButton.isEnabled = false
        disconnectButton.isEnabled = true
        sendMessageButton.isEnabled = true
    }

    private fun handleConnectionFailed() {
        this.client = null
        isConnectedLabel.text = "Connection Failed"
        isConnectedLabel.foreground = Color.RED
    }

    private fun handleConnectionClosed() {
        this.client = null
        isConnectedLabel.text = "Connection Closed"
        isConnectedLabel.foreground = Color.YELLOW
        connectButton.isEnabled = true
        disconnectButton.isEnabled = false
        sendMessageButton.isEnabled = false
    }

    fun getComponent(): JComponent {
        return panel {
            row {
                label("URL: ", bold = true)
            }
            row {
                cell(isFullWidth = true) {
                    urlHistories(CCFlags.growX, CCFlags.pushX)
                    connectButton()
                    disconnectButton()
                }
            }
            row {
                cell {
                    isConnectedLabel()
                }
                cell {
                    label("")
                }
            }
            row {
                row {

                    cell {
                        label("Responses from server")
                    }
                    cell {
                        label("Request to server")
                    }
                }
                row {
                    cell {
                        responseAreaScrollPane()
                    }
                    cell {
                        requestArea(CCFlags.growX, CCFlags.pushX, CCFlags.growY, CCFlags.pushY)
                    }
                }
            }
            row {
                row {
                    cell {
                        button("Clear") { responseArea.truncateMessages() }
                    }
                    cell {
                        button("Clear") { requestArea.truncateMessages() }
                        sendMessageButton()
                    }
                }
            }
        }
    }
}

private operator fun JBTextArea.plus(message: String) {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
    val now = LocalDateTime.now()
    this.append("(${now.format(formatter)}) $message\n\n")
}

private fun JBTextArea.truncateMessages() {
    this.text = ""
}
