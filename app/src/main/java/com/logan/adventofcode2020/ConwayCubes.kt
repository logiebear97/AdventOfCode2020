package com.logan.adventofcode2020

import java.io.File

typealias Coord = Triple<Int, Int, Int>

class Quad(val x: Int = 0, val y: Int = 0, val z: Int = 0, val w: Int = 0) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Quad

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false
        if (w != other.w) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        result = 31 * result + w
        return result
    }
}

var grid = mutableMapOf<Int, MutableMap<Int, Char>>()
var cube = mutableSetOf<Coord>()
var hyperCube = mutableSetOf<Quad>()

val cycles = 6

fun main() {
    val inputFileName = "input/day17_input.txt"
    readInitialStateFromFile(inputFileName)
    setupCube()
    setupHyperCube()

    for (i in 1..cycles) {
        cycle()
        cycle(true)
    }

    println("Active (Cube): ${cube.size}")
    println("Active (Hyper): ${hyperCube.size}")
}

fun readInitialStateFromFile(name: String) {
    val lines = File(name).readLines()

    for (y in lines.indices) {
        grid[y] = mutableMapOf()
        for (x in lines[y].indices) {
             grid[y]!![x] = lines[y][x]
        }
    }
}

fun setupCube() {
    for (x in grid) {
        for (y in x.value) {
            if (y.value == '#') cube.add(Coord(x.key, y.key, 0))
        }
    }
}

fun setupHyperCube() {
    for (x in grid) {
        for (y in x.value) {
            if (y.value == '#') hyperCube.add(Quad(x.key, y.key, 0, 0))
        }
    }
}

fun neighbors(cell: Coord): MutableList<Coord> {
    var neighbors = mutableListOf<Coord>()
    for (x in cell.first-1..cell.first+1) {
        for (y in cell.second-1..cell.second+1) {
            for (z in cell.third-1..cell.third+1) {
                neighbors.add(Coord(x, y, z))
            }
        }
    }

    return neighbors
}

fun neighbors(cell: Quad): MutableList<Quad> {
    var neighbors = mutableListOf<Quad>()
    for (x in cell.x-1..cell.x+1) {
        for (y in cell.y-1..cell.y+1) {
            for (z in cell.z-1..cell.z+1) {
                for (w in cell.w - 1..cell.w + 1) {
                    neighbors.add(Quad(x, y, z, w))
                }
            }
        }
    }

    return neighbors
}

fun aliveNeighbors(cell: Coord): Int {
    var alive = 0
    for (n in neighbors(cell)) {
        if (cube.contains(n)) alive++
    }

    if (cell in cube) alive--

    return alive
}

fun aliveNeighbors(cell: Quad): Int {
    var alive = 0
    for (n in neighbors(cell)) {
        if (hyperCube.contains(n)) alive++
    }

    if (cell in hyperCube) alive--

    return alive
}

fun allNeighbors(): MutableSet<Coord> {
    var neighbors = mutableSetOf<Coord>()
    for (cell in cube) {
        for (n in neighbors(cell)) {
            neighbors.add(n)
        }
    }

    return neighbors
}

fun allHyperNeighbors(): MutableSet<Quad> {
    var neighbors = mutableSetOf<Quad>()
    for (cell in hyperCube) {
        for (n in neighbors(cell)) {
            neighbors.add(n)
        }
    }

    return neighbors
}

fun cycle(hyper: Boolean = false) {
    if (hyper) {
        var newCube = mutableSetOf<Quad>()

        for (c in allHyperNeighbors()) {
            var alive = aliveNeighbors(c)

            if (alive == 3 || (alive == 2 && hyperCube.contains(c))) {
                newCube.add(c)
            }
        }

        hyperCube = newCube

    } else {
        var newCube = mutableSetOf<Coord>()

        for (c in allNeighbors()) {
            var alive = aliveNeighbors(c)

            if (alive == 3 || (alive == 2 && cube.contains(c))) {
                newCube.add(c)
            }
        }

        cube = newCube
    }
}