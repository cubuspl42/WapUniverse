package wapuniverse.view

import com.google.common.base.Preconditions
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.BoundingBox
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Transform
import org.fxmisc.easybind.EasyBind
import org.fxmisc.easybind.EasyBind.monadic
import org.reactfx.EventSource
import org.reactfx.EventStream
import org.reactfx.EventStreams.eventsOf
import wapuniverse.geom.Vec2d
import wapuniverse.view.ext.bottomLeftCorner
import wapuniverse.view.ext.bottomRightCorner
import wapuniverse.view.ext.map
import wapuniverse.view.ext.position
import wapuniverse.view.ext.topLeftCorner
import wapuniverse.view.ext.topRightCorner
import java.lang.Math.max
import java.lang.Math.min

private val handleWidth = 10.0

enum class ResizeDirection {
    TOP_LEFT,
    LEFT,
    BOTTOM_LEFT,
    BOTTOM,
    BOTTOM_RIGHT,
    RIGHT,
    TOP_RIGHT,
    TOP,
}

class ResizeInteraction(
        private val rectangle: ObservableValue<Rectangle2D>,
        private val resizeDirection: ResizeDirection,
        private val dragPosition: ObservableValue<Point2D>
) {
    val resizedRectangle = SimpleObjectProperty<Rectangle2D>().apply {
        bind(dragPosition.map { buildResizedRectangle(it) })
    }

    private fun buildResizedRectangle(dragPosition: Point2D): Rectangle2D {
        val p = dragPosition
        return rectangle.value.run {
            when (resizeDirection) {
                ResizeDirection.TOP -> rectangle(minX, p.y, maxX, maxY)
                ResizeDirection.TOP_LEFT -> rectangle(p.x, p.y, maxX, maxY)
                ResizeDirection.LEFT -> rectangle(p.x, minY, maxX, maxY)
                ResizeDirection.BOTTOM_LEFT -> rectangle(p.x, minY, maxX, p.y)
                ResizeDirection.BOTTOM -> rectangle(minX, minY, maxX, p.y)
                ResizeDirection.BOTTOM_RIGHT -> rectangle(minX, minY, p.x, p.y)
                ResizeDirection.RIGHT -> rectangle(minX, minY, p.x, maxY)
                ResizeDirection.TOP_RIGHT -> rectangle(minX, p.y, p.x, maxY)
            }
        }
    }
}

private fun rectangle(x0: Double, y0: Double, x1: Double, y1: Double): Rectangle2D {
    val minX = min(x0, x1)
    val maxX = max(x0, x1)
    val minY = min(y0, y1)
    val maxY = max(y0, y1)
    return Rectangle2D(minX, minY, maxX - minX, maxY - minY)
}

private fun rectangle(minV: Point2D, maxV: Point2D) =
        Rectangle2D(minV.x, minV.y, maxV.x - minV.x, maxV.y - minV.y)

private fun makeRectangle(minV: ObservableValue<Point2D>, maxV: ObservableValue<Point2D>): ObservableValue<Rectangle2D> =
        EasyBind.combine(minV, maxV, ::rectangle)

class ResizerNode(
        private val rectangle: ObservableValue<Rectangle2D>,
        private val transform: ObservableValue<Transform>
) : Group() {
    private val _resizeInteraction = SimpleObjectProperty<ResizeInteraction>()

    val resizeInteraction = _resizeInteraction as ObservableValue<ResizeInteraction>

    init {
        val tl = rectangle.map { it.topLeftCorner() }
        val bl = rectangle.map { it.bottomLeftCorner() }
        val br = rectangle.map { it.bottomRightCorner() }
        val tr = rectangle.map { it.topRightCorner() }
        children.setAll(
                transformedRectangle(rectangle, transform).apply {
                    fill = Color.TRANSPARENT
                    stroke = Color.RED
                },
                sideHandle(tl, bl, ResizeDirection.LEFT),
                sideHandle(bl, br, ResizeDirection.BOTTOM),
                sideHandle(tr, br, ResizeDirection.RIGHT),
                sideHandle(tl, tr, ResizeDirection.TOP),
                cornerHandle(tl, ResizeDirection.TOP_LEFT),
                cornerHandle(bl, ResizeDirection.BOTTOM_LEFT),
                cornerHandle(br, ResizeDirection.BOTTOM_RIGHT),
                cornerHandle(tr, ResizeDirection.TOP_RIGHT)
        )
    }

    private fun sideHandle(
            v0: ObservableValue<Point2D>,
            v1: ObservableValue<Point2D>,
            direction: ResizeDirection
    ): Node {
        val a = Vec2d(handleWidth / 2, handleWidth / 2)
        return handle(v0 - a, v1 + a, direction)
    }

    private fun cornerHandle(
            v0: ObservableValue<Point2D>,
            direction: ResizeDirection
    ): Node {
        val a = Vec2d(handleWidth / 2, handleWidth / 2)
        return handle(v0 - a, v0 + a, direction)
    }

    private fun handle(
            v0: ObservableValue<Point2D>,
            v1: ObservableValue<Point2D>,
            direction: ResizeDirection
    ): Node {
        return transformedRectangle(makeRectangle(v0, v1), transform).apply {
            stroke = Color.YELLOW
            opacity = 0.2

            eventsOf(this, MouseEvent.MOUSE_PRESSED).subscribe { event ->
                val dragPosition = eventsOf(this, MouseEvent.MOUSE_DRAGGED)
                        .map { transform.value.inverseTransform(it.position.toPoint2D()) }
                        .toBinding(transform.value.inverseTransform(event.position.toPoint2D()))
                _resizeInteraction.value = ResizeInteraction(rectangle, direction, dragPosition)
                eventsOf(this, MouseEvent.MOUSE_RELEASED).subscribe {
                    dragPosition.dispose()
                }
            }
        }
    }
}


operator fun ObservableValue<Point2D>.plus(a: Vec2d): ObservableValue<Point2D> {
    return this.map { it + a }
}

operator fun Point2D.plus(v: Vec2d): Point2D {
    return this.add(v.x, v.y)
}

operator fun ObservableValue<Point2D>.minus(v: Vec2d): ObservableValue<Point2D> {
    return this.map { it.subtract(v.x, v.y) }
}

operator fun ObservableValue<Transform>.invoke(point: ObservableValue<Point2D>) = this.transform(point)

fun ObservableValue<Transform>.transform(point: ObservableValue<Point2D>): ObservableValue<Point2D> {
    return EasyBind.combine(this, point) { transformVal, pointVal ->
        transformVal.transform(pointVal)
    }
}

fun ObservableValue<Transform>.transform(point: Point2D): ObservableValue<Point2D> {
    return this.map { it.transform(point) }
}
