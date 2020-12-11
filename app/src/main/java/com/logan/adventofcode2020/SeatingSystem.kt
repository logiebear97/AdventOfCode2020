package com.logan.adventofcode2020

import java.io.File
import java.lang.Integer.min

var layout = mutableListOf<MutableList<Char>>()
var iterations = 0

enum class Seating(val symbol: Char) {
    EMPTY('L'),
    OCCUPIED('#'),
    FLOOR('.')
}

fun main() {
    val inputFileName = "input/day11_input.txt"

    readSeatLayoutFromFile(inputFileName)
    stabilizeLayout()
    var occupied = countOccupied()

    println("Occupied: $occupied")
    println("Iterations: $iterations")

    readSeatLayoutFromFile(inputFileName)
    stabilizeLayoutV2()
    occupied = countOccupied()

    println("Occupied: $occupied")
    println("Iterations: $iterations")
}

fun readSeatLayoutFromFile(name: String) {
    val lines = File(name).readLines()
    layout = mutableListOf<MutableList<Char>>()

    lines.forEach {
        layout.add(it.toMutableList())
    }
}

fun stabilizeLayout() {
    var oldLayout: List<List<Char>>
    iterations = 0

    do {
        oldLayout = cloneLayout()

        for (y in oldLayout.indices) {
            for (x in oldLayout[y].indices) {
                layout[y][x] = changeSeatStatus(oldLayout, x, y)
            }
        }
        iterations++
    } while (layout != oldLayout)
}

fun cloneLayout(): MutableList<MutableList<Char>> {
    var copy = mutableListOf<MutableList<Char>>()

    layout.forEach {
        copy.add(it.toMutableList())
    }

    return copy
}

/*

(x-1,y-1)   (x,y-1)    (x+1,y-1)

(x-1,y)      (x,y)      (x+1,y)

(x-1,y+1)   (x,y+1)    (x+1,y+1)

 */

fun changeSeatStatus(oldLayout: List<List<Char>>, x: Int, y: Int): Char {
    return when (oldLayout[y][x]) {
        Seating.EMPTY.symbol -> emptySeat(oldLayout, x, y)
        Seating.OCCUPIED.symbol -> occupiedSeat(oldLayout, x, y)
        Seating.FLOOR.symbol -> return Seating.FLOOR.symbol
        else -> return oldLayout[y][x]
    }
}

fun emptySeat(oldLayout: List<List<Char>>, x: Int, y: Int): Char {
    for (x1 in x-1..x+1) {
        if ((x == 0 && x1 == x-1) || (x == oldLayout[y].lastIndex && x1 == x+1)) {
            continue
        }
        for (y1 in y-1..y+1) {
            if ((y == 0 && y1 == y-1) || (y == oldLayout.lastIndex && y1 == y+1) || (x == x1 && y == y1)) {
                continue
            }
            if (oldLayout[y1][x1] == Seating.OCCUPIED.symbol) { return Seating.EMPTY.symbol }
        }
    }

    return Seating.OCCUPIED.symbol
}

fun occupiedSeat(oldLayout: List<List<Char>>, x: Int, y: Int): Char {
    var adjacentOccupied = 0

    for (x1 in x-1..x+1) {
        if ((x == 0 && x1 == x-1) || (x == oldLayout[y].lastIndex && x1 == x+1)) {
            continue
        }
        for (y1 in y-1..y+1) {
            if ((y == 0 && y1 == y-1) || (y == oldLayout.lastIndex && y1 == y+1) || (x == x1 && y == y1)) {
                continue
            }
            if (oldLayout[y1][x1] == Seating.OCCUPIED.symbol) {
                adjacentOccupied++
            }
        }
    }

    return if (adjacentOccupied >= 4) {
        Seating.EMPTY.symbol
    } else {
        Seating.OCCUPIED.symbol
    }
}

fun countOccupied(): Int {
    var occupied = 0

    layout.forEach {
        occupied += it.count { char -> char == Seating.OCCUPIED.symbol }
    }

    return occupied
}

