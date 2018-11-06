package wapuniverse.model.util

import wapuniverse.geom.Vec2i

private const val chunkSize = 256

class Chunk(
        private val chunkMap: ChunkMap,
        private val chunkOffset: Vec2i
) {
    private val array = IntArray2D(chunkSize, chunkSize) { -1 }

    private var usedCells = 0

    fun getTile(globalTileOffset: Vec2i): Int {
        val localTileOffset = calculateLocalTileOffset(globalTileOffset)
        return getTileLocal(localTileOffset)
    }


    fun setTile(globalTileOffset: Vec2i, tileId: Int) {
        val localTileOffset = calculateLocalTileOffset(globalTileOffset)
        val prevTileId = getTileLocal(localTileOffset)
        setTileLocal(localTileOffset, tileId)

        usedCells += when {
            prevTileId == -1 && tileId != -1 -> 1
            prevTileId != -1 && tileId == -1 -> -1
            else -> 0
        }

        assert(usedCells >= 0)

        if (usedCells == 0) {
            chunkMap.removeChunk(chunkOffset)
        }
    }

    fun forEach(function: (tileOffset: Vec2i, tileId: Int) -> Unit) {
        for (i in 0 until chunkSize) {
            for (j in 0 until chunkSize) {
                val localTileOffset = Vec2i(j, i)
                val tileId = getTileLocal(localTileOffset)
                val tileOffset = calculateGlobalTileOffset(localTileOffset)
                function(tileOffset, tileId)
            }
        }
    }

    private fun getTileLocal(localTileOffset: Vec2i) =
            array.get(localTileOffset.y, localTileOffset.x)

    private fun setTileLocal(localTileOffset: Vec2i, tileId: Int) {
        array.set(localTileOffset.y, localTileOffset.x, tileId)
    }

    private fun calculateLocalTileOffset(globalTileOffset: Vec2i) =
            globalTileOffset - (chunkOffset * chunkSize)

    private fun calculateGlobalTileOffset(localTileOffset: Vec2i) =
            (chunkOffset * chunkSize) + localTileOffset
}

class ChunkMap {
    val chunks = hashMapOf<Vec2i, Chunk>()

    fun getTile(tileOffset: Vec2i): Int {
        val chunkOffset = calculateChunkOffset(tileOffset)
        val chunk = getChunk(chunkOffset)
        return chunk?.getTile(tileOffset) ?: -1
    }

    fun setTile(tileOffset: Vec2i, tileId: Int) {
        val chunkOffset = calculateChunkOffset(tileOffset)
        if (!chunkExists(chunkOffset) && tileId == -1) return
        val chunk = getOrPutChunk(chunkOffset)
        chunk.setTile(tileOffset, tileId)
    }

    internal fun removeChunk(chunkOffset: Vec2i) {
        chunks.remove(chunkOffset)
    }

    private fun getChunk(chunkOffset: Vec2i): Chunk? = chunks[chunkOffset]

    private fun getOrPutChunk(chunkOffset: Vec2i) =
            chunks.getOrPut(chunkOffset) { Chunk(this, chunkOffset) }

    private fun chunkExists(chunkOffset: Vec2i) = chunkOffset in chunks

    private fun calculateChunkOffset(tileOffset: Vec2i) =
            tileOffset / chunkSize

    fun forEach(function: (tileOffset: Vec2i, tileId: Int) -> Unit) {
        chunks.forEach { _, chunk ->
            chunk.forEach(function)
        }
    }
}
