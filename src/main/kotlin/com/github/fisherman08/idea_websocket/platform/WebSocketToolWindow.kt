package com.github.fisherman08.idea_websocket.platform

import com.github.fisherman08.idea_websocket.client.EventHandler
import com.github.fisherman08.idea_websocket.client.Headers
import com.github.fisherman08.idea_websocket.client.ServerUri
import com.github.fisherman08.idea_websocket.client.WebSocketClientImpl

import com.intellij.openapi.diagnostic.Logger
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.panel
import com.intellij.util.ui.UIUtil
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

    private val urlField = JBTextField(properties.getUrls().firstOrNull())
    private val connectButton = JButton("Connect").apply {
        addActionListener { handleConnectButtonClicked() }
    }
    private val disconnectButton = JButton("Disconnect").apply {
        isEnabled = false
        addActionListener { handleDisconnectButtonClicked() }
    }

    private val isConnectedLabel = JLabel()

    private val responseArea = JBTextArea(15, 50).apply { isEditable = false }
    private val responseAreaScrollPane = JBScrollPane(responseArea)


    private val requestArea = JBTextArea()

    private val sendMessageButton = JButton("Send").apply {
        isEnabled = false
        addActionListener { handleSendMessageButtonClicked() }
    }

    private fun handleConnectButtonClicked() {
        try {
            this.client?.close()
            val url = ServerUri(urlField.text)
            val headers = Headers(emptyMap())
            val handler = EventHandler(
                onOpen = {},
                onClose = { handleConnectionClosed() },
                onError = { e ->
                    logger.warn(e)
                },
                onMessage = {message ->  responseArea.addMessage("Response => $message") }
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
        responseArea.addMessage("Request => ${requestArea.text}")
        this.client?.send(requestArea.text)
    }

    private fun handleConnectionSuccess() {
        properties.setUrl(urlField.text)
        isConnectedLabel.text = "Connected"
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
                cell {
                    label("URL: ", UIUtil.ComponentStyle.REGULAR, bold = true)
                    urlField()
                    connectButton()
                    disconnectButton()
                }
                cell {
                    label("")
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
                        requestArea()
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

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")

fun JBTextArea.addMessage(message: String) {
    val now = LocalDateTime.now()
    this.append("(${now.format(formatter)}) $message\n")
}

fun JBTextArea.truncateMessages() {
    this.text = ""
}
