package wapuniverse.view.util

import javafx.beans.binding.Bindings.not
import javafx.beans.binding.BooleanExpression.booleanExpression
import javafx.scene.control.Button
import javafx.scene.control.MenuItem
import wapuniverse.model.Action

fun bind(menuItem: MenuItem, action: Action) {
    menuItem.disableProperty().bind(not(booleanExpression(action.enabled)))
    menuItem.setOnAction { action.execute() }
}

fun bind(button: Button, action: Action) {
    button.disableProperty().bind(not(booleanExpression(action.enabled)))
    button.setOnAction { action.execute() }
}

fun bind(menuItem: MenuItem, button: Button, action: Action) {
    bind(menuItem, action)
    bind(button, action)
}
