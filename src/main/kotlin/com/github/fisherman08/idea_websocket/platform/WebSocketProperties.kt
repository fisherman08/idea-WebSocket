package com.github.fisherman08.idea_websocket.platform

import com.intellij.ide.util.PropertiesComponent
import com.jetbrains.rd.util.remove

class WebSocketProperties {
    private val propertiesComponent = PropertiesComponent.getInstance()

    companion object {
        private const val KEY_URL = "com.github.fisherman08.idea_websocket.KEY_URL"
    }

    fun getUrls(): Array<String> = propertiesComponent.getValues(KEY_URL)?: emptyArray()

    fun setUrl(url: String) {
        val urls = getUrls()
        urls.remove(url)
        propertiesComponent.setValues(KEY_URL, arrayOf(url) + urls)
    }
}
