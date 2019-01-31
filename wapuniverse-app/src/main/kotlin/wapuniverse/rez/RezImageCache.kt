package wapuniverse.rez

data class RezImageCache(
        val imageSets: Map<String, RezImageSet>
) {
    fun getImage(fullyQualifiedImageSetId: String, i: Int): RezImage? =
            imageSets[fullyQualifiedImageSetId]?.findImage(i)
}
