package io.github.jwap32.v1

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets.US_ASCII


/**
 * TODO:
 * Checksums
 * Tile loading / dumping (mutable matrix?)
 */

val WAP_WWD_HEADER_SIZE = 1524
val WAP_WWD_PLANE_DESCRIPTION_SIZE = 160
val WAP_WWD_OBJECT_DESCRIPTION_SIZE = 284

val WAP_TILE_TYPE_SINGLE = 1
val WAP_TILE_TYPE_DOUBLE = 2

data class WwdRect(
        var left: Int = 0,
        var top: Int = 0,
        var right: Int = 0,
        var bottom: Int = 0
)

val NULL_BYTE = 0.toByte()

// TODO: Use LittleEndianDataInputStream instead?
class WwdInputStream(private val inputStream: InputStream) {
    fun expectInt(expectedValue: Int) {
        val value = readInt()
        if (value != expectedValue) throw IllegalStateException("$inputStream: $value != $expectedValue")
    }

    fun readInt(): Int {
        val bytes = ByteArray(4, { 0 })
        inputStream.read(bytes, 0, bytes.size)
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).int
    }

    fun readFixedString(bufferSize: Int): String {
        val bytes = ByteArray(bufferSize, { 0 })
        inputStream.read(bytes, 0, bytes.size)
        val nullIdx: Int = bytes.indexOfFirst { it == NULL_BYTE }
        val last = if (nullIdx > 0) nullIdx - 1 else bufferSize - 1
        return bytes.sliceArray(0..last).toString(US_ASCII)
    }

    fun readStaticString(bufferSize: Int): String {
        val s = readFixedString(bufferSize - 1)
        inputStream.read() // skip '\0'
        return s
    }

    fun skip(n: Long) {
        inputStream.skip(n)
    }

    fun use(function: (WwdInputStream) -> Unit) {
        return inputStream.use {
            function(this)
        }
    }

    fun readNullTerminatedString(): String {
        val os = ByteArrayOutputStream()
        val buffer = ByteArray(1, { 0 })
        while (true) {
            inputStream.read(buffer)
            if (buffer[0] <= 0) break
            os.write(buffer)
        }
        return os.toByteArray().toString(US_ASCII)
    }

    fun readRect(): WwdRect {
        return WwdRect(readInt(), readInt(), readInt(), readInt())
    }
}

fun subStream(wwdBuffer: ByteArray, offset: Int): WwdInputStream {
    return WwdInputStream(ByteArrayInputStream(wwdBuffer, offset, wwdBuffer.size - offset))
}

data class Wwd(
        var header: WwdHeader = WwdHeader(),
        var planes: MutableList<WwdPlane> = mutableListOf(),
        var tileDescriptions: MutableList<WwdTileDescription> = mutableListOf()
) {
    val mainPlane: WwdPlane?
        get() = planes.firstOrNull { it.flags.mainPlane }

    fun clone(): Wwd {
        return Wwd(
                header.clone(),
                planes.map { it.clone() }.toMutableList(),
                tileDescriptions.map { it.clone() }.toMutableList()
        )
    }
}

class WwdHeaderFlags(var dword: Int = 0) {
    var useZCoords: Boolean by flagProperty(this, WwdHeaderFlags::dword, 0)
    var compress: Boolean by flagProperty(this, WwdHeaderFlags::dword, 1)

    fun copy() = WwdHeaderFlags(dword)
}

