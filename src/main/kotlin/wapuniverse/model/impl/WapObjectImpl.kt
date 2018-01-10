package wapuniverse.model.impl

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.BoundingBox
import org.fxmisc.easybind.EasyBind.combine
import org.fxmisc.easybind.EasyBind.monadic
import wapuniverse.geom.Vec2i
import wapuniverse.model.WapObject
import wapuniverse.rez.RezIndex
import wapuniverse.view.ext.asObservableBooleanValue
import wapuniverse.view.ext.setContains

class WapObjectImpl(
        editorContext: EditorContextImpl,
        rezIndex: RezIndex
) : WapObject {
    private val selectToolContext = monadic(editorContext.activeToolContext)
            .map { it as? SelectToolContextImpl }

    override val imageSet = SimpleStringProperty("LEVEL1_OFFICER")

    override val x = SimpleIntegerProperty()

    override val y = SimpleIntegerProperty()

    override val boundingBox = combine(imageSet, x, y) { imageSet, x, y ->
        val fullyQualifiedImageSetId = resolveShortId(imageSet)
        rezIndex.findImageMetadata(fullyQualifiedImageSetId, -1)?.let { metadata ->
            val minX = x.toDouble() + metadata.offset.x
            val minY = y.toDouble() + metadata.offset.y
            val width = metadata.size.width
            val height = metadata.size.height
            BoundingBox(minX, minY, width.toDouble(), height.toDouble())
        } ?: BoundingBox(0.0, 0.0, 0.0, 0.0)
    }!!

    override val isHovered = editorContext.hoveredObjects
            .map { it.contains(this) }
            .asObservableBooleanValue()

    override val isSelected = selectToolContext
            .flatMap { setContains(it!!.selectedObjects, this) }
            .asObservableBooleanValue()

    override val isPreselected = selectToolContext
            .flatMap { it!!.areaSelection }
            .flatMap { it.preselectedObjects }
            .map { it.contains(this) }
            .orElse(false)
            .asObservableBooleanValue()

    val position = combine(x, y) { x, y -> Vec2i(x.toInt(), y.toInt()) }

    fun setPosition(position: Vec2i) {
        x.value = position.x
        y.value = position.y
    }
}

private fun resolveShortId(imageSet: String): String {
    return imageSet.replace("LEVEL_", "LEVEL1_IMAGES_")
}
