package wapuniverse.view.extensions

import javafx.beans.value.ObservableValue
import org.fxmisc.easybind.EasyBind
import org.fxmisc.easybind.Subscription
import org.fxmisc.easybind.monadic.MonadicBinding

fun <T: Any, R> ObservableValue<T>.map(transform: (T) -> R): MonadicBinding<R> =
        EasyBind.monadic(this).map(transform)!!

@JvmName("mapNullable")
fun <T: Any, R> ObservableValue<T?>.map(transform: (T) -> R): MonadicBinding<R> =
        EasyBind.monadic(this).map { transform(it!!) }!!

fun <T : Any, R> ObservableValue<T>.flatMap(transform: (T) -> ObservableValue<R>) =
        EasyBind.monadic(this).flatMap { transform(it!!) }!!

@JvmName("flatMapNullable")
fun <T : Any, R> ObservableValue<T?>.flatMap(transform: (T) -> ObservableValue<R>) =
        EasyBind.monadic(this).flatMap { transform(it!!) }!!

fun <T> ObservableValue<T>.subscribe(function: (T) -> Unit): Subscription {
    return EasyBind.subscribe(this, function)
}
