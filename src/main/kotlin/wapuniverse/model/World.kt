package wapuniverse.model

import io.github.jwap32.v1.Wwd
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.FXCollections.unmodifiableObservableList
import javafx.collections.ObservableList
import wapuniverse.model.util.UnmodifiableCollection

class World(wwd: Wwd) {
    @UnmodifiableCollection
    val planes: ObservableList<Plane>

    private val mPlanes: ObservableList<Plane> = createPlanes(wwd)

    private fun createPlanes(wwd: Wwd) =
            observableArrayList<Plane>(wwd.planes.map { Plane(it) })

    init {
        planes = unmodifiableObservableList(mPlanes)!!
    }
}
