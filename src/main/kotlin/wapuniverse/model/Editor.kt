package wapuniverse.model

import javafx.beans.value.ObservableBooleanValue
import wapuniverse.util.booleanProperty

class Editor(
        val baseLevel: BaseLevel
) {
    val saved: ObservableBooleanValue

    fun isSaved(): Boolean = saved.value

    private val mSaved = booleanProperty(false)

    init {
        saved = mSaved
    }

    fun save() {
        check(!isSaved())
    }
}
