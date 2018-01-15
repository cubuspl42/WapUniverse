package wapuniverse.model

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import wapuniverse.geom.Rect2i
import wapuniverse.geom.Vec2i
import wapuniverse.model.impl.MetaTileGroup

interface TileObject : Entity {
    val tilePosition: ObservableValue<Vec2i>
    val metaTileGroup: MetaTileGroup
    val rect: Property<Rect2i>
}
