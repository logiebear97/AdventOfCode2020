package com.logan.adventofcode2020

import java.io.File
import kotlin.math.pow

fun main() {
    val inputFileName = "input/day10_input.txt"
    val adapters = readAdaptersFromFile(inputFileName)
    val differences = calculateDifferences(adapters)

    println("Range: ${differences.count { it == 1 } * differences.count { it == 3 }}")

    val segmentedAdapters = getRequiredAndOptionalAdapters(adapters)
    println("Arrangements: ${countNumberOfArrangements(segmentedAdapters)}")
}

fun readAdaptersFromFile(name: String): List<Int> {
    val adapters = mutableListOf<Int>()
    val lines = File(name).readLines()

    lines.forEach { adapters.add(it.toInt()) }

    adapters.sort()
    adapters.add(0, 0)
    adapters.add(adapters.last() + 3)

    return adapters
}

fun calculateDifferences(adapters: List<Int>): List<Int> {
    val differences = mutableListOf<Int>()

    for (i in adapters.indices) {
        if (i == 0) {
            differences.add(adapters[i])
        } else {
            differences.add(adapters[i] - adapters[i - 1])
        }
    }

    return differences
}

fun getRequiredAndOptionalAdapters(adapters: List<Int>): List<List<Int>> {
    val segmentedAdapters = mutableListOf<List<Int>>()

    var segment = mutableListOf<Int>()
    for (i in adapters.indices) {
        if (i == 0 || i == adapters.lastIndex) {
            segmentedAdapters.add(listOf(adapters[i]))
            continue
        }

        segment.add(adapters[i])

        val previousJoltChange = adapters[i] - adapters[i-1]
        val joltChange = adapters[i+1] - adapters[i]
         if (joltChange == 3 || previousJoltChange == 3) {
            segmentedAdapters.add(segment)
            segment = mutableListOf()
        }
    }

    segmentedAdapters.forEach { println(it.toString()) }

    return segmentedAdapters
}

fun countNumberOfArrangements(segmentedAdapters: List<List<Int>>): Long {
    var arrangements: Long = 1

    for (i in segmentedAdapters.indices) {
        arrangements *= trib(segmentedAdapters[i].size)
    }

    return arrangements
}

fun trib(length: Int): Int {
    return when (length) {
        0 -> 0
        1 -> 1
        2 -> 2
        3 -> 4
        4 -> 7
        5 -> 13
        else -> trib(length-1) + trib(length-2) + trib(length-3)
    }
}