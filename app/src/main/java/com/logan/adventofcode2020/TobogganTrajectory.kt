package com.logan.adventofcode2020

import java.io.File

enum class MapSpaces(val space: Char) {
    BLANK('.'),
    TREE('#')
}

val startPosition = Pair<Int, Int>(0, 0)
var currentPosition = startPosition

val movementPatterns: Array<Pair<Int, Int>> = arrayOf(
    Pair(1, 1),
    Pair(3, 1),
    Pair(5, 1),
    Pair(7, 1),
    Pair(1, 2)
)

fun main() {
    val inputFileName = "input/day3_input.txt"
    val input = read2DInputFromFile(inputFileName)

    val encounters = calculateTrajectory(input, movementPatterns[1])
    println("${encounters.second} trees encountered")

    println("Tree encounters product across all trajectories: ${calculateAllTrajectories(input)}")
}

fun read2DInputFromFile(name: String): MutableList<List<Char>> {
    var input = mutableListOf<List<Char>>()

    val lines = File(name).readLines()
    lines.forEach {
        val charList = it.toList()
        input.add(charList)
    }

    return input
}

fun calculateTrajectory(map: MutableList<List<Char>>, movementPattern: Pair<Int, Int>): Pair<Int, Int> {
    currentPosition = startPosition
    var numBlanks = 0
    var numTrees = 0

    val mapHeight = map.count()

    while (currentPosition.second < mapHeight) {
        val currentLine = map.get(currentPosition.second)
        when (currentLine[currentPosition.first]) {
            MapSpaces.BLANK.space -> numBlanks += 1
            MapSpaces.TREE.space -> numTrees += 1
        }

        if (currentPosition.second < mapHeight) {
            moveToNextPosition(currentLine.count(), movementPattern)
        }
    }

    return Pair(numBlanks, numTrees)
}

fun moveToNextPosition(gridWidth: Int, movementPattern: Pair<Int, Int>) {
    val newHPos = (currentPosition.first + movementPattern.first) % gridWidth
    currentPosition = Pair(newHPos, currentPosition.second + movementPattern.second)
}

fun calculateAllTrajectories(map: MutableList<List<Char>>): Long {
    var treeProduct: Long = 1

    movementPatterns.forEach {
        val encounters = calculateTrajectory(map, it)
        treeProduct *= encounters.second
    }

    return treeProduct
}
