package wapuniverse.view

import javafx.beans.value.ObservableObjectValue
import javafx.scene.image.Image
import wapuniverse.model.PlaneEditor
import wapuniverse.rez.RezImageProvider
import wapuniverse.view.util.loadFxml
import wapuniverse.view.util.observableValue

private const val FXML = "/view/WorldView.fxml"

fun worldView(planeEditor: PlaneEditor, rezImageProvider: RezImageProvider) =
        loadFxml(FXML) { WorldViewController(planeEditor, rezImageProvider) }

fun provideTileImage(
        rezImageProvider: RezImageProvider,
        imageDir: String,
        imageSet: String,
        i: Int
): ObservableObjectValue<Image?> {
    val prefix = imageDir.replace('\\', '_').removePrefix("_")
    val imageSetId = "${prefix}_$imageSet"
    return observableValue { rezImageProvider.provideImage(imageSetId, i.coerceAtLeast(0))?.image }
}
