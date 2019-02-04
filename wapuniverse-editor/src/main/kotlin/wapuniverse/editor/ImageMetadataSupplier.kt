package wapuniverse.editor

interface ImageMetadataSupplier {
    fun supplyMetadata(fqImageSetId: String, i: Int): ImageMetadata?
}
