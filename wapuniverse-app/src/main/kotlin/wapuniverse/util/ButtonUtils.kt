package wapuniverse.util

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Button
import org.reactfx.value.Val

fun button(text: String, callback: () -> Unit) =
        Button(text).apply {
            setOnAction { callback() }
        }


fun button(text: String, callback: Val<() -> Unit>) =
        Button(text).apply {
            onActionProperty().bind(callback.map { f -> EventHandler<ActionEvent> { f() } })
        }
