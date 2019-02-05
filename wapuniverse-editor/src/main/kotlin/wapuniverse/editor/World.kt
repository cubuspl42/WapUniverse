package wapuniverse.editor

import io.github.jwap32.v1.Wwd
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.FXCollections.unmodifiableObservableList

class World(
        wwd: Wwd,
        private val imageMetadataSupplier: ImageMetadataSupplier
) {
    private val planesMut = observableArrayList<Plane>()

    val planes = unmodifiableObservableList(planesMut)!!

    init {
        wwd.planes.forEach { wwdPlane ->
            planesMut.add(Plane(this, wwdPlane))
        }
    }

    internal fun expandImageSetId(imageSetId: String): String {
        return imageSetId.replace("LEVEL", "LEVEL1_IMAGES")
    }

    internal fun supplyMetadata(fqImageSetId: String, i: Int): ImageMetadata? {
        return imageMetadataSupplier.supplyMetadata(fqImageSetId, i)
    }
}
