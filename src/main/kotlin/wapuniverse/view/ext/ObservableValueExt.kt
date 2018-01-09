package wapuniverse.view.ext

import javafx.beans.binding.Bindings
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableValue
import javafx.scene.Group
import javafx.scene.Node
import org.fxmisc.easybind.EasyBind
import org.fxmisc.easybind.monadic.MonadicBinding

fun <T, R> ObservableValue<T>.map(transform: (T) -> R): MonadicBinding<R> =
        EasyBind.map(this, transform)!!

fun ObservableValue<Boolean>.asObservableBooleanValue(): ObservableBooleanValue =
        Bindings.selectBoolean(this)

fun <T> ObservableValue<T>.mapTo(destination: Group, transform: (value: T) -> Node): Group {
    var node: Node? = null
    this.addListener { observable, oldValue, newValue ->
        if (node != null) {
            destination.children.remove(node)
        }
        if (newValue != null) {
            node = transform(newValue)
            destination.children.add(node)
        }
    }
    return destination
}
