package wapuniverse.model

import javafx.beans.value.ObservableBooleanValue
import io.github.jwap32.v1.Wwd
import wapuniverse.util.booleanProperty

class Editor(
       private val wwd: Wwd
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
