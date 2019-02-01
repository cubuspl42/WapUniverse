package wapuniverse.editor

import javafx.collections.FXCollections.observableSet
import javafx.collections.FXCollections.unmodifiableObservableSet

class Plane(val name: String) {
    private val objectsMut = observableSet<WapObject>()

    val objects = unmodifiableObservableSet(objectsMut)
}
