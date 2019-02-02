package wapuniverse.editor

import javafx.collections.FXCollections.observableArrayList
import javafx.collections.FXCollections.unmodifiableObservableList

class World(
        val retail: Retail
) {
    private val planesMut = observableArrayList<Plane>()

    val planes = unmodifiableObservableList(planesMut)!!

    init {
        planesMut.addAll(
                Plane("Back"),
                Plane("Action"),
                Plane("Front")
        )
    }
}
