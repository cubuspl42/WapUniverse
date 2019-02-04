package wapuniverse.editor

import javafx.geometry.BoundingBox
import org.reactfx.Change
import org.reactfx.EventStream
import org.reactfx.value.Val
import wapuniverse.editor.extensions.subscribe
import wapuniverse.editor.util.Disposable
import wapuniverse.geom.EucVec2i
import wapuniverse.geom.Vec2i
import kotlin.math.min

class AreaSelectionContext(
        activePlaneContext: ActivePlaneContext,
        position: Val<Vec2i>
) : Disposable(activePlaneContext) {
    private val plane = activePlaneContext.plane

    private val startPosition = position.value

    val area = position.map {
        val ev = EucVec2i(startPosition, it)
        BoundingBox(
                min(ev.a.x, ev.b.x).toDouble(),
                min(ev.a.y, ev.b.y).toDouble(),
                ev.delta().width.toDouble(),
                ev.delta().height.toDouble()
        )
    }

    private val objectsInArea = area.map {
        plane.findObjects(it)
    }

    fun commit() {
        dispose()
    }

    init {
        objectsInArea.observe(this, this::unhighlightObjects, this::highlightObjects)

        onDisposed.subscribe {
            unhighlightObjects(objectsInArea.value)
        }
    }

    private fun highlightObjects(objects: Set<WapObject>) {
        objects.forEach { it.highlight() }
    }

    private fun unhighlightObjects(objects: Set<WapObject>) {
        objects.forEach { it.unhighlight() }
    }
}

private fun <T> Val<T>.observe(disposable: Disposable, consumeOldValue: (T) -> Unit, consumeNewValue: (T) -> Unit) {
    consumeNewValue(this.value)
    observeChanges(disposable) { oldValue, newValue ->
        consumeOldValue(oldValue)
        consumeNewValue(newValue)
    }
}

private fun <T, U> Val<T>.map(disposable: Disposable, function: (T) -> U) =
        map { function(it) }.pin(disposable)

private operator fun <T> Change<T>.component1() = this.oldValue

private operator fun <T> Change<T>.component2() = this.newValue

private fun <T> Val<T>.observeChanges(disposable: Disposable, function: (T, T) -> Unit) {
    val sub = observeChanges { _, oldValue, newValue -> function(oldValue, newValue) }
    disposable.onDisposed.subscribe { sub.unsubscribe() }
}

private fun <T> Val<T>.pin(disposable: Disposable) {
    val sub = pin()
    disposable.onDisposed.subscribe { sub.unsubscribe() }
}