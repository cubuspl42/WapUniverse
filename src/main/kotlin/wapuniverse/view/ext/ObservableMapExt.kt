package wapuniverse.view.ext

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.MapChangeListener
import javafx.collections.ObservableMap

fun <K, V> ObservableMap<K, V>.toObservableValue(): ObservableValue<Map<K, V>> {
    val property = SimpleObjectProperty<Map<K, V>>(this)
    this.addListener { change: MapChangeListener.Change<out K, out V> ->
        property.set(this)
    }
    return property
}
