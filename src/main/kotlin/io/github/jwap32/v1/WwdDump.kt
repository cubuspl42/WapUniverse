package io.github.jwap32.v1

import com.google.common.io.LittleEndianDataOutputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets.US_ASCII

private val tileWidth = 64
private val SIZEOF_INT = 4
private val SIZEOF_NULL_BYTE = 1

class WwdOutputStream(outputStream: OutputStream) {
    val dataOutputStream = LittleEndianDataOutputStream(outputStream)

    private fun writeNullByte() {
        dataOutputStream.write(NULL_BYTE.toInt())
    }

    fun writeInt(i: Int) = dataOutputStream.writeInt(i)

    fun writeBuffer(b: ByteArray) = dataOutputStream.write(b)

    fun writeFixedString(s: String) {
        val bytes = s.toByteArray(US_ASCII)
        dataOutputStream.write(bytes)
    }

    fun writeStaticString(s: String, bufferSize: Int) {
        val bytes = s.toByteArray(US_ASCII) + ByteArray(bufferSize, { NULL_BYTE })
        dataOutputStream.write(bytes.sliceArray(0..bufferSize - 2))
        writeNullByte()
    }

    fun writeNullTerminatedString(s: String) {
        writeFixedString(s)
        writeNullByte()
    }

    fun writeRect(r: WwdRect) {
        writeInt(r.left)
        writeInt(r.top)
        writeInt(r.right)
        writeInt(r.bottom)
    }
}


private fun calculateOffsets(wwd: Wwd) {
    val planes = wwd.planes

    var offset = WAP_WWD_HEADER_SIZE;
    wwd.header.mainBlockOffset = offset;

    offset += wwd.planes.size * WAP_WWD_PLANE_DESCRIPTION_SIZE
    wwd.header.tileDescriptionsOffset = offset;

    for (p in planes) {
        p.tilesOffset = offset
        offset += p.tilesWide * p.tilesHigh * SIZEOF_INT // TODO: -> tiles.size ? + other similar stuff
        // TODO: Clone-on-dump + unserscores for "private" fields
    }

    for (p in planes) {
        p.imageSetsOffset = offset
        for (imageSet in p.imageSets) {
            offset += imageSet.length + SIZEOF_NULL_BYTE
        }
    }

    for (p in planes) {
        p.objectsOffset = offset
        for (o in p.objects) {
            offset += WAP_WWD_OBJECT_DESCRIPTION_SIZE;
            offset += o.name.length + o.logic.length + o.imageSet.length + o.animation.length
        }
    }

    wwd.header.tileDescriptionsOffset = offset
    offset += 8 * SIZEOF_INT

    for (td in wwd.tileDescriptions) {
        offset += when (td.type) {
            WAP_TILE_TYPE_SINGLE -> 5 * SIZEOF_INT
            else -> 10 * SIZEOF_INT
        }
    }

    wwd.header.decompressedMainBlockSize = offset - WAP_WWD_HEADER_SIZE
}

fun dumpWwd(outputStream: OutputStream, wwd: Wwd) {
    val wwdOutputStream = WwdOutputStream(outputStream)
    val header = wwd.header

    calculateOffsets(wwd)
    dumpWwdHeader(wwdOutputStream, wwd)

    if (header.flags.compress) {
        val mainBlockStream = ByteArrayOutputStream()
        dumpMainBlock(WwdOutputStream(mainBlockStream), wwd)
        val mainBlockBuffer = mainBlockStream.toByteArray()
        val compressedMainBlockBuffer = compress(mainBlockBuffer)
        wwdOutputStream.writeBuffer(compressedMainBlockBuffer)
    } else {
        dumpMainBlock(wwdOutputStream, wwd)
    }
}

