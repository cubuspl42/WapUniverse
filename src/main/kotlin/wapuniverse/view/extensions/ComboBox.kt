package wapuniverse.view.extensions

import javafx.beans.binding.Bindings
import javafx.beans.property.Property
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.control.ComboBox
import javafx.scene.control.ListCell
import wapuniverse.model.Plane
import wapuniverse.util.listBind

fun <T> ComboBox<T>.bind(property: Property<T>, enabled: ObservableBooleanValue) {
    valueProperty().bindBidirectional(property)
    disableProperty().bind(Bindings.not(enabled))
}

fun <T> ComboBox<T>.bind(
        values: ObservableValue<ObservableList<T>?>,
        value: ObservableValue<Property<T>?>,
        provideText: (item: T) -> String
) {
    listBind(items, values).let { properties[it] = it }

    value.subscribe { property ->
        property?.let { valueProperty().bindBidirectional(it) }
    }.also { properties[it] = it }

    setCellFactory {
        object : ListCell<T>() {
            override fun updateItem(item: T?, bln: Boolean) {
                super.updateItem(item, bln)
                item?.let { text = provideText(it) }
            }
        }
    }

    buttonCell = cellFactory.call(null)
}
