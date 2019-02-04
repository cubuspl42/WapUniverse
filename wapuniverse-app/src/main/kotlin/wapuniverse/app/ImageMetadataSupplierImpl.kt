package wapuniverse.app

import javafx.scene.image.Image
import wapuniverse.editor.ImageMetadata
import wapuniverse.editor.ImageMetadataSupplier
import wapuniverse.geom.Size2i
import wapuniverse.rez.RezImageCache

class ImageMetadataSupplierImpl(
        private val rezImageCache: RezImageCache
) : ImageMetadataSupplier {
    override fun supplyMetadata(fqImageSetId: String, i: Int): ImageMetadata? {
        return rezImageCache.getImage(fqImageSetId, i)?.let {
            val size = it.image?.size ?: return null
            val offset = it.offset
            ImageMetadata(size, offset)
        }
    }
}

private val Image.size: Size2i
    get() = Size2i(width.toInt(), height.toInt())