fun dumpWwdHeader(stream: WwdOutputStream, wwd: Wwd) {
    val header = wwd.header

    stream.writeInt(WAP_WWD_HEADER_SIZE)
    stream.writeInt(0)
    stream.writeInt(header.flags.dword)
    stream.writeInt(0)
    stream.writeStaticString(header.levelName, WwdHeader.levelNameBufferSize)
    stream.writeStaticString(header.author, WwdHeader.authorBufferSize)
    stream.writeStaticString(header.birth, WwdHeader.birthBufferSize)
    stream.writeStaticString(header.rezFile, WwdHeader.rezFileBufferSize)
    stream.writeStaticString(header.imageDir, WwdHeader.imageDirBufferSize)
    stream.writeStaticString(header.palRez, WwdHeader.palRezBufferSize)
    stream.writeInt(header.startX)
    stream.writeInt(header.startY)
    stream.writeInt(0)
    stream.writeInt(wwd.planes.size)
    stream.writeInt(WAP_WWD_HEADER_SIZE)
    stream.writeInt(header.tileDescriptionsOffset)
    stream.writeInt(header.decompressedMainBlockSize)
    stream.writeInt(header.checksum)
    stream.writeInt(0)
    stream.writeStaticString(header.launchApp, WwdHeader.launchAppBufferSize)
    stream.writeStaticString(header.imageSet1, WwdHeader.imageSetBufferSize)
    stream.writeStaticString(header.imageSet2, WwdHeader.imageSetBufferSize)
    stream.writeStaticString(header.imageSet3, WwdHeader.imageSetBufferSize)
    stream.writeStaticString(header.imageSet4, WwdHeader.imageSetBufferSize)
    stream.writeStaticString(header.prefix1, WwdHeader.prefixBufferSize)
    stream.writeStaticString(header.prefix2, WwdHeader.prefixBufferSize)
    stream.writeStaticString(header.prefix3, WwdHeader.prefixBufferSize)
    stream.writeStaticString(header.prefix4, WwdHeader.prefixBufferSize)
}

fun dumpMainBlock(stream: WwdOutputStream, wwd: Wwd) {
    dumpPlanes(stream, wwd.planes)
    dumpTileDescriptions(stream, wwd.tileDescriptions)
}

fun dumpPlanes(stream: WwdOutputStream, planes: List<WwdPlane>) {
    planes.forEach { p -> dumpPlaneHeader(stream, p) }
    planes.forEach { p -> dumpTiles(stream, p) }
    planes.forEach { p -> dumpImageSets(stream, p.imageSets) }
    planes.forEach { p -> dumpObjects(stream, p.objects) }
}

fun dumpPlaneHeader(stream: WwdOutputStream, plane: WwdPlane) {
    stream.writeInt(WAP_WWD_PLANE_DESCRIPTION_SIZE)
    stream.writeInt(0)
    stream.writeInt(plane.flags.dword)
    stream.writeInt(0)
    stream.writeStaticString(plane.name, WwdPlane.nameBufferSize)
    stream.writeInt(plane.tilesWide * tileWidth)
    stream.writeInt(plane.tilesHigh * tileWidth)
    stream.writeInt(plane.tileWidth)
    stream.writeInt(plane.tileHeight)
    stream.writeInt(plane.tilesWide)
    stream.writeInt(plane.tilesHigh)
    stream.writeInt(0)
    stream.writeInt(0)
    stream.writeInt(plane.movementXPercent)
    stream.writeInt(plane.movementYPercent)
    stream.writeInt(plane.fillColor)
    stream.writeInt(plane.imageSets.size)
    stream.writeInt(plane.objects.size)
    stream.writeInt(plane.tilesOffset)
    stream.writeInt(plane.imageSetsOffset)
    stream.writeInt(plane.objectsOffset)
    stream.writeInt(plane.zCoord)
    stream.writeInt(0)
    stream.writeInt(0)
    stream.writeInt(0)
}

fun dumpTiles(stream: WwdOutputStream, plane: WwdPlane) {
    var k = 0
    for (i in 0 until plane.tilesHigh) {
        for (j in 0 until plane.tilesWide) {
            val t = plane.tiles[k++]
            stream.writeInt(t)
        }
    }
}

