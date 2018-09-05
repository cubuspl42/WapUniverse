package wapuniverse.view.extensions

import javafx.beans.binding.Bindings
import javafx.beans.property.Property
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.control.ComboBox

fun <T> ComboBox<T>.bind(property: Property<T>, enabled: ObservableBooleanValue) {
    valueProperty().bindBidirectional(property)
    disableProperty().bind(Bindings.not(enabled))
}
