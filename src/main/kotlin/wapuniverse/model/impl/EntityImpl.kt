package wapuniverse.model.impl

import javafx.geometry.Bounds
import org.fxmisc.easybind.EasyBind
import wapuniverse.geom.Vec2i
import wapuniverse.model.Entity
import wapuniverse.view.ext.asObservableBooleanValue
import wapuniverse.view.ext.setContains

abstract class EntityImpl(
        editorContext: EditorContextImpl
) : Entity {
    private val selectToolContext = EasyBind.monadic(editorContext.activeToolContext)
            .map { it as? SelectToolContextImpl }

    override val isHovered = editorContext.hoveredObjects
            .map { it!!.contains(this) }
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

    abstract fun intersects(bounds: Bounds): Boolean

    abstract fun setPosition(position: Vec2i)
}