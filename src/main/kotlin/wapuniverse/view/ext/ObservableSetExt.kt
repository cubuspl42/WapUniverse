package wapuniverse.view.ext

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableBooleanValue
import javafx.collections.ObservableSet
import javafx.collections.SetChangeListener
import javafx.scene.Group
import javafx.scene.Node

private class MapToBinding<E>(
        set: ObservableSet<E>,
        private val destination: Group,
        private val transform: (element: E) -> Node
) {
    private val nodeMap = mutableMapOf<E, Node>()

    init {
        set.forEach { mapElement(it) }

        set.addListener { change: SetChangeListener.Change<out E> ->
            mapChangedElement(change) { changedElement ->
                if (change.wasAdded())
                    mapElement(changedElement)
                else
                    unmapElement(changedElement)
            }
        }
    }

    private fun mapElement(element: E) {
        val node = transform(element)
        nodeMap[element] = node
        destination.children.add(node)
    }

    private fun unmapElement(element: E) {
        val node = nodeMap[element]!!
        destination.children.remove(node)
    }
}

fun <E> ObservableSet<E>.mapTo(destination: Group, transform: (element: E) -> Node): Group {
    MapToBinding(this, destination, transform)
    return destination
}

fun <E> setContains(set: ObservableSet<E>, element: E): ObservableBooleanValue {
    val contains = SimpleBooleanProperty(set.contains(element))
    set.addListener { change: SetChangeListener.Change<out E> ->
        mapChangedElement(change) { changedElement ->
            if (changedElement == element) {
                if (change.wasAdded()) {
                    assert(contains.value == false)
                    contains.set(true)
                } else {
                    assert(contains.value == true)
                    contains.set(false)
                }
            }
        }
    }
    return contains
}

private inline fun <E, R> mapChangedElement(change: SetChangeListener.Change<out E>, transform: (E) -> R): R {
    if (change.wasAdded()) {
        val element = change.elementAdded
        return transform(element)
    } else if (change.wasRemoved()) {
        val element = change.elementRemoved
        return transform(element)
    }
    throw AssertionError()
}
