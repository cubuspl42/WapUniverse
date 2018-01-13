package wapuniverse.model.impl

import javafx.beans.property.SimpleObjectProperty
import jwap32.v1.Wwd
import org.fxmisc.easybind.EasyBind.monadic
import wapuniverse.geom.Vec2d
import wapuniverse.geom.Vec2i
import wapuniverse.model.EditorContext
import wapuniverse.model.Tool
import wapuniverse.rez.RezIndex
import wapuniverse.view.ext.map

class EditorContextImpl(
        rezIndex: RezIndex,
        wwd: Wwd
) : EditorContext {
    override val world = WorldImpl(this, rezIndex)

    override val hoverPositionProperty = SimpleObjectProperty<Vec2d>()

    val hoveredObjects = monadic(hoverPositionProperty)
            .map { world.objectsAt(it) }
            .orElse(emptySet())!!

    override val activeTool = SimpleObjectProperty<Tool>(Tool.SELECT)

    override val activeToolContext = activeTool.map {
        it!!
        when (it) {
            Tool.SELECT -> SelectToolContextImpl(world)
            Tool.MOVE -> MoveToolContextImpl(world)
        }
    }

    init {
        loadWorld(world, wwd)
    }
}


private fun loadWorld(world: WorldImpl, wwd: Wwd) {
    val plane = wwd.planes[1]
    plane.objects.forEach { wwdObject ->
        val wapObject = world.addObject()
        wapObject.imageSet.set(wwdObject.imageSet)
        wapObject.x.set(wwdObject.x)
        wapObject.y.set(wwdObject.y)
        wapObject.i.set(wwdObject.i)
    }
    for (i in 0 until plane.tilesHigh) {
        for (j in 0 until plane.tilesWide) {
            val tileId = plane.getTile(i, j)
            if (tileId > 0) {
                world.tiles.put(Vec2i(j, i), tileId)
            }
        }
    }
    world.addTileObject()
    world.addTileObject().apply {
        setPosition(Vec2i(0, 128))
    }
}