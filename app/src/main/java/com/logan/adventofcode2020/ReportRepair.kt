package com.logan.adventofcode2020

import java.io.File

fun main() {
    val inputFileName = "input/day1_input.txt"
    val input = readInputFromFile(inputFileName)

    val twoIndices = findTwoEntriesThatSumTo(2020, input)
    println("$twoIndices")
    println("Product (2 Entries): ${input[twoIndices.first] * input[twoIndices.second]}\n")

    val threeIndices = findThreeEntriesThatSumTo(2020, input)
    println("$threeIndices")
    println("Product (3 Entries): ${input[threeIndices.first] * input[threeIndices.second] * input[threeIndices.third]}")
}

fun readInputFromFile(name: String): List<Int> {
    var input: MutableList<Int> = arrayListOf()
    File(name).forEachLine { input.add(it.toInt()) }
    return input
}

fun findTwoEntriesThatSumTo(value: Int, input: List<Int>): Pair<Int, Int> {
    var indices: Pair<Int, Int>? = null

    while (indices ==  null) {
        for (i in 0 until input.lastIndex) {
            for (j in i+1..input.lastIndex) {
                if (input[i] + input[j] == value) {
                    println("${input[i]} + ${input[j]} = ${input[i] + input[j]}")
                    indices = Pair(i, j)
                    break
                }
            }
            if (indices != null) { break }
        }
    }

    return indices
}

fun findThreeEntriesThatSumTo(value: Int, input: List<Int>): Triple<Int, Int, Int> {
    var indices: Triple<Int, Int, Int>? = null

    while (indices == null) {
        for (i in 0 until input.lastIndex-1) {
            for (j in i+1 until input.lastIndex) {
                for (k in j+1..input.lastIndex) {
                    if (input[i] + input[j] + input[k] == value) {
                        println("${input[i]} + ${input[j]} = ${input[i] + input[j] + input[k]}")
                        indices = Triple(i, j, k)
                        break
                    }
                }
                if (indices != null) { break }
            }
            if (indices != null) { break }
        }
    }

    return indices
}