package wapuniverse.view.ext

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
            if (change.wasAdded()) {
                val element = change.elementAdded
                mapElement(element)
            } else if (change.wasRemoved()) {
                val element = change.elementRemoved
                unmapElement(element)
            }
            throw AssertionError()
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
