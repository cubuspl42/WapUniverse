package wapuniverse.model

import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableIntegerValue
import javafx.beans.value.ObservableObjectValue
import javafx.beans.value.ObservableStringValue
import javafx.geometry.BoundingBox
import wapuniverse.rez.RezImageMetadata

interface WapObject {
    val imageSet: ObservableStringValue
    val x: ObservableIntegerValue
    val y: ObservableIntegerValue
    val i: ObservableIntegerValue
    val rezImageMetadata: ObservableObjectValue<RezImageMetadata?>
    val boundingBox: ObservableObjectValue<BoundingBox>
    val isHovered: ObservableBooleanValue
    val isSelected: ObservableBooleanValue
    val isPreselected: ObservableBooleanValue
}
