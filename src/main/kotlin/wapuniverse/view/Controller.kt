package wapuniverse.view

import org.reactfx.EventStream
import wapuniverse.model.util.Disposable
import wapuniverse.model.util.disposable

open class Controller(parent: Disposable) : Disposable(parent) {
    protected fun <T> subscribe(stream: EventStream<T>, function: (T) -> Unit) {
        stream.subscribe(function).let { addChild(disposable { it.unsubscribe() }) }
    }
}
