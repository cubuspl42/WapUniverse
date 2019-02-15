package wapuniverse.editor

interface TileSetMetadataSupplier {
    fun listTiles(fqTileSetId: String): List<Int>?
}
