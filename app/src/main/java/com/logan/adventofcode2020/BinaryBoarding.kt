package com.logan.adventofcode2020

import android.util.Range
import java.io.File
import kotlin.math.floor

val rowMin = 0
val rowMax = 127
val colMin = 0
val colMax = 8

class BoardingPass(val rawSeat: String): Comparable<BoardingPass> {
    var seatRow: Int = 0
    var seatCol: Int = 0
    var seatID: Int = 0

    init {
        calculateRow()
        calculateCol()
        calculateID()
    }

    private fun calculateRow() {
        val rawRow = rawSeat.subSequence(0, 7)

        var rowRange = 1..128
        rawRow.forEach {
            rowRange = narrowRange(rowRange, it)
        }

        seatRow = rowRange.first-1
    }

    private fun calculateCol() {
        val rawCol = rawSeat.subSequence(7, rawSeat.length)

        var colRange = 1..8
        rawCol.forEach {
            colRange = narrowRange(colRange, it)
        }

        seatCol = colRange.first-1
    }

    private fun calculateID() {
        seatID = seatRow * 8 + seatCol
    }

    private fun narrowRange(range: IntRange, region: Char): IntRange {
        val midpoint = (range.first + range.last)/2

        return when (region) {
            'F', 'L' -> range.first..midpoint
            'B', 'R' -> midpoint+1..range.last
            else -> return range
        }
    }

    override fun compareTo(other: BoardingPass): Int = seatID.compareTo(other.seatID)
}

fun main() {
    val inputFileName = "input/day5_input.txt"
    val passes = readBoardingPassesFromFile(inputFileName)
    passes.sortDescending()

    println("Largest ID: ${passes.first().seatID}")

    var seatIDs = mutableListOf<Int>()
    passes.forEach {
        seatIDs.add(it.seatID)
    }

    println("Seat ID: ${findSeatID(seatIDs)}")
}

fun readBoardingPassesFromFile(name: String): MutableList<BoardingPass> {
    var passes = mutableListOf<BoardingPass>()

    val lines = File(name).readLines()
    lines.forEach {
        passes.add(BoardingPass(it))
    }

    return passes
}

fun findSeatID(passes: MutableList<Int>): Int {
    var possibleIDs = mutableListOf<Int>()

    for (row in rowMin..rowMax) {
        for (col in colMin..colMax) {
            possibleIDs.add(row * 8 + col)
        }
    }

    val emptySeatIDs = possibleIDs.filter { !passes.contains(it) }
    return emptySeatIDs.find { it in passes.last()..passes.first() } ?: 0
}