package wapuniverse.model

import com.google.common.base.Preconditions
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections.observableMap
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import wapuniverse.geom.Vec2i
import wapuniverse.model.impl.EditorContextImpl
import wapuniverse.model.impl.EntityImpl
import wapuniverse.view.ext.map

private val T = 64

class TileObjectImpl(
        editorContext: EditorContextImpl
) : TileObject, EntityImpl(editorContext) {
    override val tilePosition = SimpleObjectProperty<Vec2i>(Vec2i())

    override val position = tilePosition.map { it * T }

    override val tiles = observableMap<Vec2i, Int>(mutableMapOf(
            Vec2i() to 12
    ))

    override fun intersects(bounds: Bounds): Boolean {
        return tiles.any { (index, _) ->
            val t = T.toDouble()
            BoundingBox(index.x * t, index.y * t, t, t).intersects(bounds)
        }
    }

    override fun setPosition(position: Vec2i) {
        tilePosition.value = position / T
    }

}
