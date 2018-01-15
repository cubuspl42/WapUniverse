package wapuniverse.view

import com.google.common.base.Preconditions
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.BoundingBox
import javafx.geometry.Rectangle2D
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Transform
import org.reactfx.EventSource
import org.reactfx.EventStream
import org.reactfx.EventStreams.eventsOf
import wapuniverse.view.ext.map
import wapuniverse.view.ext.position

interface Resizer {
    interface ResizeGesture {
        val newRect: ObservableValue<BoundingBox>
        val committed: EventStream<Unit>
    }

    val resizeGesture: ObservableValue<ResizeGesture?>
}

class ResizeGestureImpl(
        newRectInitial: BoundingBox
) : Resizer.ResizeGesture {
    private var closed = false

    override val newRect = SimpleObjectProperty<BoundingBox>(newRectInitial)

    private val _committed = EventSource<Unit>()
    override val committed = _committed as EventStream<Unit>

    fun commit() {
        Preconditions.checkState(!closed)
        _committed.push(Unit)
        closed = true
    }
}

class ResizerImpl(
        private val rect: ObservableValue<BoundingBox>
) : Resizer {
    private val _resizeGesture = SimpleObjectProperty<Resizer.ResizeGesture?>()
    override val resizeGesture = _resizeGesture as ObservableValue<Resizer.ResizeGesture?>

    init {
        resizeGesture.addListener { observable,
                                    oldValue,
                                    newValue ->
            newValue?.committed?.subscribe {
                _resizeGesture.set(null)
            }
        }
    }

    fun startResizeGesture(): ResizeGestureImpl {
        Preconditions.checkState(resizeGesture.value == null)
        return ResizeGestureImpl(rect.value).also {
            _resizeGesture.value = it
        }
    }
}

fun resizerController(
        resizer: ResizerImpl,
        resizerRectangle: Rectangle,
        inversedTransform: ObservableValue<Transform>
) {
    eventsOf(resizerRectangle, MouseEvent.MOUSE_PRESSED).subscribe { event ->
        resizer.startResizeGesture().let { resizeGesture ->
            resizeGestureController(resizerRectangle, resizeGesture, inversedTransform)
        }
    }
}

private fun resizeGestureController(
        resizerRectangle: Rectangle,
        resizeGesture: ResizeGestureImpl,
        inversedTransform: ObservableValue<Transform>
) {
    val mouseDragged = eventsOf(resizerRectangle, MouseEvent.MOUSE_DRAGGED).subscribe { event ->
        val boundingBox = resizeGesture.newRect.value
        resizeGesture.newRect.bind(inversedTransform.map { inversedTransform ->
            val eventPositionW = inversedTransform.transform(event.position.toPoint2D())
            BoundingBox(
                    boundingBox.minX,
                    boundingBox.minY,
                    boundingBox.maxX,
                    eventPositionW.y
            )
        })
    }
    val mouseReleased = eventsOf(resizerRectangle, MouseEvent.MOUSE_RELEASED).subscribe { event ->
        resizeGesture.commit()
    }
    resizeGesture.committed.subscribe {
        mouseDragged.unsubscribe()
        mouseReleased.unsubscribe()
    }
}

fun resizer(
        rect: ObservableValue<BoundingBox>,
        transform: ObservableValue<Transform>,
        handler: (Resizer) -> Unit
): Node {
    val model = ResizerImpl(rect)
    return presentRectangle(rect, transform).also {
        resizerController(model, it, transform.map { it.createInverse() })
        handler(model)
    }.apply {
        opacity = 0.2
    }
}
