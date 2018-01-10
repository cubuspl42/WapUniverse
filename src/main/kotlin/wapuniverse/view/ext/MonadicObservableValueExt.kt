package wapuniverse.view.ext

import org.fxmisc.easybind.monadic.MonadicObservableValue
import wapuniverse.view.Controller

fun <T> MonadicObservableValue<T>.attachController(function: (T) -> Controller) {
    var controller = if (this.isPresent) function(this.value!!) else null
    this.addListener { observable, oldValue, newValue ->
        controller?.uninit()
        if (newValue != null) {
            controller = function(newValue)
        }
    }
}