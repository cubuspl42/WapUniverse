package wapuniverse.editor.extensions

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ObservableList
import org.fxmisc.easybind.EasyBind
import org.fxmisc.easybind.EasyBind.subscribe
import org.fxmisc.easybind.Subscription

fun <T : Any, R> ObservableValue<T>.map(transform: (T) -> R): ObservableValue<R> =
        EasyBind.monadic(this).map { transform(it!!) }!!

@JvmName("mapNullable")
fun <T : Any, R> ObservableValue<T?>.map(transform: (T) -> R): ObservableValue<R?> =
        EasyBind.monadic(this).map { transform(it!!) }!!

fun <T : Any, R> ObservableValue<T>.flatMapOv(transform: (T) -> ObservableValue<R>): ObservableValue<R> =
        EasyBind.monadic(this).flatMap { transform(it!!) }!!

fun <T : Any, R> ObservableValue<T>.flatMap(transform: (T) -> ObservableValue<R>): ObservableValue<R> =
        this.flatMapOv(transform)

@JvmName("flatMapNullable")
fun <T : Any, R> ObservableValue<T?>.flatMap(transform: (T) -> ObservableValue<R>): ObservableValue<R?> =
        EasyBind.monadic(this).flatMap { transform(it!!) }

fun <T : Any> ObservableValue<T>.subscribe(function: (T) -> Unit): Subscription {
    return subscribe(this) { function(it!!) }
}

@JvmName("subscribeNullable")
fun <T : Any> ObservableValue<T?>.subscribe(function: (T?) -> Unit): Subscription {
    return subscribe(this) { function(it) }
}

fun <T : Any> ObservableValue<T>.forEach(function: (T) -> Unit) {
    subscribe(this) { it?.let(function) }
}

@JvmName("forEachNullable")
fun <T : Any> ObservableValue<T?>.forEach(function: (T) -> Unit) {
    subscribe(this) { it?.let(function) }
}

fun <T: Any> ObservableValue<T?>.orElse(value: T): ObservableValue<T> =
        EasyBind.monadic(this).orElse(value).map { it!! }

@JvmName("flatMapObservableList")
fun <T : Any, R> ObservableValue<T>.flatMapOl(transform: (T) -> ObservableList<R>): ObservableList<R> {
    val list = observableArrayList<R>()
    this.value?.let { list.addAll(transform(it)) }
    subscribe(this) { value ->
        value?.let { list.setAll(transform(it)) } ?: list.clear()
    }
    return list
}

@JvmName("flatMapProperty")
fun <T : Any, R> ObservableValue<T>.flatMapProp(transform: (T) -> Property<R>): Property<R> {
    val propertyVal = this.map(transform)
    val observableValue = propertyVal.flatMapOv { it }
    return object : Property<R>, ObservableValue<R> by observableValue {
        override fun setValue(value: R) {
            propertyVal.value.value = value
        }

        override fun getName(): String {
            TODO("not implemented")
        }

        override fun bindBidirectional(other: Property<R>?) {
            TODO("not implemented")
        }

        override fun getBean(): Any {
            TODO("not implemented")
        }

        override fun unbind() {
            TODO("not implemented")
        }

        override fun bind(observable: ObservableValue<out R>?) {
            TODO("not implemented")
        }

        override fun isBound(): Boolean {
            TODO("not implemented")
        }

        override fun unbindBidirectional(other: Property<R>?) {
            TODO("not implemented")
        }

    }
}
