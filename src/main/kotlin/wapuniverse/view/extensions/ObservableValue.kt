package wapuniverse.view.extensions

import javafx.beans.value.ObservableValue
import org.fxmisc.easybind.EasyBind
import org.fxmisc.easybind.Subscription
import wapuniverse.model.util.Disposable

fun <T : Any, R> ObservableValue<T>.map(transform: (T) -> R): ObservableValue<R> =
        EasyBind.monadic(this).map { transform(it!!) }!!

@JvmName("mapNullable")
fun <T : Any, R> ObservableValue<T?>.map(transform: (T) -> R): ObservableValue<R?> =
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
    EasyBind.subscribe(this) { it?.let(function)}
}

@JvmName("forEachNullable")
fun <T : Any> ObservableValue<T?>.forEach(function: (T) -> Unit) {
    EasyBind.subscribe(this) { it?.let(function) }
}

fun <T : Any, R : Disposable> ObservableValue<T>.transform(function: (T) -> R): ObservableValue<R> {
    return this.map { function(it!!) }.also {
        it.addListener { _, oldValue, _ ->
            oldValue?.dispose()
        }
    }
}

@JvmName("transformNullable")
fun <T : Any, R : Disposable> ObservableValue<T?>.transform(function: (T) -> R): ObservableValue<R?> {
    return this.map { function(it) }.also {
        it.addListener { _, oldValue, _ ->
            oldValue?.dispose()
        }
    }
}
