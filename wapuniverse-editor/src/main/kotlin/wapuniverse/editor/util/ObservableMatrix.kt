package wapuniverse.editor.util

import org.reactfx.EventStream
import org.reactfx.EventStreamBase
import org.reactfx.Observable
import org.reactfx.Subscription
import wapuniverse.editor.util.ObservableMatrix.Change

private typealias Observer = (Change) -> Unit

class ObservableMatrix<T>(
        private val matrix: MutableMatrix<T>
) : MutableMatrix<T> by matrix, Observable<Observer> {
    data class Change(
            val i: Int,
            val j: Int
    )

    private val observers = mutableListOf<Observer>()

    fun put(i: Int, j: Int, value: T) {
        matrix.set(i, j, value)
        emitChange(Change(i, j))
    }

    override fun get(i: Int, j: Int) = matrix.get(i, j)

    override fun addObserver(observer: Observer) {
        observers.add(observer)
    }

    override fun removeObserver(observer: Observer) {
        observers.remove(observer)
    }

    private fun emitChange(change: Change) {
        observers.forEach { it(change) }
    }

    fun changes(): EventStream<Change> = changesOf(this)
}

fun observableIntMatrix(rowCount: Int, columnCount: Int, init: () -> Int) =
        ObservableMatrix(
                IntMatrix(rowCount, columnCount, init)
        )

private fun <T> changesOf(observable: Observable<(T) -> Unit>): EventStream<T> {
    return object : EventStreamBase<T>() {
        override fun observeInputs(): Subscription {
            val observer = { it: T -> emit(it) }
            addObserver(observer)
            return Subscription { removeObserver(observer) }
        }
    }
}
