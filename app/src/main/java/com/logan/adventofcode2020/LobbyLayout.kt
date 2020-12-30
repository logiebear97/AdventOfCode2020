package com.logan.adventofcode2020

import java.io.File

typealias Address = Pair<Int, Int>

var tileDirections = mutableListOf<String>()
var tileArray = mutableMapOf<Address, Tile>()
var centerTile = Tile()

enum class Color {
    BLACK,
    WHITE;

    operator fun not(): Color {
        return when(this) {
            BLACK -> WHITE
            WHITE -> BLACK
        }
    }
}

private fun MutableMap<Address, Tile>.getTile(address: Address): Tile? {
    val addresses = this.keys

    val key = addresses.firstOrNull { it == address }

    return this[key]
}

private fun MutableList<String>.removeDuplicateOpposites() {
    val nwse = minOf(this.count { it == "nw"}, this.count { it == "se"})
    val nesw = minOf(this.count { it == "ne"}, this.count { it == "sw"})
    val ew = minOf(this.count { it == "e"}, this.count { it == "w"})

    for (d in 1..nwse) {
        this.remove("nw")
        this.remove("se")
    }

    for (d in 1..nesw) {
        this.remove("ne")
        this.remove("sw")
    }

    for (d in 1..ew) {
        this.remove("e")
        this.remove("w")
    }
}

private fun MutableList<String>.replaceDiagonals() {
    val nwsw = minOf(this.count { it == "nw" }, this.count { it == "sw" })
    val nese = minOf(this.count { it == "ne"}, this.count { it == "se" })

    for (d in 1..nwsw) {
        this.remove("nw")
        this.remove("sw")
        this.add("w")
    }

    for (d in 1..nese) {
        this.remove("ne")
        this.remove("se")
        this.add("e")
    }
}

private fun MutableList<String>.consolidateDiagonals() {
    val wne = minOf(this.count { it == "w" }, this.count { it == "ne" })
    val wse = minOf(this.count { it == "w" }, this.count { it == "se" })
    val enw = minOf(this.count { it == "e" }, this.count { it == "nw" })
    val esw = minOf(this.count { it == "e" }, this.count { it == "sw" })

    for (d in 1..wne) {
        this.remove("w")
        this.remove("ne")
        this.add("nw")
    }

    for (d in 1..wse) {
        this.remove("w")
        this.remove("se")
        this.add("sw")
    }

    for (d in 1..enw) {
        this.remove("e")
        this.remove("nw")
        this.add("ne")
    }

    for (d in 1..esw) {
        this.remove("e")
        this.remove("sw")
        this.add("se")
    }
}

data class Tile(var color: Color = Color.WHITE,
                var address: Address = Address(0,0)) {
}

fun main() {
    val inputFileName = "input/day24_input.txt"
    readTileDirectionsFromFile(inputFileName)

    tileArray[centerTile.address] = centerTile

    tileDirections.forEach {
        flipTile(it)
    }

    var blackTiles = tileArray.count { it.value.color == Color.BLACK }
    println("black tiles: $blackTiles")

    addMissingTiles()

    for (i in 1..100) {
        newDay()
        blackTiles = tileArray.count { it.value.color == Color.BLACK }
        println("Day $i: $blackTiles")
    }

}

fun readTileDirectionsFromFile(name: String) {
    tileDirections = File(name).readLines().toMutableList()
}

fun flipTile(directions: String) {
    var i = 0
    var allDirections = mutableListOf<String>()
    var address: Array<Int> = arrayOf(0,0)

    while (i < directions.length){
        var dir = directions[i].toString()
        if (dir == "n" || dir == "s") {
            dir += directions[++i]
        }

        allDirections.add(dir)
        i++
    }

    var shortenedDirections = allDirections.toMutableList()
    do {
        allDirections = shortenedDirections.toMutableList()
        shortenedDirections.replaceDiagonals()
        shortenedDirections.consolidateDiagonals()
        shortenedDirections.removeDuplicateOpposites()
        shortenedDirections.sort()
    } while (shortenedDirections != allDirections)

    shortenedDirections.forEach { dir ->
        when (dir) {
            "e" -> address[0] += 2
            "w" -> address[0] -= 2
            "ne" -> {
                address[1] += 1
                address[0] += 1
            }
            "se" -> {
                address[1] -= 1
                address[0] += 1
            }
            "nw" -> {
                address[1] += 1
                address[0] -= 1
            }
            "sw" -> {
                address[1] -= 1
                address[0] -= 1
            }
        }
    }

    val tileAddress = Address(address[0], address[1])
    val flipTile: Tile? = tileArray.getTile(tileAddress)

    if (flipTile != null) {
        flipTile.color = !flipTile.color
    } else {
        tileArray[tileAddress] = Tile(Color.BLACK, tileAddress)
    }
}

fun newDay() {
    var newTileArray = copyTileArray(tileArray)

    var blackTileArray = mutableMapOf<Address, Int>()
    var blackTileAddresses = newTileArray.filter { it.value.color == Color.BLACK }.keys
    blackTileAddresses.forEach { blackTileArray[it] = 0}

    var affectedTiles = blackTileArray.toMutableMap()

    for (addr in blackTileArray.keys) {
        var blackTiles = 0
        val currentEW = addr.first
        val currentNS = addr.second

        val neighborAddresses = arrayOf(
                Address(currentEW + 2, currentNS),
                Address(currentEW - 2, currentNS),
                Address(currentEW + 1, currentNS + 1),
                Address(currentEW + 1, currentNS - 1),
                Address(currentEW - 1, currentNS + 1),
                Address(currentEW - 1, currentNS - 1))

        neighborAddresses.forEach { nAddr ->
            affectedTiles[nAddr] = affectedTiles[nAddr]?.plus(1) ?: 1
        }
    }

    for (addr in affectedTiles.keys) {
        if (blackTileArray[addr] != null) {
            if (affectedTiles[addr] == 0 || affectedTiles[addr]!! > 2) {
                newTileArray[addr]!!.color = Color.WHITE
            }
        } else {
            if (affectedTiles[addr] == 2) {
                newTileArray[addr]!!.color = Color.BLACK
            }
        }
    }

    tileArray = newTileArray
}

fun addMissingTiles() {
    val bound = 150
    for (x in -bound..bound) {
        for (y in -bound..bound) {
            val addr = Address(x, y)
            if (tileArray[addr] == null) {
                tileArray[addr] = Tile(Color.WHITE, addr)
            }
        }
    }
}

fun copyTileArray(array: MutableMap<Address, Tile>): MutableMap<Address, Tile> {
    var copy = mutableMapOf<Address, Tile>()
    val iterator = array.iterator()

    while (iterator.hasNext()) {
        val tile = iterator.next()
        val copyTile = Tile(tile.value.color, tile.value.address)
        copy[copyTile.address] = copyTile
    }

    return copy
}