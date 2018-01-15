package wapuniverse.model

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import wapuniverse.geom.Rect2i
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

    override val rect = SimpleObjectProperty<Rect2i>(Rect2i(0, 0, 1, 1))

    override val tilePosition = rect.map { it.minV }

    override val position = rect.map { it.minV * T}

    override val metaTileGroup = MetaTileGroup().apply {
        tilePosition.bind(this@TileObjectImpl.tilePosition)
        metaTiles.bind(rect.map { ladder(it.size) })
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
        val oldRect = rect.value
        rect.value = Rect2i(newTilePosition.x, newTilePosition.y, oldRect.width, oldRect.height)
    }

}

private fun ladder(size: Vec2i): Map<Vec2i, MetaTile> {
    val (w, h) = size
    return (0 until h).map { i ->
        Vec2i(0, i) to when (i) {
            0 -> MetaTile.LADDER_TOP
            h - 1 -> MetaTile.LADDER_BOTTOM
            else -> MetaTile.LADDER_MID
        }
    }.toMap()
}
