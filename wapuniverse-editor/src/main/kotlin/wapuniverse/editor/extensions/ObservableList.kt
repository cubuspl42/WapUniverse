package wapuniverse.editor.extensions

import javafx.collections.ObservableList
import net.corda.client.jfx.utils.map

fun <E, R> ObservableList<E>.map(function: (E) -> R) =
        this.map { function(it) }
