package io.github.jwap32.v1

import com.google.common.io.ByteStreams
import com.google.common.primitives.Bytes
import java.io.ByteArrayInputStream
import java.io.InputStream

fun loadWwd(wwdStream: InputStream): Wwd {
    val wwdBuffer = ByteStreams.toByteArray(wwdStream)
    val headerBuffer = wwdBuffer.sliceArray(0 until WAP_WWD_HEADER_SIZE)
    val header = loadWwdHeader(ByteArrayInputStream(headerBuffer))
    if (header.flags.compress) {
        val mainBlockBuffer = decompress(wwdBuffer, header.mainBlockOffset)
        val wwdBuffer2 = Bytes.concat(headerBuffer, mainBlockBuffer)
        return loadMainBlock(header, wwdBuffer2)
    } else {
        return loadMainBlock(header, wwdBuffer)
    }
}

fun loadWwdHeader(inputStream: InputStream): WwdHeader {
    val stream = WwdInputStream(inputStream)
    val header = WwdHeader()

    stream.expectInt(WAP_WWD_HEADER_SIZE);
    stream.expectInt(0)
    header.flags.dword = stream.readInt()
    stream.expectInt(0)
    header.levelName = stream.readStaticString(WwdHeader.levelNameBufferSize)
    header.author = stream.readStaticString(WwdHeader.authorBufferSize)
    header.birth = stream.readStaticString(WwdHeader.birthBufferSize)
    header.rezFile = stream.readStaticString(WwdHeader.rezFileBufferSize)
    header.imageDir = stream.readStaticString(WwdHeader.imageDirBufferSize)
    header.palRez = stream.readStaticString(WwdHeader.palRezBufferSize)
    header.startX = stream.readInt()
    header.startY = stream.readInt()
    val unknown0 = stream.readInt()
    header.planeCount = stream.readInt()
    header.mainBlockOffset = stream.readInt()
    header.tileDescriptionsOffset = stream.readInt()
    header.decompressedMainBlockSize = stream.readInt()
    header.checksum = stream.readInt()
    val unknown1 = stream.readInt()
    header.launchApp = stream.readStaticString(WwdHeader.launchAppBufferSize)
    header.imageSet1 = stream.readStaticString(WwdHeader.imageSetBufferSize)
    header.imageSet2 = stream.readStaticString(WwdHeader.imageSetBufferSize)
    header.imageSet3 = stream.readStaticString(WwdHeader.imageSetBufferSize)
    header.imageSet4 = stream.readStaticString(WwdHeader.imageSetBufferSize)
    header.prefix1 = stream.readStaticString(WwdHeader.prefixBufferSize)
    header.prefix2 = stream.readStaticString(WwdHeader.prefixBufferSize)
    header.prefix3 = stream.readStaticString(WwdHeader.prefixBufferSize)
    header.prefix4 = stream.readStaticString(WwdHeader.prefixBufferSize)

    return header
}

fun loadMainBlock(header: WwdHeader, wwdBuffer: ByteArray): Wwd {
    val wwd = Wwd()
    wwd.header = header
    loadPlanes(wwdBuffer, wwd)
    loadTileDescriptions(wwdBuffer, wwd)
    return wwd
}

fun loadPlanes(wwdBuffer: ByteArray, wwd: Wwd) {
    subStream(wwdBuffer, wwd.header.mainBlockOffset).use { stream ->
        wwd.planes = (1..wwd.header.planeCount).map {
            loadPlaneHeader(stream)
        }.toMutableList()
        wwd.planes.forEach { loadTiles(stream, it) }
        wwd.planes.forEach { loadImageSets(stream, it) }
        wwd.planes.forEach { loadObjects(stream, it) }
    }
}

fun loadPlaneHeader(stream: WwdInputStream): WwdPlane {
    val plane = WwdPlane()

    stream.expectInt(WAP_WWD_PLANE_DESCRIPTION_SIZE)
    stream.expectInt(0)
    plane.flags.dword = stream.readInt()
    stream.expectInt(0)
    plane.name = stream.readStaticString(WwdPlane.nameBufferSize)
    val widthPx = stream.readInt()
    val heightPx = stream.readInt()
    plane.tileWidth = stream.readInt()
    plane.tileHeight = stream.readInt()
    plane.tilesWide = stream.readInt()
    plane.tilesHigh = stream.readInt()
    plane.tiles = IntArray(plane.tilesWide * plane.tilesHigh) { -1 }
    stream.expectInt(0)
    stream.expectInt(0)
    plane.movementXPercent = stream.readInt()
    plane.movementYPercent = stream.readInt()
    plane.fillColor = stream.readInt()
    plane.imageSetCount = stream.readInt()
    plane.objectCount = stream.readInt()
    plane.tilesOffset = stream.readInt()
    plane.imageSetsOffset = stream.readInt()
    plane.objectsOffset = stream.readInt()
    plane.zCoord = stream.readInt()
    stream.expectInt(0)
    stream.expectInt(0)
    stream.expectInt(0)

    return plane
}

