package wapuniverse.view.ext

import javafx.beans.binding.Bindings
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableValue
import org.fxmisc.easybind.EasyBind
import org.fxmisc.easybind.monadic.MonadicBinding

fun <T, R> ObservableValue<T>.map(transform: (T) -> R): MonadicBinding<R> =
        EasyBind.map(this, transform)!!

fun ObservableValue<Boolean>.asObservableBooleanValue(): ObservableBooleanValue =
        Bindings.selectBoolean(this)
