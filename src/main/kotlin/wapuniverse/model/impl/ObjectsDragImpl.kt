package wapuniverse.model.impl

import javafx.beans.value.ObservableValue
import wapuniverse.geom.Vec2d
import wapuniverse.model.ObjectsDrag

class ObjectsDragImpl(
        override val origin: Vec2d,
        override val delta: ObservableValue<Vec2d>
) : ObjectsDrag
