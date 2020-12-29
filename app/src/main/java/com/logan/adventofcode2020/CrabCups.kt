package com.logan.adventofcode2020

import java.io.File
import java.util.*
import kotlin.time.*

var cupList = ArrayList<Cup>()
var cupIndices = mutableMapOf<Long, Int>()
lateinit var current: Cup
var min = 0L
var max = 0L

// Times
@ExperimentalTime
var addTimes = mutableListOf<Duration>()
@ExperimentalTime
var pickUps = mutableListOf<Duration>()
@ExperimentalTime
var calcDest = mutableListOf<Duration>()
@ExperimentalTime
val inserts = mutableListOf<Duration>()

data class Cup(var label: Long, var next: Cup? = null) {
    override fun toString(): String {
        return ("(label: $label, next: ${next?.label ?: "none"})")
    }
}

@ExperimentalTime
private fun ArrayList<Cup>.add(element: Long) {
    val addTime = measureTime {
        var tail = this.lastOrNull()
        val newCup = Cup(element)

        this.add(newCup)
        tail?.next = newCup

        cupIndices[element] = this.size-1
    }
    addTimes.add(addTime)
}

@ExperimentalTime
private fun ArrayList<Cup>.maxOrNull(): Long? {
    var max: Long?
    val calcTime = measureTime {
        if (this.size == 0) return null

        val values = this.map { it.label }
        max = values.maxOrNull()
    }

    println("Calculate max time: $calcTime")

    return max
}

@ExperimentalTime
private fun ArrayList<Cup>.minOrNull(): Long? {
    var min: Long?
    val calcTime = measureTime {
        if (this.size == 0) return null

        val values = this.map { it.label }
        min = values.minOrNull()
    }

    println("Calculate min time: $calcTime")

    return min
}

private fun ArrayList<Cup>.contains(element: Long): Boolean {
    val values = this.map { it.label }

    return values.contains(element)
}

private fun ArrayList<Cup>.indexOf(element: Long): Int {
    val values = this.map { it.label }

    return values.indexOf(element)
}

@ExperimentalTime
fun main() {
    val inputFileName = "input/day23_input.txt"
//    readCupsFromFile(inputFileName)
//
//    for (i in 1..10) {
//        move()
//    }

//    println("-- final --")
//    print("cups: ")
//    for (cup in cupList.indices) {
//        print("${cupList[cup]} ")
//    }
//    println()
//
    readCupsFromFile(inputFileName)

    val addTime = measureTime {
        addMoreCups(1000000)
    }
    println("Adding extra cups: $addTime")

    max = cupList.maxOrNull()!!
    min = cupList.minOrNull()!!

    val time = measureTime {
        for (i in 1..10000000) {
//            if (i % 10000 == 0) {
//                println("Move $i")
//                println("Average pick up time: ${averageTimes(pickUps)}")
//                println("Average calc destination time: ${averageTimes(calcDest)}")
//                println("Average insert time: ${averageTimes(inserts)}")
//                println()
//            }
            move()
        }
    }

    println("Average pick up time: ${averageTimes(pickUps)}")
    println("Average calc destination time: ${averageTimes(calcDest)}")
    println("Average insert time: ${averageTimes(inserts)}")
    println("Time: $time")

    val indexOf1 = cupList.indexOf(1)
    println("index of 1: $indexOf1")
    println("After 1: ${(cupList[indexOf1].next?.label ?: 1) * (cupList[indexOf1].next?.next?.label ?: 1)}")

}

@ExperimentalTime
fun readCupsFromFile(name: String) {
    val line = File(name).readLines().first()

    cupList = ArrayList()
    cupIndices = mutableMapOf()

    line.forEach { cup ->
        cupList.add(cup.toLong()-48)
    }
    cupList.last().next = cupList.first()
    current = cupList.first()

//    println("cups: $cupList")
}

@ExperimentalTime
fun addMoreCups(total: Int) {
    val currentMax = cupList.maxOrNull()!!
    for (label in currentMax+1..total) {
        cupList.add(label)
    }
    cupList.last().next = cupList.first()
}

@ExperimentalTime
fun move() {
    var p0: Cup
    var p1: Cup
    var p2: Cup
    var nextCup: Cup

    val pickUpTime = measureTime {
        p0 = current.next!!
        p1 = p0.next!!
        p2 = p1.next!!
        nextCup = p2.next!!
    }
    pickUps.add(pickUpTime)

    var destination: Long
    val calcDestinationTime = measureTime {
        destination = current.label
        do {
            destination--
            if (destination < min) {
                destination = max
            }
        } while (destination == p0.label || destination == p1.label || destination == p2.label)
    }
    calcDest.add(calcDestinationTime)

    val insertTime = measureTime {
        current.next = nextCup
        val destCup = cupList[cupIndices[destination]!!]
        p2.next = destCup.next
        destCup.next = p0

    }
    inserts.add(insertTime)

    current = nextCup
}

@ExperimentalTime
fun averageTimes(times: MutableList<Duration>): Duration {
    var totalTime = 0.toDuration(DurationUnit.MICROSECONDS)

    for (i in times.indices) {
        totalTime += times[i]
    }

    return (totalTime/times.size)
}