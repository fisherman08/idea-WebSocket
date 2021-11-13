package com.github.fisherman08.idea_websocket.client

data class EventHandler(
    val onOpen: () -> Unit,
    val onClose: () -> Unit,
    val onError: (e: Throwable) -> Unit,
    val onMessage: (message: String) -> Unit,
)
