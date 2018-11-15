package wapuniverse.util

import javafx.beans.InvalidationListener
import javafx.collections.ObservableSet
import javafx.collections.SetChangeListener
import javafx.collections.WeakSetChangeListener

class MappedSet<T, R>(
        private val source: ObservableSet<T>,
        private val transform: (T) -> R
) : ObservableSet<R> {
    private val changeListeners = mutableListOf<SetChangeListener<in R>>()

    private val map =
            mutableMapOf<T, R>(*source.toList().map { it to transform(it) }.toTypedArray())

    init {
        source.addListener(WeakSetChangeListener<T> { change ->
            if (change.wasAdded()) {
                onElementAdded(change.elementAdded)
            } else {
                assert(change.wasRemoved())
                onElementRemoved(change.elementRemoved)
            }
        })
    }

    private fun onElementAdded(element: T) {
        val mappedElement = transform(element)
        map[element] = mappedElement
        invokeListeners(object : SetChangeListener.Change<R>(this) {
            override fun wasAdded() = true
            override fun wasRemoved() = false
            override fun getElementAdded() = mappedElement!!
            override fun getElementRemoved() = null
        })
    }

    private fun onElementRemoved(element: T) {
        val mappedElement = map.remove(element)!!
        invokeListeners(object : SetChangeListener.Change<R>(this) {
            override fun wasAdded() = false
            override fun wasRemoved() = true
            override fun getElementAdded() = null
            override fun getElementRemoved() = mappedElement
        })
    }

    private fun invokeListeners(change: SetChangeListener.Change<R>) {
        changeListeners.forEach { it.onChanged(change) }
    }

    override fun iterator() = map.values.iterator()

    override val size: Int
        get() = source.size

    override fun contains(element: R): Boolean {
        TODO("not implemented")
    }

    override fun isEmpty(): Boolean {
        TODO("not implemented")
    }

    override fun addListener(listener: SetChangeListener<in R>) {
        changeListeners.add(listener)
    }

    override fun addListener(listener: InvalidationListener) {
        TODO("not implemented")
    }

    override fun removeListener(listener: SetChangeListener<in R>) {
        changeListeners.remove(listener)
    }

    override fun removeListener(listener: InvalidationListener) {
        TODO("not implemented")
    }

    override fun containsAll(elements: Collection<R>): Boolean {
        TODO("not implemented")
    }

    override fun add(element: R?) = throw UnsupportedOperationException()

    override fun remove(element: R) = throw UnsupportedOperationException()

    override fun addAll(elements: Collection<R>) = throw UnsupportedOperationException()

    override fun retainAll(elements: Collection<R>) = throw UnsupportedOperationException()

    override fun removeAll(elements: Collection<R>) = throw UnsupportedOperationException()

    override fun clear() = throw UnsupportedOperationException()
}

fun <T, R> ObservableSet<T>.map(f: (T) -> R): ObservableSet<R> {
    return MappedSet(this, f)
}
