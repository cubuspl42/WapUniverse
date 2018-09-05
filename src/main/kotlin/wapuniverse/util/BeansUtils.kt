package wapuniverse.util

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty

fun <T> objectProperty(value: T? = null) = SimpleObjectProperty<T>(value)

fun booleanProperty(value: Boolean) = SimpleBooleanProperty(value)
