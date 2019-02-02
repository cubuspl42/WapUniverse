package wapuniverse.editor.util

import javafx.beans.value.ObservableValue
import org.reactfx.EventSource
import org.reactfx.EventStream

open class Disposable(
        parent: Disposable? = null
) {
    private val onDisposedSrc = EventSource<Unit>()

    val onDisposed = onDisposedSrc as EventStream<Unit>

    private var isDisposed = false

    init {
        parent?.onDisposed?.subscribe { disposeInt() }
    }

    internal fun disposeInt() {
        if (isDisposed) return
        isDisposed = true
        onDisposedSrc.emit(Unit)
    }

    protected fun dispose() = disposeInt()

    protected fun <T> subscribe(stream: EventStream<T>, function: (T) -> Unit) {
        stream.subscribe(function).let { sub -> onDisposed.subscribe { sub.unsubscribe() } }
    }
}

fun ObservableValue<out Disposable?>.disposeOldValues() {
    addListener { observable, oldValue, newValue ->
        oldValue?.disposeInt()
    }
}
