package wapuniverse.rez

interface RezIndex {
    fun findImageMetadata(fullyQualifiedImageSetId: String, i: Int): RezImageMetadata?

}