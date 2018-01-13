package wapuniverse.model

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import wapuniverse.geom.Vec2i
import wapuniverse.model.impl.EditorContextImpl
import wapuniverse.model.impl.EntityImpl
import wapuniverse.model.impl.MetaTileGroup
import wapuniverse.view.ext.map

private val T = 64

class TileObjectImpl(
        editorContext: EditorContextImpl
) : TileObject, EntityImpl(editorContext) {
    private val world = editorContext.world

    override val tilePosition = SimpleObjectProperty<Vec2i>(Vec2i())

    override val position = tilePosition.map { it * T }

    override val metaTileGroup = MetaTileGroup().apply {
        tilePosition.bind(this@TileObjectImpl.tilePosition)
        metaTiles.value = mapOf(
                Vec2i() to MetaTile.BLOCK_LEFT,
                Vec2i(1, 0) to MetaTile.BLOCK_TOP)
    }.also {
        world.metaTileLayer.metaTileGroups.add(it)
    }

    override fun intersects(bounds: Bounds): Boolean {
        return metaTileGroup.metaTilesG.value.any { (index, _) ->
            val t = T.toDouble()
            BoundingBox(index.x * t, index.y * t, t, t).intersects(bounds)
        }
    }

    override fun setPosition(position: Vec2i) {
        val newTilePosition = position / T
        tilePosition.value = newTilePosition
    }

}
