package wapuniverse.view.extensions

import javafx.beans.value.ObservableValue
import org.fxmisc.easybind.EasyBind
import org.fxmisc.easybind.Subscription

fun <T : Any, R> ObservableValue<T>.map(transform: (T) -> R): ObservableValue<R> =
        EasyBind.monadic(this).map { transform(it!!) }!!

@JvmName("mapNullable")
fun <T : Any, R> ObservableValue<T?>.map(transform: (T) -> R): ObservableValue<R> =
        EasyBind.monadic(this).map { transform(it!!) }!!

fun <T : Any, R> ObservableValue<T>.flatMap(transform: (T) -> ObservableValue<R>): ObservableValue<R> =
        EasyBind.monadic(this).flatMap { transform(it!!) }!!

@JvmName("flatMapNullable")
fun <T : Any, R> ObservableValue<T?>.flatMap(transform: (T) -> ObservableValue<R>): ObservableValue<R> =
        EasyBind.monadic(this).flatMap { transform(it!!) }!!

fun <T : Any> ObservableValue<T>.subscribe(function: (T) -> Unit): Subscription {
    return EasyBind.subscribe(this) { function(it!!)}
}

@JvmName("subscribeNullable")
fun <T : Any> ObservableValue<T?>.subscribe(function: (T?) -> Unit): Subscription {
    return EasyBind.subscribe(this) { function(it) }
}

fun <T : Any> ObservableValue<T>.forEach(function: (T) -> Unit) {
    EasyBind.subscribe(this) { function(it!!)}
}

@JvmName("forEachNullable")
fun <T : Any> ObservableValue<T?>.forEach(function: (T) -> Unit) {
    EasyBind.subscribe(this) { it?.let(function) }
}
