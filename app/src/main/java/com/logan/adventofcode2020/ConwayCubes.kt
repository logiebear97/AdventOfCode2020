package com.logan.adventofcode2020

import java.io.File

var pocketDimension = mutableMapOf<Int, MutableMap<Int, MutableMap<Int, MutableMap<Int, Boolean>>>>()

val cycles = 1

fun main() {
    val inputFileName = "input/day17_smallExample.txt"
    readInitialStateFromFile(inputFileName)
    printPocketDimension()

    simulateCycles(cycles)
    println("Active: ${calculateActiveCubes()}")
}

fun readInitialStateFromFile(name: String) {
    val lines = File(name).readLines()

    pocketDimension[0] = mutableMapOf()
    pocketDimension[0]!![0] = mutableMapOf()

    for (y in lines.indices) {
        pocketDimension[0]!![0]!![y] = mutableMapOf()
        for (x in lines[y].indices) {
            when (lines[y][x]) {
                '#' -> pocketDimension[0]!![0]!![y]!![x] = true
                '.' -> pocketDimension[0]!![0]!![y]!![x] = false
            }
        }
    }
}

fun copyPocketDimension(): MutableMap<Int, MutableMap<Int, MutableMap<Int, MutableMap<Int, Boolean>>>> {
    expandPocketDimension()

    var dimension = mutableMapOf<Int, MutableMap<Int, MutableMap<Int, MutableMap<Int, Boolean>>>>()

    for (w in pocketDimension.keys) {
        dimension[w] = mutableMapOf()
        for (z in pocketDimension[w]!!.keys) {
            dimension[w]!![z] = mutableMapOf()
            for (y in pocketDimension[w]!![z]!!.keys) {
                dimension[w]!![z]!![y] = mutableMapOf()
                for (x in pocketDimension[w]!![z]!![y]!!.keys) {
                    dimension[w]!![z]!![y]!![x] = pocketDimension[w]!![z]!![y]!![x]!!
                }
            }
        }
    }

    return dimension
}

fun expandPocketDimension() {
    val downW = pocketDimension.keys.minOrNull()!!-1
    val upW = pocketDimension.keys.maxOrNull()!!+1
    val downZ = pocketDimension[0]!!.keys.minOrNull()!!-1
    val upZ = pocketDimension[0]!!.keys.maxOrNull()!!+1
    val downY = pocketDimension[0]!![0]!!.keys.minOrNull()!!-1
    val upY = pocketDimension[0]!![0]!!.keys.maxOrNull()!!+1
    val downX = pocketDimension[0]!![0]!![0]!!.keys.minOrNull()!!-1
    val upX = pocketDimension[0]!![0]!![0]!!.keys.maxOrNull()!!+1

    for (w in downW..upW) {
        if (pocketDimension[w] == null) {
            pocketDimension[w] = mutableMapOf()
        }
        for (z in downZ..upZ) {
            if (pocketDimension[w]!![z] == null) {
                pocketDimension[w]!![z] = mutableMapOf()
            }
            for (y in downY..upY) {
                if (pocketDimension[w]!![z]!![y] == null) {
                    pocketDimension[w]!![z]!![y] = mutableMapOf()
                }
                for (x in downX..upX) {
                    if (pocketDimension[w]!![z]!![y]!![x] == null) {
                        pocketDimension[w]!![z]!![y]!![x] = false
                    }
                }
            }
        }
    }
}

fun simulateCycles(numCycles: Int) {
    for (i in 1..numCycles) {
        completeCycle()

        println("Cycle $i")
        printPocketDimension()
    }
}

fun completeCycle() {
    var activeNeighbors = 0
    var copyDimension = copyPocketDimension()
    printPocketDimension()

    println("----------------------")
//    pocketDimension = copyDimension
    printPocketDimension()

    for (w in pocketDimension.keys) {
        for (z in pocketDimension.keys) {
            for (y in pocketDimension[z]!!.keys) {
                for (x in pocketDimension[z]!![y]!!.keys) {
                    activeNeighbors = 0

                    for (wI in w-1..w+1) {
                        for (zI in z-1..z+1) {
                            for (yI in y-1..y+1) {
                                for (xI in x-1..x+1) {
                                    if (xI == x && yI == y && zI == z && wI == w) {
                                        continue
                                    }
                                    if (pocketDimension[wI]?.get(zI)?.get(yI)?.get(xI) == true) activeNeighbors++
                                }
                            }
                        }
                    }

                    when (pocketDimension[w]!![z]!![y]!![x]!!) {
                        true -> {
                            if (activeNeighbors != 2 || activeNeighbors != 3) {
                                copyDimension[w]!![z]!![y]!![x] = false
                            }
                        }
                        false -> {
                            if (activeNeighbors == 3) {
                                copyDimension[w]!![z]!![y]!![x] = true
                            }
                        }
                    }

                }
            }
        }
    }

    pocketDimension = copyDimension
}

fun calculateActiveCubes(): Int {
    var active = 0

    for (w in pocketDimension.keys) {
        for (z in pocketDimension[w]!!.keys) {
            for (y in pocketDimension[w]!![z]!!.keys) {
                for (x in pocketDimension[w]!![z]!![y]!!.keys) {
                    if (pocketDimension[w]!![z]!![y]!![x]!!) active++
                }
            }
        }
    }

    return active
}

fun printPocketDimension() {
    for (w in pocketDimension.keys) {
        for (z in pocketDimension[w]!!.keys) {
            println("w = $w, z = $z")
            for (y in pocketDimension[w]!![z]!!.keys) {
                for (x in pocketDimension[w]!![z]!![y]!!.keys) {
                    when (pocketDimension[w]!![z]!![y]!![x]!!) {
                        true -> print('#')
                        false -> print('.')
                    }
                }
                println()
            }
            println()
        }
    }
}