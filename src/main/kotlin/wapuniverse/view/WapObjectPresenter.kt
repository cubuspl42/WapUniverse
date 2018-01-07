package wapuniverse.view

import javafx.scene.image.ImageView
import wapuniverse.model.WapObject
import wapuniverse.rez.RezImageProvider
import wapuniverse.view.util.observableValue

class WapObjectPresenter(
        private val rezImageProvider: RezImageProvider
) {
    fun presentObjectImageView(wapObject: WapObject) =
            ImageView().apply {
                xProperty().bind(wapObject.x)
                imageProperty().bind(provideImage(wapObject))
            }

    private fun provideImage(wapObject: WapObject) = observableValue {
        rezImageProvider.provideImage(
                wapObject.imageSet.value, -1
        )?.image
    }
}
