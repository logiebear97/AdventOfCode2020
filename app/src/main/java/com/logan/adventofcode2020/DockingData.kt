package com.logan.adventofcode2020

import java.io.File
import kotlin.math.pow

var memory = mutableMapOf<Long, ULong>()
var currentMask = CharArray(64)
var program = listOf<String>()

val maskPattern = "[01X]+".toRegex()
val memPattern = """(\d+)""".toRegex()

fun main() {
    val inputFileName = "input/day14_input.txt"
    readInitializationProgramFromFile(inputFileName)

    runInitializationProgram()
    var sum = sumValuesInMemory()

    println("Sum: $sum")

    memory = mutableMapOf()
    runInitializationProgramV2()
    sum = sumValuesInMemory()

    println("Sum: $sum")
}

fun readInitializationProgramFromFile(name: String) {
    program = File(name).readLines()
}

fun runInitializationProgram() {
    program.forEach { line ->
        if (line.contains("mask")) {
            val mask = maskPattern.find(line)?.groups?.get(0)?.value
            if (mask != null) {
                currentMask = mask.toCharArray()
            }
        } else if (line.contains("mem")) {
            val matchResult = memPattern.findAll(line)
            val addressString = matchResult.first().value
            var valueString = matchResult.last().value
            if (addressString != null && valueString != null) {
                val address = addressString.toLong()
                var value = Integer.toBinaryString(valueString.toInt()).toCharArray().toMutableList()

                while (value.size != currentMask.size) {
                    value.add(0, '0')
                }

                for (i in value.indices) {
                    if (currentMask[i] != 'X') {
                        value[i] = currentMask[i]
                    }
                }

                memory[address] = value.joinToString("").toULong(2)
            }
        }
    }
}

fun runInitializationProgramV2() {
    program.forEach { line ->
        if (line.contains("mask")) {
            val mask = maskPattern.find(line)?.groups?.get(0)?.value
            if (mask != null) {
                currentMask = mask.toCharArray()
            }
        } else if (line.contains("mem")) {
            val matchResult = memPattern.findAll(line)
            val addressString = matchResult.first().value
            var valueString = matchResult.last().value
            if (addressString != null && valueString != null) {
                val address = Integer.toBinaryString(addressString.toInt()).toCharArray().toMutableList()
                var value = valueString.toULong()

                while (address.size != currentMask.size) {
                    address.add(0, '0')
                }

                var floatingIdx = mutableListOf<Int>()

                for (i in address.indices) {
                    when (currentMask[i]) {
                        'X' -> {
                            address[i] = 'X'
                            floatingIdx.add(i)
                        }
                        '1' -> address[i] = '1'
                    }
                }

                var subsets = generateSubsets(floatingIdx)
                subsets.forEach { subset ->
                    var floatingAddress = address.toMutableList()
                    subset.forEach { idx ->
                        floatingAddress[idx] = '1'
                    }

                    for (i in floatingAddress.indices) {
                        if (floatingAddress[i] == 'X') {
                            floatingAddress[i] = '0'
                        }
                    }

                    memory[floatingAddress.joinToString("").toLong(2)] = value
                }
            }
        }
    }
}

fun sumValuesInMemory(): ULong {
    var sum: ULong = 0u

    memory.forEach { value ->
        sum += value.value
    }

    return sum
}

fun generateSubsets(indices: List<Int>): List<List<Int>> {
    val length = indices.size
    var subsetList = mutableListOf<List<Int>>()

    for (i in 0 until 2.0.pow(length).toInt()) {
        var subset = mutableListOf<Int>()
        for (j in 0 until length) {
            if ((i and  1.shl(j)) > 0) {
                subset.add(indices[j])
            }
        }
        subsetList.add(subset)
    }

    return subsetList
}