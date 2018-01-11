package wapuniverse.model.impl

import javafx.collections.FXCollections.observableArrayList
import javafx.collections.FXCollections.observableHashMap
import javafx.collections.FXCollections.observableSet
import javafx.collections.ObservableMap
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import wapuniverse.geom.Vec2d
import wapuniverse.geom.Vec2i
import wapuniverse.model.Entity
import wapuniverse.model.TileObjectImpl
import wapuniverse.model.World
import wapuniverse.rez.RezIndex

class WorldImpl(
        private val editorContext: EditorContextImpl,
        private val rezIndex: RezIndex
) : World {
    override val entities = observableArrayList<EntityImpl>()!!

    override val tiles: ObservableMap<Vec2i, Int> = observableHashMap()

    override val selectedObjects = observableSet<EntityImpl>()

    fun objectsAt(point: Vec2d): Set<EntityImpl> =
            entities.filter { it.intersects(BoundingBox(point.x, point.y, 1.0, 1.0)) }.toSet()

    fun objectsIntersecting(bounds: Bounds): Set<EntityImpl> =
            entities.filter { it.intersects(bounds) }.toSet()

    fun deleteObjects(objectsToDelete: Set<Entity>) {
        entities.removeAll(objectsToDelete)
    }

    fun selectObjects(objectsToSelect: Set<EntityImpl>) {
        selectedObjects.clear()
        selectedObjects.addAll(objectsToSelect)
    }

    fun addObject() = WapObjectImpl(editorContext, rezIndex).also {
        entities.add(it)
    }

    fun addTileObject() = TileObjectImpl(editorContext).also {
        entities.add(it)
    }
}
