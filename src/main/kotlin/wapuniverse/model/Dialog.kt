package wapuniverse.model

import javafx.beans.value.ObservableBooleanValue
import wapuniverse.model.util.Disposable
import wapuniverse.util.booleanProperty

open class Dialog : Disposable() {
    val isOpen: ObservableBooleanValue

    fun close() {
        mIsOpen.value = false
    }

    fun addCloseListener(block: () -> Unit) {
        isOpen.addListener { _, _, open ->
            if (open == false) {
                block()
            }
        }
    }

    private val mIsOpen = booleanProperty(true)

    init {
        isOpen = mIsOpen
    }
}
