package com.logan.adventofcode2020

import java.io.File

var numbers = mutableListOf<Int>()
var lastTurn = mutableMapOf<Int,Int>()

fun main() {
    val inputFileName = "input/day15_input.txt"
    readStartingNumbers(inputFileName)

    var stop = 2020
    playGame(stop)

    println("${numbers[stop-1]}")

    stop = 30000000
    playGame(stop)

    println("${numbers[stop-1]}")
}

fun readStartingNumbers(name: String) {
    val lines = File(name).readLines()
    lines.forEach {
        numbers.add(it.toInt())
    }

    for (i in 0 until numbers.lastIndex) {
        lastTurn[numbers[i]] = i
    }
}

fun playGame(stop: Int) {
    while (numbers.size < stop) {
        val last = numbers.last()

        if (lastTurn[last] != null) {
            numbers.add(numbers.lastIndex - lastTurn[last]!!)
        } else {
            numbers.add(0)
        }

        lastTurn[last] = numbers.lastIndex-1

    }
}