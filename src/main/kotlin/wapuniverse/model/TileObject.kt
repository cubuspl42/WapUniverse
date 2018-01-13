package wapuniverse.model

import javafx.beans.value.ObservableValue
import wapuniverse.geom.Vec2i
import wapuniverse.model.impl.MetaTileGroup

interface TileObject : Entity {
    val tilePosition: ObservableValue<Vec2i>
    val metaTileGroup: MetaTileGroup
}