data class WwdHeader(
        var flags: WwdHeaderFlags = WwdHeaderFlags(),
        var levelName: String = "",
        var author: String = "",
        var birth: String = "",
        var rezFile: String = "",
        var imageDir: String = "",
        var palRez: String = "",
        var startX: Int = 0,
        var startY: Int = 0,
        var planeCount: Int = 0,
        var mainBlockOffset: Int = 0,
        var tileDescriptionsOffset: Int = 0,
        var decompressedMainBlockSize: Int = 0,
        var checksum: Int = 0,
        var launchApp: String = "",
        var imageSet1: String = "",
        var imageSet2: String = "",
        var imageSet3: String = "",
        var imageSet4: String = "",
        var prefix1: String = "",
        var prefix2: String = "",
        var prefix3: String = "",
        var prefix4: String = ""
) {
    companion object {
        val levelNameBufferSize = 64
        val authorBufferSize = 64
        val birthBufferSize = 64
        val rezFileBufferSize = 256
        val imageDirBufferSize = 128
        val palRezBufferSize = 128
        val launchAppBufferSize = 128
        val imageSetBufferSize = 128
        val prefixBufferSize = 32
    }

    fun clone(): WwdHeader = copy(flags = flags.copy())
}

class WwdPlaneFlags(var dword: Int = 0) {
    var mainPlane: Boolean by flagProperty(this, WwdPlaneFlags::dword, 0)
    var noDraw: Boolean by flagProperty(this, WwdPlaneFlags::dword, 1)
    var xWrapping: Boolean by flagProperty(this, WwdPlaneFlags::dword, 2)
    var yWrapping: Boolean by flagProperty(this, WwdPlaneFlags::dword, 3)
    var autoTileSize: Boolean by flagProperty(this, WwdPlaneFlags::dword, 4)

    fun copy() = WwdPlaneFlags(dword)
}

data class WwdPlane(
        var flags: WwdPlaneFlags = WwdPlaneFlags(),
        var name: String = "",
        var tileWidth: Int = 0, /* tile's width in pixels */
        var tileHeight: Int = 0, /* tile's height in pixels */
        var movementXPercent: Int = 0,
        var movementYPercent: Int = 0,
        var fillColor: Int = 0,
        var imageSetCount: Int = 0, // *
        var objectCount: Int = 0, // *
        var tilesOffset: Int = 0, // *
        var imageSetsOffset: Int = 0, // *
        var objectsOffset: Int = 0, // *
        var zCoord: Int = 0,
        var tilesWide: Int = 0,
        var tilesHigh: Int = 0,
        var tiles: IntArray = IntArray(tilesWide * tilesHigh) { -1 },
        var imageSets: MutableList<String> = mutableListOf(),
        var objects: MutableList<WwdObject> = mutableListOf()
) {
    companion object {
        val nameBufferSize = 64
    }

    fun clone() = copy(
            flags = flags.copy(),
            tiles = tiles.clone(),
            imageSets = imageSets.toMutableList(),
            objects = objects.map { it.clone() }.toMutableList()
    )

    fun getTile(i: Int, j: Int): Int {
        val idx = i * tilesWide + j
        val t = tiles[idx]
        return t
    }

    fun setTile(i: Int, j: Int, t: Int) {
        val idx = i * tilesWide + j
        tiles[idx] = t
    }
}

class WwdObjectDynamicFlags(var dword: Int = 0) {
    // TODO

    fun copy() = WwdObjectDynamicFlags(dword)
}

class WwdObjectDrawFlags(var dword: Int = 0) {
    var noDraw: Boolean by flagProperty(this, WwdObjectDrawFlags::dword, 0)
    var mirror: Boolean by flagProperty(this, WwdObjectDrawFlags::dword, 1)
    var invert: Boolean by flagProperty(this, WwdObjectDrawFlags::dword, 2)
    var flash: Boolean by flagProperty(this, WwdObjectDrawFlags::dword, 3)

    fun copy() = WwdObjectDrawFlags(dword)
}

class WwdObjectUserFlags(var dword: Int = 0) {
    // TODO

    fun copy() = WwdObjectUserFlags(dword)
}

class WwdObjectAddFlags(var dword: Int = 0) {
    var difficult: Boolean by flagProperty(this, WwdObjectAddFlags::dword, 0)
    var eyeCandy: Boolean by flagProperty(this, WwdObjectAddFlags::dword, 1)
    var highDetail: Boolean by flagProperty(this, WwdObjectAddFlags::dword, 2)
    var multiplayer: Boolean by flagProperty(this, WwdObjectAddFlags::dword, 3)
    var extraMemory: Boolean by flagProperty(this, WwdObjectAddFlags::dword, 4)
    var fastCpu: Boolean by flagProperty(this, WwdObjectAddFlags::dword, 5)

