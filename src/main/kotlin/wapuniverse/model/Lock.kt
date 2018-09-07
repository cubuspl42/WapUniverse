package wapuniverse.model

import javafx.beans.binding.Bindings.not
import javafx.beans.value.ObservableBooleanValue
import wapuniverse.util.booleanProperty

class Lock {
    val isLocked: ObservableBooleanValue

    val isUnlocked: ObservableBooleanValue

    private var mLocked = booleanProperty(false)

    init {
        isLocked = mLocked
        isUnlocked = not(isLocked)
    }

    fun lockNow() {
        check(!isLocked.value)
        mLocked.value = true
    }

    fun unlock() {
        check(isLocked.value)
        mLocked.value = false
    }
}
