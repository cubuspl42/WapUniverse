package wapuniverse.util

import javafx.beans.InvalidationListener
import javafx.collections.ObservableSet
import javafx.collections.SetChangeListener
import javafx.collections.WeakSetChangeListener

class UnionSet<E>(
        private val setA: ObservableSet<E>,
        private val setB: ObservableSet<E>
) : ObservableSet<E> {
    private val changeListeners = mutableListOf<SetChangeListener<in E>>()

    init {
        addListener(setA)
        addListener(setB)
    }

    private fun addListener(set: ObservableSet<E>) {
        set.addListener(WeakSetChangeListener<E> { change ->
            if (change.wasAdded()) {
                onElementAdded(change.elementAdded)
            } else {
                assert(change.wasRemoved())
                onElementRemoved(change.elementRemoved)
            }
        })
    }

    private fun onElementAdded(element: E) {
        invokeListeners(object : SetChangeListener.Change<E>(this) {
            override fun wasAdded() = true
            override fun wasRemoved() = false
            override fun getElementAdded() = element
            override fun getElementRemoved() = null
        })
    }

    private fun onElementRemoved(element: E) {
        invokeListeners(object : SetChangeListener.Change<E>(this) {
            override fun wasAdded() = false
            override fun wasRemoved() = true
            override fun getElementAdded() = null
            override fun getElementRemoved() = element
        })
    }

    private fun invokeListeners(change: SetChangeListener.Change<E>) {
        changeListeners.forEach { it.onChanged(change) }
    }

    override fun iterator() =
            mutableIterator(setA.asSequence() + setB.asSequence())

    override fun addListener(listener: SetChangeListener<in E>) {
        changeListeners.add(listener)
    }

    override fun removeListener(listener: SetChangeListener<in E>) {
        changeListeners.remove(listener)
    }

    override val size: Int
        get() = setA.size + setB.size

    override fun contains(element: E): Boolean {
        TODO("not implemented")
    }

    override fun isEmpty(): Boolean {
        TODO("not implemented")
    }

    override fun addListener(listener: InvalidationListener) {
        TODO("not implemented")
    }

    override fun removeListener(listener: InvalidationListener) {
        TODO("not implemented")
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        TODO("not implemented")
    }

    override fun add(element: E?) = throw UnsupportedOperationException()

    override fun remove(element: E) = throw UnsupportedOperationException()

    override fun addAll(elements: Collection<E>) = throw UnsupportedOperationException()

    override fun retainAll(elements: Collection<E>) = throw UnsupportedOperationException()

    override fun removeAll(elements: Collection<E>) = throw UnsupportedOperationException()

    override fun clear() = throw UnsupportedOperationException()
}

private fun <T> mutableIterator(iterable: Sequence<T>) = mutableIterator(iterable.iterator())

private fun <T> mutableIterator(iterator: Iterator<T>): MutableIterator<T> =
        object : Iterator<T> by iterator, MutableIterator<T> {
            override fun remove(): Unit = throw UnsupportedOperationException()
        }

operator fun <T> ObservableSet<T>.plus(other: ObservableSet<T>): ObservableSet<T> {
    return UnionSet(this, other)
}
