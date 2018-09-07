package wapuniverse.rez

data class RezImageSet(
        var images: Map<String, RezImageMetadata>,
        val frames: Map<Int, String>
) {
    fun findImageMetadata(frameIndex: Int): RezImageMetadata? {
        return frames[frameIndex]?.let { frameName ->
            return images[frameName]!!
        }
    }
}
