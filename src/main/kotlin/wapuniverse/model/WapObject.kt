package wapuniverse.model

import javafx.beans.value.ObservableIntegerValue
import javafx.beans.value.ObservableStringValue

interface WapObject {
    val imageSet: ObservableStringValue
    val x: ObservableIntegerValue
    val y: ObservableIntegerValue
}