fun dumpImageSets(stream: WwdOutputStream, imageSets: List<String>) {
    imageSets.forEach { stream.writeNullTerminatedString(it) }
}

fun dumpObjects(stream: WwdOutputStream, objects: List<WwdObject>) {
    objects.forEach { dumpObject(stream, it) }
}

fun dumpObject(stream: WwdOutputStream, obj: WwdObject) {
    stream.writeInt(obj.id)
    stream.writeInt(obj.name.length)
    stream.writeInt(obj.logic.length)
    stream.writeInt(obj.imageSet.length)
    stream.writeInt(obj.animation.length)

    stream.writeInt(obj.x)
    stream.writeInt(obj.y)
    stream.writeInt(obj.z)
    stream.writeInt(obj.i)
    stream.writeInt(obj.addFlags.dword)
    stream.writeInt(obj.dynamicFlags.dword)
    stream.writeInt(obj.drawFlags.dword)
    stream.writeInt(obj.userFlags.dword)
    stream.writeInt(obj.score)
    stream.writeInt(obj.points)
    stream.writeInt(obj.powerup)
    stream.writeInt(obj.damage)
    stream.writeInt(obj.smarts)
    stream.writeInt(obj.health)
    stream.writeRect(obj.moveRect)
    stream.writeRect(obj.hitRect)
    stream.writeRect(obj.attackRect)
    stream.writeRect(obj.clipRect)
    stream.writeRect(obj.userRect1)
    stream.writeRect(obj.userRect2)
    stream.writeInt(obj.userValue1)
    stream.writeInt(obj.userValue2)
    stream.writeInt(obj.userValue3)
    stream.writeInt(obj.userValue4)
    stream.writeInt(obj.userValue5)
    stream.writeInt(obj.userValue6)
    stream.writeInt(obj.userValue7)
    stream.writeInt(obj.userValue8)
    stream.writeInt(obj.xMin)
    stream.writeInt(obj.yMin)
    stream.writeInt(obj.xMax)
    stream.writeInt(obj.yMax)
    stream.writeInt(obj.speedX)
    stream.writeInt(obj.speedY)
    stream.writeInt(obj.xTweak)
    stream.writeInt(obj.yTweak)
    stream.writeInt(obj.counter)
    stream.writeInt(obj.speed)
    stream.writeInt(obj.width)
    stream.writeInt(obj.height)
    stream.writeInt(obj.direction)
    stream.writeInt(obj.faceDir)
    stream.writeInt(obj.timeDelay)
    stream.writeInt(obj.frameDelay)
    stream.writeInt(obj.objectType)
    stream.writeInt(obj.hitTypeFlags)
    stream.writeInt(obj.xMoveRes)
    stream.writeInt(obj.yMoveRes)

    stream.writeFixedString(obj.name)
    stream.writeFixedString(obj.logic)
    stream.writeFixedString(obj.imageSet)
    stream.writeFixedString(obj.animation)
}

fun dumpTileDescriptions(stream: WwdOutputStream, tileDescriptions: List<WwdTileDescription>) {
    stream.writeInt(32)
    stream.writeInt(0)
    stream.writeInt(tileDescriptions.size)
    stream.writeInt(0)
    stream.writeInt(0)
    stream.writeInt(0)
    stream.writeInt(0)
    stream.writeInt(0)

    tileDescriptions.forEach {
        dumpTileDescription(stream, it)
    }
}

fun dumpTileDescription(stream: WwdOutputStream, td: WwdTileDescription) {
    stream.writeInt(td.type)
    stream.writeInt(0)
    stream.writeInt(td.width)
    stream.writeInt(td.height)

    if (td.type == WAP_TILE_TYPE_SINGLE) {
        stream.writeInt(td.insideAttrib)
    } else {
        stream.writeInt(td.outsideAttr)
        stream.writeInt(td.insideAttrib)
        stream.writeRect(td.rect)
    }
}
