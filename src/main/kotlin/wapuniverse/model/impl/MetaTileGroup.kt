package wapuniverse.model.impl

import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.FXCollections.observableHashMap
import javafx.collections.ObservableMap
import javafx.collections.SetChangeListener
import org.fxmisc.easybind.EasyBind.combine
import wapuniverse.geom.Vec2i
import wapuniverse.model.MetaTile

class MetaTileGroup {
    val tilePosition = SimpleObjectProperty<Vec2i>(Vec2i())

    val metaTiles = SimpleObjectProperty<Map<Vec2i, MetaTile>>(mapOf())

    val metaTilesG = combine(tilePosition, metaTiles) { tilePosition, metaTiles ->
        metaTiles.mapKeys { (tileOffset, _) -> tilePosition + tileOffset }
    }
}

class MetaTileLayer {
    val tiles: ObservableMap<Vec2i, Int> = observableHashMap()

    val metaTileGroups = FXCollections.observableSet<MetaTileGroup>(
            MetaTileGroup().apply {
                metaTiles.value = mapOf(
                        Vec2i(4, 4) to MetaTile.BLOCK_TOP
                )
            },
            MetaTileGroup().apply {
                metaTiles.value = mapOf(
                        Vec2i(2, 2) to MetaTile.BLOCK_LEFT
                )
            }
    )!!

    init {
        initTilesTransformation()
    }

    private fun initTilesTransformation() {
        metaTileGroups.forEach { metaTileGroup ->
            updateTiles(metaTileGroup)
        }
        metaTileGroups.addListener { change: SetChangeListener.Change<out MetaTileGroup> ->
            if (change.wasAdded()) {
                val metaTileGroup = change.elementAdded
                updateTiles(metaTileGroup)
            }
        }
    }

    private fun updateTiles(metaTileGroup: MetaTileGroup) {
        metaTileGroup.metaTilesG.value.keys.forEach { offsetG ->
            updateTile(offsetG)
        }
        metaTileGroup.metaTilesG.addListener { _,
                                               oldOffsetsG,
                                               newOffsetsG ->
            val dirtyTileOffsetsG = oldOffsetsG.keys.union(newOffsetsG.keys)
            dirtyTileOffsetsG.forEach { offsetG ->
                updateTile(offsetG)
            }
        }
    }

    private fun updateTile(globalCoord: Vec2i) {
        val metaTiles = findMetaTilesAt(globalCoord)
        val newTileId = calculateTile(metaTiles)
        if (newTileId > 0) {
            tiles[globalCoord] = newTileId
        } else {
            tiles.remove(globalCoord)
        }
//        (0..5).forEach { i -> (0..5).forEach { j -> println("[$i, $j] = ${tiles[Vec2i(j, i)]}") } }
    }


    private fun findMetaTilesAt(coord: Vec2i): Set<MetaTile> {
        return metaTileGroups.mapNotNull {
            it.metaTilesG.value[coord]
        }.toSet()
    }
}

private fun calculateTile(metaTiles: Set<MetaTile>): Int {
    return when {
        metaTiles == setOf(MetaTile.BLOCK_TOP, MetaTile.BLOCK_LEFT) -> 74
        metaTiles == setOf(MetaTile.BLOCK_TOP) -> 304
        metaTiles == setOf(MetaTile.BLOCK_LEFT) -> 302
        metaTiles == setOf(MetaTile.LADDER_TOP) -> 310
        metaTiles == setOf(MetaTile.LADDER_MID) -> 311
        metaTiles == setOf(MetaTile.LADDER_BOTTOM) -> 312
        else -> -1
    }
}