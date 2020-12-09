package com.logan.adventofcode2020

import java.io.File

val preambleLength = 25

fun main() {
    val inputFileName = "input/day9_input.txt"
    val numbers = readCipherFromFile(inputFileName)

    val firstInvalid = findFirstInvalidNumber(numbers)
    println("First invalid: $firstInvalid")

    val sublist = findContiguousRangeForInvalid(firstInvalid, numbers)
    println("Weakness: ${(sublist.minOrNull() ?: 0) + (sublist.maxOrNull() ?: 0)}")
}

fun readCipherFromFile(name: String): List<Long> {
    var numbers = mutableListOf<Long>()
    val lines = File(name).readLines()

    lines.forEach { numbers.add(it.toLong())  }

    return numbers
}

fun findFirstInvalidNumber(numbers: List<Long>): Long {
    var numberPtr = 25

    numbers.subList(preambleLength, numbers.size).forEach {
        if (!checkForSum(it, numbers.subList(numberPtr-preambleLength, numberPtr))) {
            return it
        }
        numberPtr++
    }

    return -1
}

fun checkForSum(number: Long, preamble: List<Long>): Boolean {
    for (i in 0 until preambleLength-1) {
        for (j in i+1 until preambleLength) {
            if (preamble[i] + preamble[j] == number) {
                return true
            }
        }
    }

    return false
}

fun findContiguousRangeForInvalid(invalid: Long, numbers: List<Long>): List<Long> {
    val invalidIndex = numbers.indexOf(invalid)
    val subList = numbers.subList(0, invalidIndex)

    var lowerBound = 0
    var upperBound = 1
    var currentSum = subList.subList(lowerBound, upperBound+1).sum()

    while (currentSum != invalid) {
        if (currentSum > invalid) {
            lowerBound += 1
        } else if (currentSum < invalid) {
            upperBound += 1
        }

        currentSum = subList.subList(lowerBound, upperBound+1).sum()
    }

    return subList.subList(lowerBound, upperBound+1)
}
