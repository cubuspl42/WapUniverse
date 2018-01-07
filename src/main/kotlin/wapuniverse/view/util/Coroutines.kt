package wapuniverse.view.util

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableObjectValue
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.startCoroutine

private class ObservableValueContinuation<T>(
        override val context: CoroutineContext,
        private val property: SimpleObjectProperty<T>
) : Continuation<T> {
    override fun resume(value: T) {
        property.set(value)
    }

    override fun resumeWithException(exception: Throwable) {
    }
}

fun <T> observableValue(
        context: CoroutineContext = JavaFx,
        block: suspend () -> T
): ObservableObjectValue<T> {
    val property = SimpleObjectProperty<T>()
    block.startCoroutine(ObservableValueContinuation(context, property))
    return property
}
