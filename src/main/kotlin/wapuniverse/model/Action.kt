package wapuniverse.model

import javafx.beans.value.ObservableBooleanValue
import wapuniverse.util.booleanProperty

class Action(
        val enabled: ObservableBooleanValue = booleanProperty(true),
        private val block: () -> Unit
) {
    fun execute() {
        if (!enabled.value) {
            throw IllegalStateException("action is not enabled")
        }
        block()
    }
}
