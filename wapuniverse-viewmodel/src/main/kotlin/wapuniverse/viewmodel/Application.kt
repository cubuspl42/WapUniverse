package wapuniverse.viewmodel

import org.reactfx.EventSource
import org.reactfx.EventStream

class Application {
    private val onWindowCreatedSrc = EventSource<RootWindow>()

    val onWindowCreated = onWindowCreatedSrc as EventStream<RootWindow>

    fun start() {
        openNewRootWindow()
    }

    private fun openNewRootWindow() {
        onWindowCreatedSrc.emit(RootWindow(
        ))
    }
}
