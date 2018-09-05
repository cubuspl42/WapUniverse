package wapuniverse.view.extensions

import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property
import javafx.beans.value.ObservableValue

fun <T> ObjectProperty<T>.bindBidirectional(propertyObservable: ObservableValue<Property<T>>) {
    propertyObservable.value?.let { bindBidirectional(it) }
    propertyObservable.addListener { _, _, property ->
        property?.let { bindBidirectional(it) }
    }
}
