package wapuniverse.editor

import javafx.collections.FXCollections.observableArrayList
import javafx.collections.FXCollections.unmodifiableObservableList

class World(
        val retail: Retail,
        private val imageMetadataSupplier: ImageMetadataSupplier
) {
    private val planesMut = observableArrayList<Plane>()

    val planes = unmodifiableObservableList(planesMut)!!

    init {
        planesMut.addAll(
                Plane(this, "Back"),
                Plane(this, "Action"),
                Plane(this, "Front")
        )
    }

    internal fun expandImageSetId(imageSetId: String): String {
        return imageSetId.replace("LEVEL", "LEVEL1_IMAGES")
    }

    internal fun supplyMetadata(fqImageSetId: String, i: Int): ImageMetadata? {
        return imageMetadataSupplier.supplyMetadata(fqImageSetId, i)
    }
}
