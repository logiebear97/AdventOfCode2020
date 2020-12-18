package com.logan.adventofcode2020

import java.io.File

var earliestTime = 0
var buses = mutableListOf<Int>()

fun main() {
    val inputFileName = "input/day13_input.txt"
    readNotes(inputFileName)

    val departureTimes = nextDepartureTimes()
    departureTimes.sortBy { it.second }
    val nextBus = departureTimes.first()
    println("${nextBus.first * (nextBus.second - earliestTime)}")
}

fun readNotes(name: String) {
    earliestTime = File(name).readLines().first().toInt()

    val busNumbers = File(name).readLines()[1].split(',')
    busNumbers.forEach {
        if (it != "x") {
            buses.add(it.toInt())
        }
    }
}

fun nextDepartureTimes(): MutableList<Pair<Int,Int>> {
    val departureTimes = mutableListOf<Pair<Int,Int>>()

    buses.forEach {
        departureTimes.add(Pair(it, getNextDepartureTime(it)))
    }

    return departureTimes
}

fun getNextDepartureTime(bus: Int): Int {
    var time = earliestTime

    while (time % bus != 0) { time++ }
    return time
}