fun loadTiles(stream: WwdInputStream, plane: WwdPlane) {
    for (i in 0..plane.tilesHigh - 1) {
        for (j in 0..plane.tilesWide - 1) {
            val t = stream.readInt()
            plane.setTile(i, j, t)
        }
    }
}

fun loadImageSets(stream: WwdInputStream, plane: WwdPlane) {
    plane.imageSets = (1..plane.imageSetCount).map {
        stream.readNullTerminatedString()
    }.toMutableList()
}

fun loadObjects(stream: WwdInputStream, plane: WwdPlane) {
    plane.objects = (1..plane.objectCount).map {
        loadObject(stream)
    }.toMutableList()
}

fun loadObject(stream: WwdInputStream): WwdObject {
    val obj = WwdObject()

    obj.id = stream.readInt()
    val nameLen = stream.readInt()
    val logicLen = stream.readInt()
    val imageSetLen = stream.readInt()
    val animationLen = stream.readInt()

    obj.x = stream.readInt()
    obj.y = stream.readInt()
    obj.z = stream.readInt()
    obj.i = stream.readInt()
    obj.addFlags.dword = stream.readInt()
    obj.dynamicFlags.dword = stream.readInt()
    obj.drawFlags.dword = stream.readInt()
    obj.userFlags.dword = stream.readInt()
    obj.score = stream.readInt()
    obj.points = stream.readInt()
    obj.powerup = stream.readInt()
    obj.damage = stream.readInt()
    obj.smarts = stream.readInt()
    obj.health = stream.readInt()
    obj.moveRect = stream.readRect()
    obj.hitRect = stream.readRect()
    obj.attackRect = stream.readRect()
    obj.clipRect = stream.readRect()
    obj.userRect1 = stream.readRect()
    obj.userRect2 = stream.readRect()
    obj.userValue1 = stream.readInt()
    obj.userValue2 = stream.readInt()
    obj.userValue3 = stream.readInt()
    obj.userValue4 = stream.readInt()
    obj.userValue5 = stream.readInt()
    obj.userValue6 = stream.readInt()
    obj.userValue7 = stream.readInt()
    obj.userValue8 = stream.readInt()
    obj.xMin = stream.readInt()
    obj.yMin = stream.readInt()
    obj.xMax = stream.readInt()
    obj.yMax = stream.readInt()
    obj.speedX = stream.readInt()
    obj.speedY = stream.readInt()
    obj.xTweak = stream.readInt()
    obj.yTweak = stream.readInt()
    obj.counter = stream.readInt()
    obj.speed = stream.readInt()
    obj.width = stream.readInt()
    obj.height = stream.readInt()
    obj.direction = stream.readInt()
    obj.faceDir = stream.readInt()
    obj.timeDelay = stream.readInt()
    obj.frameDelay = stream.readInt()
    obj.objectType = stream.readInt()
    obj.hitTypeFlags = stream.readInt()
    obj.xMoveRes = stream.readInt()
    obj.yMoveRes = stream.readInt()

    obj.name = stream.readFixedString(nameLen)
    obj.logic = stream.readFixedString(logicLen)
    obj.imageSet = stream.readFixedString(imageSetLen)
    obj.animation = stream.readFixedString(animationLen)

    return obj
}

fun loadTileDescriptions(wwdBuffer: ByteArray, wwd: Wwd) {
    subStream(wwdBuffer, wwd.header.tileDescriptionsOffset).use { stream ->
        stream.expectInt(32)
        stream.expectInt(0)
        val numTileDescriptions = stream.readInt()
        stream.expectInt(0)
        stream.expectInt(0)
        stream.expectInt(0)
        stream.expectInt(0)
        stream.expectInt(0)

        wwd.tileDescriptions = (1..numTileDescriptions).map {
            loadTileDescription(stream)
        }.toMutableList()
    }
}

fun loadTileDescription(stream: WwdInputStream): WwdTileDescription {
    val td = WwdTileDescription()
    td.type = stream.readInt()
    val unknown = stream.readInt()
    td.width = stream.readInt()
    td.height = stream.readInt()

    if (td.type == WAP_TILE_TYPE_SINGLE) {
        td.insideAttrib = stream.readInt()
    } else {
        td.outsideAttr = stream.readInt()
        td.insideAttrib = stream.readInt()
        td.rect = stream.readRect()
    }

    return td
}
