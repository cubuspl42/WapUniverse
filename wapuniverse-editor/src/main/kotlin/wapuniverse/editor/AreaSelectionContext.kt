package wapuniverse.editor

import org.reactfx.Change
import org.reactfx.value.Val
import wapuniverse.editor.util.Disposable
import wapuniverse.geom.Rect2i
import wapuniverse.geom.Vec2i

class AreaSelectionContext(
        objectModeContext: ObjectModeContext,
        position: Val<Vec2i>
) : Disposable(objectModeContext) {
    private val plane = objectModeContext.plane

    private val startPosition = position.value

    val area = position.map {
        Rect2i.fromDiagonal(startPosition, it)
    }

    private val objectsInArea = area.map {
        plane.findObjects(it)
    }

    fun commit() {
        check(!isDisposed)
        objectsInArea.value?.let { plane.selectObjects(it) }
        dispose()
    }

    init {
        plane.unselectAllObjects()

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