package wapuniverse.extensions

import javafx.collections.ObservableList
import org.fxmisc.easybind.EasyBind

fun <E, R> ObservableList<E>.map(function: (E) -> R): ObservableList<R> {
    return EasyBind.map(this) { function(it)!! }
}
