package wapuniverse.model

import io.github.jwap32.v1.Wwd
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.FXCollections.unmodifiableObservableList
import javafx.collections.ObservableList
import wapuniverse.model.util.UnmodifiableCollection

class World(
        wwd: Wwd,
        private val editor: Editor
) {
    val imageDir = wwd.header.imageDir

    val imageSet1 = wwd.header.imageSet1

    val prefixMap = mapOf(
            wwd.header.prefix1 to wwd.header.imageSet1,
            wwd.header.prefix2 to wwd.header.imageSet2,
            wwd.header.prefix3 to wwd.header.imageSet3,
            wwd.header.prefix4 to wwd.header.imageSet4
    )

    @UnmodifiableCollection
    val planes: ObservableList<Plane>

    private val mPlanes: ObservableList<Plane> = createPlanes(wwd)

    private fun createPlanes(wwd: Wwd) =
            observableArrayList<Plane>(wwd.planes.map { Plane(this, it, editor) })

    init {
        planes = unmodifiableObservableList(mPlanes)!!
    }
}
