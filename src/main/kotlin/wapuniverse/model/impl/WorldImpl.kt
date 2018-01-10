package wapuniverse.model.impl

import javafx.collections.FXCollections.observableArrayList
import javafx.collections.FXCollections.observableHashMap
import javafx.collections.ObservableMap
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import wapuniverse.geom.Vec2d
import wapuniverse.geom.Vec2i
import wapuniverse.model.Entity
import wapuniverse.model.World
import wapuniverse.rez.RezIndex

class WorldImpl(
        private val editorContext: EditorContextImpl,
        private val rezIndex: RezIndex
) : World {
    override val entities = observableArrayList<EntityImpl>()!!

    override val tiles: ObservableMap<Vec2i, Int> = observableHashMap()

    fun objectsAt(point: Vec2d): Set<Entity> =
            entities.filter { it.intersects(BoundingBox(point.x, point.y, 1.0, 1.0)) }.toSet()

    fun objectsIntersecting(bounds: Bounds): Set<Entity> =
            entities.filter { it.intersects(bounds) }.toSet()

    fun deleteObjects(objectsToDelete: Set<Entity>) {
        entities.removeAll(objectsToDelete)
    }

    fun addObject() = WapObjectImpl(editorContext, rezIndex).also {
        entities.add(it)
    }
}
