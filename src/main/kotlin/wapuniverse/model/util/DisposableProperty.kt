package wapuniverse.model.util

import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue

class DisposableProperty<T : Disposable?>(
        value: T,
        private val property: Property<T>
) : ObservableValue<T> by property {
    init {
        value?.let { set(it) }
    }

    fun set(value: T) {
        property.value = value
        value?.addDisposeListener {
            property.value = null
        }
    }
}
fun <T: Disposable?> disposableProperty(value: T): DisposableProperty<T> =
        DisposableProperty(value, SimpleObjectProperty(value))
