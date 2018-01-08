package wapuniverse.rez

data class RezIndex(
        val imageSets: Map<String, RezImageSet>
) {
    fun findImageMetadata(fullyQualifiedImageSetId: String, i: Int): RezImageMetadata? =
            imageSets[fullyQualifiedImageSetId]?.findImageMetadata(i)
}
