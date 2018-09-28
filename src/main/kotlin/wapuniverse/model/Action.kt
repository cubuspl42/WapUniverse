package wapuniverse.model

import javafx.beans.value.ObservableValue
import wapuniverse.util.booleanProperty
import wapuniverse.view.extensions.flatMap

class Action(
        val enabled: ObservableValue<Boolean> = booleanProperty(true),
        private val block: () -> Unit
) {
    fun execute() {
        if (!enabled.value) {
            throw IllegalStateException("action is not enabled")
        }
        block()
    }
}

fun Action(observable: ObservableValue<Action?>) =
        Action(observable.flatMap { it.enabled }) {
            val action = observable.value ?: throw IllegalStateException()
            action.execute()
        }
