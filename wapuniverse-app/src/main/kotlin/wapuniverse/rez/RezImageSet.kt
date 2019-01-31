package wapuniverse.rez

data class RezImageSet(
        val images: Map<String, RezImage>,
        val frames: Map<Int, String>
) {
    fun findImage(frameIndex: Int): RezImage? {
        return frames[frameIndex]?.let { frameName ->
            return images[frameName]!!
        }
    }
}
