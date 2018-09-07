package wapuniverse.rez

interface RezImageProvider {
    suspend fun provideImage(fullyQualifiedImageSetId: String, i: Int): RezImage?
}
