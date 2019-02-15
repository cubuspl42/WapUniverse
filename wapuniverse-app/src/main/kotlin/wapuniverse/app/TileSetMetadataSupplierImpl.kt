package wapuniverse.app

import wapuniverse.editor.TileSetMetadataSupplier
import wapuniverse.rez.RezImageCache

class TileSetMetadataSupplierImpl(
        private val rezImageCache: RezImageCache
) : TileSetMetadataSupplier {
    override fun listTiles(fqTileSetId: String): List<Int>? {
        val imageSet = rezImageCache.imageSets[fqTileSetId] ?: return null
        return imageSet.frames.keys.toList()
    }
}