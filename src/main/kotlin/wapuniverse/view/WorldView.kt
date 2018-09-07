package wapuniverse.view

import javafx.beans.value.ObservableObjectValue
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import wapuniverse.model.Plane
import wapuniverse.rez.RezImageProvider
import wapuniverse.view.extensions.toObservableList
import wapuniverse.view.util.group
import wapuniverse.view.util.observableValue

fun worldView(plane: Plane, rezImageProvider: RezImageProvider): Node {
    val tileNodes = plane.tiles.toObservableList { index, tileId ->
        ImageView().apply {
            x = index.x * 64.0
            y = index.y * 64.0
            imageProperty().bind(provideTileImage(rezImageProvider, plane.world.imageDir, plane.imageSet, tileId))
        }
    }
    return group(tileNodes)
}

fun provideTileImage(
        rezImageProvider: RezImageProvider,
        imageDir: String,
        imageSet: String,
        i: Int
): ObservableObjectValue<Image?> {
    val prefix = imageDir.replace('\\', '_').removePrefix("_")
    val imageSetId = "${prefix}_$imageSet"
    return observableValue { rezImageProvider.provideImage(imageSetId, i)?.image }
}