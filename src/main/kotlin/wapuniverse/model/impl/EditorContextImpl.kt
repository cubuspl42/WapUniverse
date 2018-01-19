package wapuniverse.model.impl

import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import jwap32.v1.Wwd
import jwap32.v1.WwdPlane
import org.fxmisc.easybind.EasyBind.monadic
import wapuniverse.geom.Vec2d
import wapuniverse.geom.Vec2i
import wapuniverse.model.EditorContext
import wapuniverse.model.Plane
import wapuniverse.model.Tool
import wapuniverse.model.ToolContext
import wapuniverse.rez.RezIndex
import wapuniverse.view.ext.map

class EditorContextImpl(
        rezIndex: RezIndex,
        wwd: Wwd
) : EditorContext {
    override val world = WorldImpl(this, rezIndex)

    override val activePlane = SimpleObjectProperty<PlaneImpl>()

    override val activePlaneContext = activePlane.map { it?.let { PlaneContextImpl(this, it) } }

    override val activeTool = SimpleObjectProperty(Tool.SELECT)

    init {
        loadWorld(world, wwd)
        activePlane.value = world.planes.getOrNull(0)
    }
}

interface PlaneContext {
    val plane: Plane

    val hoverPositionProperty: Property<Vec2d>

    val activeToolContext: ObservableValue<out ToolContext>
}

class PlaneContextImpl(
        editorContext: EditorContextImpl,
        override val plane: PlaneImpl
) : PlaneContext {
    override val hoverPositionProperty = SimpleObjectProperty<Vec2d>()

    val hoveredObjects = monadic(hoverPositionProperty)
            .map { plane.objectsAt(it) }
            .orElse(emptySet())!!

    override val activeToolContext = editorContext.activeTool.map {
        it!!
        when (it) {
            Tool.SELECT -> SelectToolContextImpl(plane)
            Tool.MOVE -> MoveToolContextImpl(plane)
        }
    }
}

private fun loadWorld(world: WorldImpl, wwd: Wwd) {
    wwd.planes.forEach {
        loadPlane(world, it)
    }
    world.planes.getOrNull(1)?.run {
        addTileObject()
        addTileObject().apply {
            setPosition(Vec2i(0, 128))
        }
    }
}

private fun loadPlane(world: WorldImpl, wwdPlane: WwdPlane) {
    val plane = world.addPlane()
    plane.name.value = wwdPlane.name
    plane.imageSet = wwdPlane.imageSets.first()
    wwdPlane.objects.forEach { wwdObject ->
        val wapObject = plane.addObject()
        wapObject.imageSet.set(wwdObject.imageSet)
        wapObject.x.set(wwdObject.x)
        wapObject.y.set(wwdObject.y)
        wapObject.i.set(wwdObject.i)
    }
    for (i in 0 until wwdPlane.tilesHigh) {
        for (j in 0 until wwdPlane.tilesWide) {
            val tileId = wwdPlane.getTile(i, j)
            if (tileId > 0) {
                plane.tiles.put(Vec2i(j, i), tileId)
            }
        }
    }
}