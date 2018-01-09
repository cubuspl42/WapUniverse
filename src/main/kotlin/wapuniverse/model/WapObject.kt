package wapuniverse.model

import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableIntegerValue
import javafx.beans.value.ObservableObjectValue
import javafx.beans.value.ObservableStringValue
import javafx.geometry.BoundingBox

interface WapObject {
    val imageSet: ObservableStringValue
    val x: ObservableIntegerValue
    val y: ObservableIntegerValue
    val boundingBox: ObservableObjectValue<BoundingBox>
    val isHovered: ObservableBooleanValue
    val isSelected: ObservableBooleanValue
    val isPreselected: ObservableBooleanValue
}
