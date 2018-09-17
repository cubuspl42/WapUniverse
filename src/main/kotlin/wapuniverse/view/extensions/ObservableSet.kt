package wapuniverse.view.extensions

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.ObservableSet
import javafx.collections.SetChangeListener

fun <T, R> ObservableSet<T>.toObservableList(transform: (T) -> R): ObservableList<R> {
    val transformMap = mutableMapOf<T, R>()
    val list = FXCollections.observableArrayList<R>()!!

    fun addValue(value: T) {
        val value2 = transform(value)
        list.add(value2)
    }

    forEach { key -> addValue(key) }

    addListener { change: SetChangeListener.Change<out T> ->
        if (change.wasRemoved()) {
            val value = transformMap[change.elementRemoved]
            list.remove(value)
        }
        if (change.wasAdded()) {
            addValue(change.elementAdded)
        }
    }

    return list
}
