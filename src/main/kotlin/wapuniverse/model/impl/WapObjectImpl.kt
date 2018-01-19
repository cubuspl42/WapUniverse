package wapuniverse.model.impl

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import org.fxmisc.easybind.EasyBind.combine
import org.fxmisc.easybind.EasyBind.monadic
import wapuniverse.geom.Vec2i
import wapuniverse.model.WapObject
import wapuniverse.rez.RezIndex
import wapuniverse.view.ext.asObservableBooleanValue
import wapuniverse.view.ext.setContains

class WapObjectImpl(
        editorContext: EditorContextImpl,
        rezIndex: RezIndex,
        plane: PlaneImpl,
        world: WorldImpl
) : WapObject, EntityImpl(editorContext, plane) {
//    private val selectToolContext = monadic(editorContext.activeToolContext)
//            .map { it as? SelectToolContextImpl }

    override val imageSet = SimpleStringProperty("")

    override val x = SimpleIntegerProperty(0)

    override val y = SimpleIntegerProperty(0)

    override val i = SimpleIntegerProperty(-1)

    override val rezImageMetadata = combine(imageSet, i) { imageSet, i ->
        val fullyQualifiedImageSetId = world.resolveImageSetId(imageSet)
        rezIndex.findImageMetadata(fullyQualifiedImageSetId, i.toInt())
    }

    override val boundingBox = combine(rezImageMetadata, x, y) { rezImageMetadata, x, y ->
        rezImageMetadata?.let { metadata ->
            val minX = x.toDouble() + metadata.offset.x - metadata.size.width / 2
            val minY = y.toDouble() + metadata.offset.y - metadata.size.height / 2
            val width = metadata.size.width
            val height = metadata.size.height
            BoundingBox(minX, minY, width.toDouble(), height.toDouble())
        } ?: BoundingBox(0.0, 0.0, 0.0, 0.0)
    }!!

    override val position = combine(x, y) { x, y -> Vec2i(x.toInt(), y.toInt()) }

    override fun setPosition(position: Vec2i) {
        x.value = position.x
        y.value = position.y
    }

    override fun intersects(bounds: Bounds) =
            boundingBox.value.intersects(bounds)
}