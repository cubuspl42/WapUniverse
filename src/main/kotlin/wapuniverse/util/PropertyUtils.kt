package wapuniverse.util

import javafx.beans.property.BooleanProperty
import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty

fun <T> objectProperty(value: T? = null): Property<T> = SimpleObjectProperty<T>(value)

fun booleanProperty(value: Boolean): BooleanProperty = SimpleBooleanProperty(value)
