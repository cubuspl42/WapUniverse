package wapuniverse.util

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue

class OptionalProperty<T>(
        private val property: Property<T>
) : ObservableValue<T> by property {
    fun set(value: T) {
        check(property.value == null) { "Property is already set" }
        property.value = value
    }

    fun replace(value: T) {
        property.value = value
    }

    fun clear() {
        check(property.value != null)
        property.value = null
    }
}

fun <T> optionalProperty(value: T? = null) =
        OptionalProperty<T?>(objectProperty(value))
