package wapuniverse.util

import javafx.geometry.HPos
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import org.reactfx.value.Var
import wapuniverse.editor.extensions.map

fun stringTextField(strVar: Var<String>) =
        TextField().apply {
            textProperty().value = strVar.value.toString()
            strVar.bind(textProperty())
        }

fun intTextField(intVar: Var<Int>) =
        TextField().apply {
            textProperty().value = intVar.value.toString()
            intVar.bind(textProperty().map { it.toIntOrNull() ?: 0 })
        }

fun label(labelText: String) =
        Label("$labelText:").apply {
            GridPane.setHalignment(this, HPos.RIGHT)
        }