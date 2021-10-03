package com.github.fisherman08.idea_websocket.platform

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.enteredTextSatisfies
import com.intellij.ui.layout.panel
import com.intellij.util.ui.UIUtil
import java.awt.Color
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class WebSocketToolWindow {

    private val urlField = JBTextField().apply {
        document.addDocumentListener(object : DocumentListener {
            override fun changedUpdate(e: DocumentEvent?) =
                handleUrlChanged(this@apply.text, isConnectedLabel)

            override fun insertUpdate(e: DocumentEvent?) =
                handleUrlChanged(this@apply.text, isConnectedLabel)

            override fun removeUpdate(e: DocumentEvent?) =
                handleUrlChanged(this@apply.text, isConnectedLabel)
        })
    }
    private val isConnectedLabel = JLabel()

    private val responseArea = JBLabel("<html>ここに文字を書く<br>改行後<html>").apply {
        fontColor = UIUtil.FontColor.NORMAL
        background = Color.BLACK
        this.size = Dimension(50, 10)
        updateUI()
    }
    private val requestArea = JBTextArea()

    private fun handleConnect() {
        val url = urlField.text
    }

    private fun handleUrlChanged(url: String, label: JLabel) {
        if (url.matches(Regex("https://.+"))) {
            label.text = "Connected"
            label.foreground = Color.GREEN
        } else {
            label.text = "Not Connected"
            label.foreground = Color.RED
        }
    }

    fun getComponent(): JComponent {
        return panel {
            row {
                cell {
                    label("URL: ", UIUtil.ComponentStyle.REGULAR, bold = true)
                    urlField()
                    button("connect") { handleConnect() }.enableIf(urlField.enteredTextSatisfies { url ->
                        url.matches(
                            Regex("https://.+")
                        )
                    })
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
                        responseArea()
                    }
                    cell {
                        requestArea()
                    }
                }
            }

        }
    }
}
