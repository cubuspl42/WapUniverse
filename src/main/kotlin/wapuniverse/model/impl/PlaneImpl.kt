package wapuniverse.model.impl

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.FXCollections.observableSet
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import wapuniverse.geom.Vec2d
import wapuniverse.model.Entity
import wapuniverse.model.Plane
import wapuniverse.model.TileObjectImpl
import wapuniverse.rez.RezIndex

class PlaneImpl(
        private val editorContext: EditorContextImpl,
        private val rezIndex: RezIndex
) : Plane {
    override val world = editorContext.world

    override val entities = observableArrayList<EntityImpl>()!!

    val metaTileLayer = MetaTileLayer()

    override val tiles = metaTileLayer.tiles

    override val selectedObjects = observableSet<EntityImpl>()!!

    override val name = SimpleStringProperty()

    override var imageSet = ""

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

    fun addObject() = WapObjectImpl(editorContext, rezIndex, this, world).also {
        entities.add(it)
    }

    fun addTileObject() = TileObjectImpl(editorContext, this).also {
        entities.add(it)
    }
}