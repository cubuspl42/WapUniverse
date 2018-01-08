package wapuniverse.view.ext

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableObjectValue
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import wapuniverse.geom.Vec2d

fun Node.parentToLocal(v: Vec2d): Vec2d =
        parentToLocal(v.toPoint2D()).toVec2d()

fun Node.hoverPositionProperty(): ObservableObjectValue<Point2D> {
    val property = SimpleObjectProperty<Point2D>()

    this.addEventHandler(MouseEvent.MOUSE_ENTERED) { event ->
        property.set(Point2D(event.x, event.y))
    }

    this.addEventHandler(MouseEvent.MOUSE_MOVED) { event ->
        property.set(Point2D(event.x, event.y))
    }

    this.addEventHandler(MouseEvent.MOUSE_EXITED) { event ->
        property.set(null)
    }

    return property
}
