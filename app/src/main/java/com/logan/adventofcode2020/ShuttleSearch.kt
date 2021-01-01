package com.logan.adventofcode2020

import java.io.File
import kotlin.math.floor

var earliestTime = 0L
var buses = mutableListOf<Long>()
var busList = mutableListOf<String>()

fun main() {
    val inputFileName = "input/day13_input.txt"
    readNotes(inputFileName)

    val departureTimes = nextDepartureTimes()
    departureTimes.sortBy { it.second }
    val nextBus = departureTimes.first()
    println("${nextBus.first * (nextBus.second - earliestTime)}")

    calculateDeltas()
}

fun readNotes(name: String) {
    earliestTime = File(name).readLines().first().toLong()

    val busNumbers = File(name).readLines()[1].split(',')
    busNumbers.forEach {
        busList.add(it)
        if (it != "x") {
            buses.add(it.toLong())
        }
    }
}

fun nextDepartureTimes(): MutableList<Pair<Long,Long>> {
    val departureTimes = mutableListOf<Pair<Long,Long>>()

    buses.forEach {
        departureTimes.add(Pair(it, getNextDepartureTime(it)))
    }

    return departureTimes
}

fun getNextDepartureTime(bus: Long): Long {
    var time = earliestTime

    while (time % bus != 0L) { time++ }
    return time
}

fun calculateDeltas() {
    var busDeltas = mutableMapOf<Long, Long>()
    for (i in busList.indices) {
        if (busList[i] != "x") {
            busDeltas[i.toLong()] = busList[i].toLong()
        }
    }

    var t = busDeltas[0]!!
    var step = busDeltas[0]!!

    for (bus in busDeltas) {
        while (((t + bus.key) % bus.value) != 0L) {
            t += step
        }
        step = lcm(step, bus.value)
    }

    println("t = $t")
}

fun lcm(a: Long, b: Long): Long {
    return floor((a * b).div(gcd(a, b).toDouble())).toLong()
}

fun gcd(first: Long, second: Long): Long {
    var a = first
    var b = second

    while (b > 0) {
        var t = b
        b = a % b
        a = t
    }

    return a
}