    fun copy() = WwdObjectAddFlags(dword)
}


data class WwdObject(
        var id: Int = 0,
        var x: Int = 0,
        var y: Int = 0,
        var z: Int = 0,
        var i: Int = 0,
        var addFlags: WwdObjectAddFlags = WwdObjectAddFlags(), /* WAP_OBJECT_ADD_FLAG_ flags */
        var dynamicFlags: WwdObjectDynamicFlags = WwdObjectDynamicFlags(), /* WAP_OBJECT_DYNAMIC_FLAG_ flags */
        var drawFlags: WwdObjectDrawFlags = WwdObjectDrawFlags(), /* WAP_OBJECT_DRAW_FLAG_ flags */
        var userFlags: WwdObjectUserFlags = WwdObjectUserFlags(), /* WAP_OBJECT_USER_FLAG_ flags */
        var score: Int = 0,
        var points: Int = 0,
        var powerup: Int = 0,
        var damage: Int = 0,
        var smarts: Int = 0,
        var health: Int = 0,
        var moveRect: WwdRect = WwdRect(),
        var hitRect: WwdRect = WwdRect(),
        var attackRect: WwdRect = WwdRect(),
        var clipRect: WwdRect = WwdRect(),
        var userRect1: WwdRect = WwdRect(),
        var userRect2: WwdRect = WwdRect(),
        var userValue1: Int = 0,
        var userValue2: Int = 0,
        var userValue3: Int = 0,
        var userValue4: Int = 0,
        var userValue5: Int = 0,
        var userValue6: Int = 0,
        var userValue7: Int = 0,
        var userValue8: Int = 0,
        var xMin: Int = 0,
        var yMin: Int = 0,
        var xMax: Int = 0,
        var yMax: Int = 0,
        var speedX: Int = 0,
        var speedY: Int = 0,
        var xTweak: Int = 0,
        var yTweak: Int = 0,
        var counter: Int = 0,
        var speed: Int = 0,
        var width: Int = 0,
        var height: Int = 0,
        var direction: Int = 0,
        var faceDir: Int = 0,
        var timeDelay: Int = 0,
        var frameDelay: Int = 0,
        var objectType: Int = 0, /* WAP_OBJECT_TYPE_ single value */
        var hitTypeFlags: Int = 0, /* WAP_OBJECT_TYPE_ flags */
        var xMoveRes: Int = 0,
        var yMoveRes: Int = 0,
        var name: String = "",
        var logic: String = "",
        var imageSet: String = "",
        var animation: String = ""
) {
    fun clone(): WwdObject {
        return copy(
                addFlags = addFlags.copy(),
                dynamicFlags = dynamicFlags.copy(),
                drawFlags = drawFlags.copy(),
                userFlags = userFlags.copy(),
                moveRect = moveRect.copy(),
                hitRect = hitRect.copy(),
                attackRect = attackRect.copy(),
                clipRect = clipRect.copy(),
                userRect1 = userRect1.copy(),
                userRect2 = userRect2.copy()
        )
    }
}

data class WwdTileDescription(
        var type: Int = 0, /* WAP_TILE_TYPE_ single value */
        var width: Int = 0, /* in pixels */
        var height: Int = 0, /* in pixels */
        var insideAttrib: Int = 0, /* WAP_TILE_ATTRIBUTE_ */
        /* outside_attrib and rect only if type == WAP_TILE_TYPE_DOUBLE */
        var outsideAttr: Int = 0, /* WAP_TILE_ATTRIBUTE_ */
        var rect: WwdRect = WwdRect()
) {
    fun clone(): WwdTileDescription {
        return copy(rect = rect.copy())
    }
}
