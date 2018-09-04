package wapuniverse.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableBooleanValue

class Action(
        val enabled: ObservableBooleanValue = SimpleBooleanProperty(true),
        private val executeFunction: () -> Unit
) {
    fun execute() {
        if (!enabled.value) {
            throw IllegalStateException("action is not enabled")
        }
        executeFunction()
    }
}
