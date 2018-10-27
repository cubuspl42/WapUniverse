package wapuniverse.view.extensions

import javafx.collections.FXCollections.observableArrayList
import javafx.collections.MapChangeListener
import javafx.collections.ObservableList
import javafx.collections.ObservableMap

fun <K, V, R> ObservableMap<K, V>.toObservableList(transform: (K, V) -> R): ObservableList<R> {
    val transformMap = mutableMapOf<K, R>()
    val list = observableArrayList<R>()!!

    fun addValue(key: K, value: V) {
        val value2 = transform(key, value)
        transformMap[key] = value2
        list.add(value2)
    }

    forEach { key, value -> addValue(key, value) }

    addListener { change: MapChangeListener.Change<out K, out V> ->
        val key = change.key
        if (change.wasRemoved()) {
            val value = transformMap.remove(key)
            list.remove(value)
        }
        if (change.wasAdded()) {
            addValue(key, change.valueAdded)
        }
    }

    return list
}
