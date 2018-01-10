package wapuniverse.view.ext

import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.fxmisc.easybind.EasyBind

fun <E, R> ObservableList<E>.map(transform: (E) -> R): ObservableList<R> =
        EasyBind.map(this, transform)

fun <T> singletonObservableList(observableValue: ObservableValue<T>): ObservableList<T> {
    val observableList = FXCollections.observableArrayList<T>()
    if (observableValue.value != null) {
        observableList.add(observableValue.value)
    }
    observableValue.addListener { observable, oldValue, newValue ->
        if (observableList.isEmpty() && newValue != null) {
            observableList.add(newValue)
        } else if (observableList.size == 1) {
            if (newValue != null) {
                observableList[0] = newValue
            } else {
                observableList.clear()
            }
        } else throw AssertionError()
    }
    return observableList
}
