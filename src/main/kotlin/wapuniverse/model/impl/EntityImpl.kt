package wapuniverse.model.impl

import javafx.geometry.Bounds
import wapuniverse.geom.Vec2i
import wapuniverse.model.Entity
import wapuniverse.view.ext.asObservableBooleanValue
import wapuniverse.view.ext.setContains

abstract class EntityImpl(
        editorContext: EditorContextImpl,
        plane: PlaneImpl
) : Entity {
    private val activeToolContext = editorContext.activePlaneContext
            .flatMap { it!!.activeToolContext }

    private val selectToolContext = activeToolContext
            .map { it as? SelectToolContextImpl }

    override val isHovered = editorContext.activePlaneContext
            .flatMap { it!!.hoveredObjects }
            .map { it.contains(this) }
            .asObservableBooleanValue()

    override val isSelected = setContains(plane.selectedObjects, this)
            .asObservableBooleanValue()

    override val isPreselected = selectToolContext
            .flatMap { it!!.areaSelection }
            .flatMap { it.preselectedObjects }
            .map { it.contains(this) }
            .orElse(false)
            .asObservableBooleanValue()

    abstract fun intersects(bounds: Bounds): Boolean

    abstract fun setPosition(position: Vec2i)
}