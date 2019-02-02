package wapuniverse.extensions

import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import org.fxmisc.easybind.EasyBind
import wapuniverse.editor.extensions.subscribe

fun <E, R> ObservableList<E>.map(function: (E) -> R): ObservableList<R> {
    return EasyBind.map(this) { function(it)!! }
}

fun <E : Any> listBind(list: ObservableList<E?>, child: ObservableValue<E?>) {
    list.setOneNullable(child.value)
    child.subscribe {
        list.setOneNullable(it)
    }
}

private fun <E> ObservableList<E>.setOneNullable(value: E) {
    value?.let { setAll(it) } ?: clear()
}
