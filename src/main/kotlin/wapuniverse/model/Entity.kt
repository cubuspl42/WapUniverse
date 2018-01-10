package wapuniverse.model

import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableValue
import wapuniverse.geom.Vec2i

interface Entity {
    val position: ObservableValue<Vec2i>
    val isHovered: ObservableBooleanValue
    val isSelected: ObservableBooleanValue
    val isPreselected: ObservableBooleanValue
}