fun stabilizeLayoutV2() {
    var oldLayout: List<List<Char>>
    iterations = 0

    do {
        oldLayout = cloneLayout()

        for (y in oldLayout.indices) {
            for (x in oldLayout[y].indices) {
                layout[y][x] = changeSeatStatusV2(oldLayout, x, y)
            }
        }
        iterations++
    } while (layout != oldLayout)
}

fun changeSeatStatusV2(oldLayout: List<List<Char>>, x: Int, y: Int): Char {
    val visibleSeats = findVisibleSeats(oldLayout, x, y)

    when (oldLayout[y][x]) {
        Seating.EMPTY.symbol -> {
            return if (visibleSeats.count { it == Seating.OCCUPIED.symbol } == 0) {
                Seating.OCCUPIED.symbol
            } else {
                Seating.EMPTY.symbol
            }
        }
        Seating.OCCUPIED.symbol -> {
            return if (visibleSeats.count { it == Seating.OCCUPIED.symbol } >= 5) {
                Seating.EMPTY.symbol
            } else {
                Seating.OCCUPIED.symbol
            }
        }
        Seating.FLOOR.symbol -> return Seating.FLOOR.symbol
        else -> return oldLayout[y][x]
    }
}

fun findVisibleSeats(oldLayout: List<List<Char>>, x: Int, y: Int): List<Char> {
    val visibleSeats = mutableListOf<Char>()

    //North (x, y-n) y != 0
    if (y != 0) {
        for (n in 1..y) {
            val seat = oldLayout[y-n][x]
            if (seat != Seating.FLOOR.symbol) {
                visibleSeats.add(seat)
                break
            }
        }
    }

    //North-East (x+n, y-n) y != 0
    if (y != 0) {
        for (n in 1..min(oldLayout[y].lastIndex-x, y)) {
            val seat = oldLayout[y-n][x+n]
            if (seat != Seating.FLOOR.symbol) {
                visibleSeats.add(seat)
                break
            }
        }
    }

    //East (x+n, y) x != max
    if (x != oldLayout[y].lastIndex) {
        for (n in 1..oldLayout[y].lastIndex-x) {
            val seat = oldLayout[y][x+n]
            if (seat != Seating.FLOOR.symbol) {
                visibleSeats.add(seat)
                break
            }
        }
    }

    //South-East (x+n, y+n) x != max, y != max
    if (x != oldLayout[y].lastIndex && y != oldLayout.lastIndex) {
        for (n in 1..min(oldLayout[y].lastIndex-x, oldLayout.lastIndex-y)) {
            val seat = oldLayout[y+n][x+n]
            if (seat != Seating.FLOOR.symbol) {
                visibleSeats.add(seat)
                break
            }
        }
    }

    //South (x, y+n) y != max
    if (y != oldLayout.lastIndex) {
        for (n in 1..oldLayout.lastIndex-y) {
            val seat = oldLayout[y+n][x]
            if (seat != Seating.FLOOR.symbol) {
                visibleSeats.add(seat)
                break
            }
        }
    }

    //South-West (x-n, y+n) x != 0, y != max
    if (x != 0 && y != oldLayout.lastIndex) {
        for (n in 1..min(x, oldLayout.lastIndex-y)) {
            val seat = oldLayout[y+n][x-n]
            if (seat != Seating.FLOOR.symbol) {
                visibleSeats.add(seat)
                break
            }
        }
    }

    //West (x-n, y) x != 0
    if (x != 0) {
        for (n in 1..x) {
            val seat = oldLayout[y][x-n]
            if (seat != Seating.FLOOR.symbol) {
                visibleSeats.add(seat)
                break
            }
        }
    }

    //North-West (x-n, y-n) x != 0, y != 0
    if (x != 0 && y != 0) {
        for (n in 1..min(x, y)) {
            val seat = oldLayout[y-n][x-n]
            if (seat != Seating.FLOOR.symbol) {
                visibleSeats.add(seat)
                break
            }
        }
    }

    return visibleSeats
}